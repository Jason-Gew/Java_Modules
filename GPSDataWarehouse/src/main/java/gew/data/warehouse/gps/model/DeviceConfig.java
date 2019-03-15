package gew.data.warehouse.gps.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.ZonedDateTime;

/**
 * @author Jason/GeW
 * @since 2019-03-04
 */
@Data
@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class DeviceConfig implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private DeviceUser user;

    @NotNull(message = "deviceId unique id cannot be null")
    @NotEmpty(message = "deviceId unique id cannot be empty")
    @Column(name = "device_id", nullable = false)
    private String deviceId;

    private String function;

    @UpdateTimestamp
    @Column(name = "modify_datetime")
    private ZonedDateTime modifyDatetime;

    @Column(nullable = false)
    private boolean enable;

    private String note;
}
