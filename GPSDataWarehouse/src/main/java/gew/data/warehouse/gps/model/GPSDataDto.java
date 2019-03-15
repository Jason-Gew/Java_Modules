package gew.data.warehouse.gps.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;


/**
 * @author Jason/GeW
 * @since 2019-03-12
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GPSDataDto {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long id;

    private String username;

    private String deviceId;

    private String type;

    private String datetime;

    private Long timestamp;

    private Double latitude;

    private Double longitude;

    private Float altitude;

    private Float speed;

    private Integer satellites;

    private String note;


    public GPSDataDto() {

    }

    public GPSDataDto(final GPSData data) {
        this.id = data.getId();
        this.username = data.getUser().getUsername();
        this.deviceId = data.getConfig().getDeviceId();
        this.type = data.getType();
        this.datetime = data.getDatetime();
        this.timestamp = data.getUnixTimestamp();
        this.latitude = data.getLatitude();
        this.longitude = data.getLongitude();
        this.altitude = data.getAltitude();
        this.speed = data.getSpeed();
        this.satellites = data.getSatellites();
        this.note = data.getNote();
    }
}
