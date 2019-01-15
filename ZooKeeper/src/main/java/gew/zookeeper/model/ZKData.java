package gew.zookeeper.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.zookeeper.Watcher;

import java.time.Instant;
import java.util.Map;

/**
 * Data Collections for Sub Nodes under ZooKeeper certain path.
 * @author Jason/GeW
 * @since 2018-12-20
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ZKData {

    private String root;
    private Map<String, NodeInfo> nodes;
    private Watcher.Event.EventType type;
    private Watcher.Event.KeeperState state;
    private Instant timestamp;
}
