package gew.zookeeper.config;

import lombok.Builder;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author Jason/GeW
 */
@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class ZooKeeperConfig {

    private String host;
    private String root;
    private Long sessionId;
    private String sessionPassword;
    private String authType;
    private String authInfo;
    private Integer timeout = 4000;
    private Boolean readOnly = false;
    private Boolean sequentialMode = false;
    private Boolean enableWatching = false;
    private Boolean enableAdvancedWatcher;
}
