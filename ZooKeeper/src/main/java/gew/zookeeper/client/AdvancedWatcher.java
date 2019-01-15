package gew.zookeeper.client;


import gew.zookeeper.model.NodeInfo;
import gew.zookeeper.model.ZKData;
import gew.zookeeper.util.JSONMapper;
import lombok.extern.log4j.Log4j2;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.CountDownLatch;


/**
 * @author Jason/GeW
 * @since 2018-12-24
 */
@Log4j2
public class AdvancedWatcher extends NodeWatcher {


    private Queue<ZKData> dataQueue;

    public AdvancedWatcher(String root, ZooKeeper zkClient) {
        super(root, zkClient);
    }

    public AdvancedWatcher(String root, CountDownLatch countDownLatch) {
        super(root, countDownLatch);
    }

    public AdvancedWatcher(String root, ZooKeeper zkClient, Queue<ZKData> dataQueue) {
        super(root, zkClient);
        this.dataQueue = dataQueue;
    }

    public Queue<ZKData> getDataQueue() {
        return dataQueue;
    }

    public void setDataQueue(Queue<ZKData> dataQueue) {
        this.dataQueue = dataQueue;
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        if (dataQueue == null) {
            super.process(watchedEvent);
        } else {
            log.info("Advanced Watcher [{}] -> Path \"{}\": Type: {}, State: {}", super.root, watchedEvent.getPath(),
                    watchedEvent.getType(), watchedEvent.getState());
            if (super.countDownLatch != null && super.countDownLatch.getCount() > 0
                    && Event.KeeperState.SyncConnected.equals(watchedEvent.getState())) {
                super.countDownLatch.countDown();
                log.debug("Advanced Watcher Count Down! Total: {}", super.countDownLatch.getCount());
            }
            if (super.isEnableWatching()) {
                ZKData data = getNodesData();
                data.setTimestamp(Instant.now());
                data.setType(watchedEvent.getType());
                data.setState(watchedEvent.getState());
                boolean queueState = dataQueue.offer(data);
                log.debug("Advanced Watcher Add ZooKeeper Data to the Queue: {}", queueState);
                notify();
            }
        }
    }

    private ZKData getNodesData() {
        ZKData zkData = new ZKData();
        zkData.setRoot(super.root);
        final Map<String, NodeInfo> nodeInfoMap = new HashMap<>();
        try {
            List<String> nodes = super.zkClient.getChildren(super.root, true);
            if (nodes == null || nodes.isEmpty()) {
                return zkData;
            }
            for (String node : nodes) {
                byte[] data = zkClient.getData(super.root + "/" + node, false, null);
                NodeInfo nodeInfo;
                try {
                    nodeInfo = JSONMapper.deserialize(data);
                    nodeInfoMap.put(node, nodeInfo);
                } catch (IOException | IllegalArgumentException err) {
                    log.error("Advanced Watcher Mapping Node [{}] Info Failed: {}", node, err.getMessage());
                    nodeInfoMap.put(node, null);
                }
            }
        } catch (Exception e) {
            log.error("Advanced Watcher [{}] Get Sub Nodes Data Failed: {}", root, e.getMessage());
        } finally {
            zkData.setNodes(Collections.unmodifiableMap(nodeInfoMap));
        }
        return zkData;
    }
}
