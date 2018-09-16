package gew.caching.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Message for direct caching into Redis
 * @author Jason/GeW
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CacheMessage implements Serializable {

    private static final long serialVersionUID = 20180901L;         // Entity Created Data, Use as Serialization UID

    private String timestamp;
    private String timezone;
    @NotNull
    @NotEmpty
    private String key;
    private Long ttl;
    private String timeUnit;
    @NotNull
    private Object message;

}
