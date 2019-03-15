package gew.data.warehouse.gps.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import gew.data.warehouse.gps.util.DatetimeConverter;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * @author Jason/GeW
 * @since 2019-03-04
 */
@Data
@Entity
@Table(name = "gps_data", indexes = {@Index(name = "user", columnList = "user"),
                                     @Index(name = "config", columnList = "config")})
@JsonIgnoreProperties(ignoreUnknown = true)
public class GPSData implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "user", nullable = false)
    private DeviceUser user;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "config", nullable = false)
    private DeviceConfig config;

    @Size(max = 255)
    private String type;

    @NotNull
    @Column(name = "date_time", nullable = false)
    private String datetime;

    @Column(name = "unix_timestamp")
    private Long unixTimestamp;

    private Double latitude;

    private Double longitude;

    private Float altitude;

    private Float speed;

    private Integer satellites;

    @Size(max = 255)
    private String note;


    public GPSData() {

    }

    public GPSData(@NotNull DeviceUser user, @NotNull DeviceConfig config) {
        this.user = user;
        this.config = config;
    }

    public void autoFulfil(final GPSDataDto temp) {
        this.type = temp.getType();
        this.datetime = temp.getDatetime();
        this.unixTimestamp = temp.getTimestamp();
        this.latitude = temp.getLatitude();
        this.longitude = temp.getLongitude();
        this.altitude = temp.getAltitude();
        this.speed = temp.getSpeed();
        this.satellites = temp.getSatellites();
        this.note = temp.getNote();
    }

    public Boolean autoDateTimeConvert() {
        if (this.datetime.isEmpty() && (this.unixTimestamp == null || this.unixTimestamp == 0)) {
            return null;
        } else if (!datetime.isEmpty() && (this.unixTimestamp == null || this.unixTimestamp == 0)) {
            try {
                this.unixTimestamp = DatetimeConverter.toUnixTimestamp(this.datetime);
                return true;
            } catch (Exception err) {
                return false;
            }
        } else {
            return false;
        }
    }
}
