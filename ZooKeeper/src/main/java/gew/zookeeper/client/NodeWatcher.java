package gew.zookeeper.client;

import lombok.extern.log4j.Log4j2;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.util.List;
import java.util.Optional;

/**
 * @author Jason/GeW
 * @since 2018-12-20
 */
@Log4j2
public class NodeWatcher implements Watcher {

    final String root;
    volatile ZooKeeper zkClient;
    private String watcherName;
    private boolean enableWatching;

    public NodeWatcher(String root) {
        this.root = root;
        this.watcherName = "Default";
    }

    public NodeWatcher(String root, ZooKeeper zkClient) {
        this.root = root;
        this.zkClient = zkClient;
        this.watcherName = "Default";
    }


    public ZooKeeper getZkClient() {
        return zkClient;
    }

    public void setZkClient(ZooKeeper zkClient) {
        this.zkClient = zkClient;
    }

    public String getWatcherName() {
        return watcherName;
    }

    public void setWatcherName(String watcherName) {
        this.watcherName = watcherName;
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
        if (watcherName != null && !watcherName.isEmpty()) {
            log.info("Watcher [{}] -> Path \"{}\": Type: {}, State: {}", watcherName, watchedEvent.getPath(),
                    watchedEvent.getType(), watchedEvent.getState());
        } else {
            log.info("Watcher [{}] -> Path \"{}\": Type: {}, State: {}", root, watchedEvent.getPath(),
                    watchedEvent.getType(), watchedEvent.getState());
        }
        if (enableWatching && Event.EventType.NodeChildrenChanged == watchedEvent.getType()) {
            Optional<List<String>> nodes = listNodes();
            log.info("Watcher [{}] Get Current Nodes: {}", watcherName == null ? root : watcherName,
                    nodes.isPresent() ? nodes.get() : "Failed!");
        }
    }


    private Optional<List<String>> listNodes() {
        Optional<List<String>> nodes = Optional.empty();
        try {
            nodes = Optional.of(zkClient.getChildren(root, this));          // Pass Current NodeWatcher Object
        } catch (Exception err) {
            log.error("Watcher [{}] Get Sub Nodes Failed: {}", watcherName == null ? root : watcherName, err.getMessage());
        }
        return nodes;
    }

}
