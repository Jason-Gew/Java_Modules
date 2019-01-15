package gew.zookeeper.client;

import gew.zookeeper.config.ZooKeeperConfig;
import gew.zookeeper.model.NodeInfo;
import gew.zookeeper.model.ZKData;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.data.Stat;

/**
 * @author Jason/GeW
 */
public interface NodeClient extends ZKClient {

    ZooKeeperConfig getZooKeeperConfig();

    void setZooKeeperConfig(final ZooKeeperConfig zooKeeperConfig);

    NodeInfo getNodeInfo(final String rootPath, final String nodeName) throws KeeperException;

    NodeInfo getNodeInfo(final String rootPath, final String nodeName, final Stat stat) throws KeeperException;

    ZKData listNodesInfo(final String rootPath) throws KeeperException ;

}
