package gew.zookeeper.client;

import gew.zookeeper.config.ZooKeeperConfig;
import lombok.extern.log4j.Log4j2;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * ZooKeeper Session Watcher:
 * When enabled autoReconnect, it re-initializes a new ZooKeeper client if old session expired.
 * In SERVER mode, it re-register the ephemeral node to ZooKeeper.
 * @author Jason/GeW
 * @since 2019-01-16
 */
@Log4j2
public class SessionWatcher implements Watcher {

    private boolean autoReconnect;
    private volatile ZooKeeper zkClient;
    private CountDownLatch countDownLatch;
    private final ZooKeeperConfig zooKeeperConfig;

    public SessionWatcher(ZooKeeper zkClient, ZooKeeperConfig zooKeeperConfig) {
        this.zkClient = zkClient;
        this.zooKeeperConfig = zooKeeperConfig;
    }

    public SessionWatcher(ZooKeeper zkClient, CountDownLatch countDownLatch, ZooKeeperConfig zooKeeperConfig) {
        this.zkClient = zkClient;
        this.countDownLatch = countDownLatch;
        this.zooKeeperConfig = zooKeeperConfig;
    }

    public boolean isAutoReconnect() {
        return autoReconnect;
    }

    public void setAutoReconnect(boolean autoReconnect) {
        this.autoReconnect = autoReconnect;
    }

    public CountDownLatch getCountDownLatch() {
        return countDownLatch;
    }


    @Override
    public void process(WatchedEvent watchedEvent) {
        log.info("[{}] -> Path \"{}\": Type: {}, State: {}", this.getClass().getName(), zooKeeperConfig.getRoot(),
                watchedEvent.getType(), watchedEvent.getState());
        if (countDownLatch != null && countDownLatch.getCount() > 0
                && Event.KeeperState.SyncConnected == watchedEvent.getState()) {
            countDownLatch.countDown();
            log.debug("SessionWatcher Count Down! Remain: {}", countDownLatch.getCount());
        } else if (Event.KeeperState.Expired == watchedEvent.getState() && autoReconnect) {
            Boolean status = null;
            switch (zooKeeperConfig.getNodeMode()) {
                case CLIENT:
                    log.info("ZooKeeper Session Expired, Client Mode Re-initializing...");
                    status = reInitializeClient(zooKeeperConfig);
                    break;
                case SERVER:
                    log.info("ZooKeeper Session Expired, Server Mode Re-initializing...");
                    status = reInitializeServer();
                    break;
            }
            log.info("[{}] -> ZooKeeper Client Re-Initialize Status: {}", this.getClass().getName(), status);
        }
    }


    private List<String> listNodes() {
        List<String> nodes = null;
        try {
            nodes = zkClient.getChildren(zooKeeperConfig.getRoot(), true);
        } catch (Exception err) {
            log.error("[{}] Get Sub Nodes Failed: {}", this.getClass().getName(), err.getMessage());
        }
        return nodes;
    }


    protected boolean reInitializeClient(ZooKeeperConfig config) {
        boolean status = false;
        countDownLatch = new CountDownLatch(1);
        try {
            zkClient = new ZooKeeper(config.getHost(), config.getTimeout(), this, config.getReadOnly());
            countDownLatch.await();
            status = ZooKeeper.States.CONNECTED.equals(zkClient.getState());
            if (!status) {
                Thread.sleep(5000);
            } else {
                List<String> nodes = listNodes();
                log.info("Re-Initialized Success -> Remaining Nodes:\n{}", nodes);
            }
        } catch (Exception e) {
            log.error("Re-Initialize ZooKeeper Client Exception: {}", e.getMessage());
        }
        return status;
    }


    protected boolean reInitializeServer() {
        boolean status = false;
        NodeManager manager = NodeManager.getInstance();
        manager.disconnect();
        try {
            countDownLatch = new CountDownLatch(1);
            status = manager.reconnect(this, this.zooKeeperConfig);
            if (status) {
                manager.register(manager.getCurrentNodeInfo());             // Re-register itself
                List<String> nodes = manager.listNodes();                   // Re-invoke listNode(), Add NodeWatcher
                log.info("Re-Initialized Success, Remaining Nodes:\n{}", nodes);
            } else {
                Thread.sleep(5000);
            }
        } catch (Exception e) {
            log.error("Re-Initialize ZooKeeper Client (Server Mode) Exception: {}", e.getMessage());
        }
        return status;
    }
}
