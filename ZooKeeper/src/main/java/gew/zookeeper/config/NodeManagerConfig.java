package gew.zookeeper.config;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Jason/GeW
 */
@Data
@NoArgsConstructor
public class NodeManagerConfig {

    private Integer id;
    private String name;
    private String timezoneOffset;
    private List<Integer> ports;    // ports split by ',' without space
    private String notes;
}
