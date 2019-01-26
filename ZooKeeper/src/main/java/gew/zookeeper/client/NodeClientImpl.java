package gew.zookeeper.client;

import gew.zookeeper.config.ZooKeeperConfig;
import gew.zookeeper.model.NodeInfo;
import gew.zookeeper.model.ZKData;
import gew.zookeeper.util.JSONMapper;
import lombok.extern.log4j.Log4j2;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

/**
 * Distributed System Dynamic Node Registration Management
 * @author Jason/GeW
 * @since 2018-12-20
 */
@Log4j2
public class NodeClientImpl implements NodeClient, Callable<ZKData> {

    private ZooKeeper zkClient;
    private CountDownLatch initialSignal;
    private ZooKeeperConfig zooKeeperConfig;


    public NodeClientImpl(ZooKeeperConfig zooKeeperConfig) {
        this.zooKeeperConfig = zooKeeperConfig;
        initialSignal = new CountDownLatch(1);
    }


    @Override
    public ZooKeeperConfig getZooKeeperConfig() {
        return zooKeeperConfig;
    }

    @Override
    public void setZooKeeperConfig(ZooKeeperConfig zooKeeperConfig) {
        this.zooKeeperConfig = zooKeeperConfig;
    }


    @Override
    public boolean connect() throws IOException, InterruptedException {
        if (zooKeeperConfig == null || zooKeeperConfig.getHost().isEmpty() || zooKeeperConfig.getRoot().isEmpty()) {
            log.error("Invalid ZooKeeper Config");
            return false;
        } else if (zkClient != null && (zkClient.getState() == ZooKeeper.States.CONNECTED ||
                zkClient.getState() == ZooKeeper.States.CONNECTING)) {
            log.warn("ZooKeeper Client is Connecting or Connected!");
            return false;
        } else {
            SessionWatcher watcher = new SessionWatcher(zkClient, initialSignal, zooKeeperConfig);
            watcher.setAutoReconnect(true);
            zkClient = new ZooKeeper(zooKeeperConfig.getHost(), zooKeeperConfig.getTimeout(),
                    watcher, zooKeeperConfig.getReadOnly());
            initialSignal.await();
            return ZooKeeper.States.CONNECTED == zkClient.getState();
        }
    }

    @Override
    public boolean reconnect(SessionWatcher watcher, ZooKeeperConfig config) throws IOException {
        if (zkClient != null) {
            disconnect();
        }
        if (watcher.getCountDownLatch() == null || watcher.getCountDownLatch().getCount() != 1) {
            throw new IllegalArgumentException("Invalid CountDown Setting in Watcher");
        }
        if (config != null) {
            zkClient = new ZooKeeper(config.getHost(), config.getTimeout(), watcher, config.getReadOnly());
        } else {
            zkClient = new ZooKeeper(zooKeeperConfig.getHost(), zooKeeperConfig.getTimeout(),
                    watcher, zooKeeperConfig.getReadOnly());
        }
        try {
            initialSignal.await();
        } catch (InterruptedException e) {
            log.error("Reconnection Await Got Interrupted!");
        }
        return ZooKeeper.States.CONNECTED.equals(zkClient.getState());
    }

    @Override
    public void disconnect() {
        try {
            if (zkClient != null) {
                zkClient.close();
            }
            zkClient = null;
        } catch (Exception e) {
            log.error("Closing ZooKeeper Client Error: " + e.getMessage());
        }
    }

    @Override
    public ZooKeeper.States getStatus() {
        return zkClient.getState();
    }


    @Override
    public void addWatcher(Watcher watcher) {
        //TODO
    }


    @Override
    public List<String> listNodes() throws KeeperException, InterruptedException {
        return listNodes(zooKeeperConfig.getRoot());
    }

    @Override
    public List<String> listNodes(String rootPath) throws KeeperException, InterruptedException {
        return zkClient.getChildren(rootPath, false);
    }

    @Override
    public List<String> listNodes(String rootPath, NodeWatcher watcher) throws KeeperException, InterruptedException {
        if (watcher.getZkClient() == null) {
            watcher.setZkClient(zkClient);
        }
        return zkClient.getChildren(rootPath, watcher);
    }

    @Override
    public NodeInfo getNodeInfo(String rootPath, String nodeName) throws KeeperException {
        return getNodeInfo(rootPath, nodeName, null);
    }

    @Override
    public NodeInfo getNodeInfo(String rootPath, String nodeName, Stat stat) throws KeeperException {
        NodeInfo nodeInfo = null;
        try {
            byte[] data = zkClient.getData(zooKeeperConfig.getRoot() + "/" + nodeName, false, stat);
            nodeInfo = JSONMapper.deserialize(data);
        } catch (InterruptedException e) {
            log.error("Get Node [{}] Info Got Interrupted...", rootPath + "/" + nodeName);
        } catch (IOException e) {
            log.error("Deserialize Node [{}] Info Error: {}", rootPath + "/" + nodeName, e.getMessage());
        }
        return nodeInfo;
    }

    @Override
    public ZKData listNodesInfo(String rootPath) throws KeeperException {
        ZKData zkData = new ZKData();
        zkData.setRoot(rootPath);
        try {
            zkData.setTimestamp(Instant.now());
            List<String> subNodes = listNodes();
            Map<String, NodeInfo> nodes = new HashMap<>();
            for (String node : subNodes) {
                try {
                    NodeInfo nodeInfo = getNodeInfo(rootPath, node);
                    nodes.put(node, nodeInfo);
                } catch (Exception err) {
                    log.error("Node Client Mapping Node [{}] Info Failed: {}", node, err.getMessage());
                    nodes.put(node, null);
                }
            }
            zkData.setNodes(Collections.unmodifiableMap(nodes));
        } catch (InterruptedException e) {
            log.error("Node Client List Nodes Info From [{}] Got Interrupted.", rootPath);
        }
        return zkData;
    }


    @Override
    public Optional<Stat> existPath(String path) {
        Optional<Stat> exists;
        try {
            Stat status = zkClient.exists(path, null);
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


    /**
     * Async One Time Call to Get ZooKeeper Node Information
     * @return ZKData, Current Node Information under ZooKeeper Certain Path
     * @throws Exception KeeperException, InterruptException
     */
    @Override
    public ZKData call() throws Exception {
        try {
            boolean status = connect();
            if (status) {
                return listNodesInfo(zooKeeperConfig.getRoot());
            } else {
                return new ZKData();
            }
        } finally {
            disconnect();
        }
    }
}
