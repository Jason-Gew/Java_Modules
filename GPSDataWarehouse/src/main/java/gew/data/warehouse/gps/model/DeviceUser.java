package gew.data.warehouse.gps.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
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
@Table(name = "device_user",
        indexes = {@Index(name = "username",  columnList="username", unique = true),
                   @Index(name = "email",     columnList="email", unique = true)})
@JsonIgnoreProperties(ignoreUnknown = true)
public class DeviceUser implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "username cannot be null")
    @NotEmpty(message = "username cannot be empty")
    @Column(name = "username", unique = true, updatable = false, nullable = false)
    private String username;

    @Email(message = "invalid email address")
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "apikey", nullable = false)
    private String apiKey;

    private boolean active;

    private Integer role = 0;

    @CreationTimestamp
    private ZonedDateTime createTime;

    @UpdateTimestamp
    private ZonedDateTime updateTime;

    private String timezone;

}
