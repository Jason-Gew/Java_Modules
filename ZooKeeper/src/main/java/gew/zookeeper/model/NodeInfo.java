package gew.zookeeper.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;
import java.time.Instant;
import java.util.Map;

/**
 * Distributed Node Information, written to the ZooKeeper Node as JSON format or Serialized Object.
 * Once Registered to the ZooKeeper, id and name should not be updated in order to keep consistence!
 * @author Jason/GeW
 * @since 2018-12-20
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class NodeInfo implements Serializable {

    private Integer id;
    private String name;
    private String version;
    private Integer sequence;
    private Map<String, String> info;                       // Addition Information
    private String timestamp = Instant.now().toString();    // UTC Timestamp

    private static final long serialversionUID = 20181220L;


    public NodeInfo() {

    }

    public NodeInfo(Integer id, String name, String version) {
        this.id = id;
        this.name = name;
        this.version = version;
    }

}
