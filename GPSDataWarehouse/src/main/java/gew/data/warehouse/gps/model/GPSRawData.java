package gew.data.warehouse.gps.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


/**
 * @author Jason/GeW
 * @since 2019-03-04
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class GPSRawData implements Serializable {

    private Long id;
    private String username;
    private String type;
    private Long timestamp;
    private String data;
    private String note;
}
