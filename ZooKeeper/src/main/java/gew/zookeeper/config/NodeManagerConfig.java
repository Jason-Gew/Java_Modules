package gew.zookeeper.config;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Jason/GeW
 */
@Data
@NoArgsConstructor
public class NodeManagerConfig {

    private Integer id;
    private String name;
    private String timezoneOffset;
    private Integer[] ports;    // ports split by ',' without space
    private String notes;
}
