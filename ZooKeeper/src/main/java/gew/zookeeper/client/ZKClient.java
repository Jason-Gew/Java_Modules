package gew.zookeeper.client;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * @author Jason/GeW
 */
public interface ZKClient {

    boolean connect() throws IOException, InterruptedException;

    void disconnect();

    ZooKeeper.States getStatus();

    List<String> listNodes() throws KeeperException, InterruptedException;

    List<String> listNodes(final String rootPath) throws KeeperException, InterruptedException;

    Optional<Stat> existPath(final String path);

    void addWatcher(Watcher watcher);

}
