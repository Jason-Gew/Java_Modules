package gew.zookeeper.client;


import com.fasterxml.jackson.core.JsonProcessingException;
import gew.zookeeper.config.ZooKeeperConfig;
import gew.zookeeper.model.NodeInfo;
import gew.zookeeper.model.ZKData;
import gew.zookeeper.util.JSONMapper;
import lombok.extern.log4j.Log4j2;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.CountDownLatch;

/**
 * Distributed System Dynamic Node Registration Management
 * @author Jason/GeW
 * @since 2018-12-20
 */
@Log4j2
public class NodeManager implements ZKClient {

    private ZooKeeper zkClient;
    private String currentNodePath;
    private NodeInfo currentNodeInfo;
    private CountDownLatch initialSignal;
    private Queue<ZKData> dataQueue;

    private static ZooKeeperConfig ZK_CONFIG;
    private static volatile NodeManager CLIENT;

    private NodeManager() {
        if (ZK_CONFIG == null || ZK_CONFIG.getHost() == null) {
            throw new IllegalArgumentException("Invalid ZooKeeper Config");
        }
        initialSignal = new CountDownLatch(1);
    }

    public static NodeManager getInstance() {
        if (CLIENT == null) {
            synchronized (NodeManager.class) {
                if (CLIENT == null) {
                    CLIENT = new NodeManager();
                }
            }
        }
        return CLIENT;
    }

    public static void setZkConfig(ZooKeeperConfig zkConfig) {
        ZK_CONFIG = zkConfig;
    }

    public String getCurrentNodePath() {
        return currentNodePath;
    }

    public void setDataQueue(final Queue<ZKData> dataQueue) {
        this.dataQueue = dataQueue;
    }

    @Override
    public ZooKeeper.States getStatus() {
        return zkClient.getState();
    }

    @Override
    public boolean connect() throws IOException, InterruptedException {
        if (zkClient != null && (zkClient.getState() == ZooKeeper.States.CONNECTED ||
                zkClient.getState() == ZooKeeper.States.CONNECTING)) {
            log.warn("ZooKeeper Client is Connecting or Connected!");
            return false;
        }
        NodeWatcher watcher = new NodeWatcher(ZK_CONFIG.getRoot(), initialSignal);
        zkClient = new ZooKeeper(ZK_CONFIG.getHost(), ZK_CONFIG.getTimeout(), watcher, ZK_CONFIG.getReadOnly());
        initialSignal.await();
        return ZooKeeper.States.CONNECTED.equals(zkClient.getState());
    }


    public String register(final NodeInfo nodeInfo) throws KeeperException, JsonProcessingException {
        String path = null;
        byte[] info = null;
        try {
            if (!existPath(ZK_CONFIG.getRoot()).isPresent()) {
                zkClient.create(ZK_CONFIG.getRoot(), null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
                log.info("Create Root Path: {}", ZK_CONFIG.getRoot());
            }
            info = JSONMapper.serialize(nodeInfo).getBytes(StandardCharsets.UTF_8);
            if (ZK_CONFIG.getSequentialMode()) {
                path = zkClient.create(ZK_CONFIG.getRoot() + "/" + nodeInfo.getName() + "-", info,
                        ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
            } else {
                String absolutePath = ZK_CONFIG.getRoot() + "/" + nodeInfo.getName();
                if (existPath(absolutePath).isPresent()) {
                    log.fatal("Given Node [{}] Has Already Registered!", absolutePath);
                }
                path = zkClient.create(absolutePath, info, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
            }
        } catch (InterruptedException e) {
            log.error("Register Node [{}] Got Interrupted!", nodeInfo.getName());
        }

        this.currentNodeInfo = nodeInfo;
        this.currentNodePath = path;
        log.info("Registered Current Node: {}, Data Size [{}] Bytes",
                path == null ? "Failed" : path, info == null ? 0 : info.length);
        return path;
    }


    public Boolean deregister() {
        if (currentNodePath == null || currentNodePath.isEmpty() || !existPath(this.currentNodePath).isPresent()) {
            log.error("Current Node Is Not Online");
            return false;
        }
        try {
            zkClient.delete(currentNodePath, -1);
            log.info("Deregister Node [{}] Success", currentNodePath);
            return true;
        } catch (Exception err) {
            log.error("Deregister Node [{}] Failed: {}", currentNodePath, err.getMessage());
            return null;
        }
    }


    @Override
    public void addWatcher(Watcher watcher) {
        if (watcher == null) {
            return;
        }
        if (watcher instanceof AdvancedWatcher) {
            ((AdvancedWatcher) watcher).setZkClient(this.zkClient);
            if (((AdvancedWatcher) watcher).getDataQueue() == null) {
                log.warn("Data OutPut Queue is Not Enabled for the Advanced Watcher...");
            }
        } else if (watcher instanceof NodeWatcher) {
            ((NodeWatcher) watcher).setZkClient(this.zkClient);
        }
        zkClient.register(watcher);
    }


    public Optional<Stat> updateNodeInfo(final NodeInfo nodeInfo, final int version)
            throws JsonProcessingException, KeeperException {
        Optional<Stat> status = Optional.empty();
        if (!this.currentNodeInfo.getName().equalsIgnoreCase(nodeInfo.getName())
                || !this.currentNodeInfo.getId().equals(nodeInfo.getId())) {
            throw new IllegalArgumentException("Node ID or Node Name Are Not Allowed to Change!");
        }
        byte[] info = JSONMapper.serialize(nodeInfo).getBytes(StandardCharsets.UTF_8);
        try {
            Stat result = zkClient.setData(this.currentNodePath, info, version);
            if (result != null) {
                status = Optional.of(result);
                log.info("Update Node [{}] Info {} Success!", this.currentNodePath, nodeInfo.toString());
            }
        } catch (InterruptedException e) {
            log.error("Update Node [{}] Info {} Got Interrupted!", this.currentNodePath, nodeInfo.toString());
        }
        return status;
    }


    @Override
    public List<String> listNodes() throws KeeperException, InterruptedException {
        return listNodes(ZK_CONFIG.getRoot());
    }

    @Override
    public List<String> listNodes(final String rootPath) throws KeeperException, InterruptedException {
        List<String> children;
        if (ZK_CONFIG.getEnableWatching()) {
            NodeWatcher watcher = new NodeWatcher(ZK_CONFIG.getRoot(), zkClient);
            watcher.enableWatching(ZK_CONFIG.getEnableWatching());
            children = zkClient.getChildren(rootPath, watcher);
        } else {
            children = zkClient.getChildren(rootPath, false);
        }
        List<String> nodes = new ArrayList<>();
        for (String child : children) {
            byte[] data = zkClient.getData(rootPath + "/" + child, false, null);
            nodes.add(new String(data, StandardCharsets.UTF_8));
        }
        return nodes;
    }


    @Override
    public Optional<Stat> existPath(final String path) {
        Optional<Stat> exists;
        try {
            Stat status = zkClient.exists(path, false);
            if (status == null) {
                exists = Optional.empty();
            } else {
                exists = Optional.of(status);
            }
        } catch (Exception e) {
            log.error("Checking Path Error for {}: {}", path, e.getMessage());
            exists = Optional.empty();
        }
        return exists;
    }


    @Override
    public void disconnect() {
        try {
            if (zkClient != null) {
                zkClient.close();
                log.debug("ZooKeeper Client Disconnected");
            }
            zkClient = null;
        } catch (InterruptedException e) {
            log.error("Closing ZooKeeper Client Got Interrupted!");
        }
    }


}
