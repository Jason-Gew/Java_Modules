package gew.zookeeper.client;

import lombok.extern.log4j.Log4j2;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;

/**
 * @author Jason/GeW
 * @since 2018-12-20
 */
@Log4j2
public class NodeWatcher implements Watcher {

    final String root;
    ZooKeeper zkClient;
    CountDownLatch countDownLatch;
    private boolean enableWatching;

    public NodeWatcher(String root, ZooKeeper zkClient) {
        this.root = root;
        this.zkClient = zkClient;
    }

    public NodeWatcher(String root, CountDownLatch countDownLatch) {
        this.root = root;
        this.countDownLatch = countDownLatch;
    }

    public ZooKeeper getZkClient() {
        return zkClient;
    }

    public void setZkClient(ZooKeeper zkClient) {
        this.zkClient = zkClient;
    }

    public boolean isEnableWatching() {
        return enableWatching;
    }

    /**
     * Enable Watching, invoke after first time instantiate.
     * @param enableWatching boolean flag
     */
    public void enableWatching(boolean enableWatching) {
        this.enableWatching = enableWatching;
    }


    public void process(WatchedEvent watchedEvent) {
        log.info("Watcher [{}] -> Path \"{}\": Type: {}, State: {}", root, watchedEvent.getPath(),
                watchedEvent.getType(), watchedEvent.getState());
        if (countDownLatch != null && countDownLatch.getCount() > 0
                && watchedEvent.getState().equals(Event.KeeperState.SyncConnected)) {
            countDownLatch.countDown();
            log.debug("Watcher Count Down! Total: {}", countDownLatch.getCount());
        }
        if (enableWatching) {
            Optional<List<String>> nodes = listNodes();
            log.info("Watcher [{}] Get Current Nodes: {}", root, nodes.isPresent() ? nodes.get() : "Failed!");
        }
    }


    private Optional<List<String>> listNodes() {
        Optional<List<String>> nodes = Optional.empty();
        try {
            nodes = Optional.of(zkClient.getChildren(root, true));
        } catch (Exception err) {
            log.error("Watcher [{}] Get Sub Nodes Failed: {}", root, err.getMessage());
        }
        return nodes;
    }

}
