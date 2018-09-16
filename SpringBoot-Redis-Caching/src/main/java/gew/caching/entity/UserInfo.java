package gew.caching.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.Date;


/**
 * @author Jason/GeW
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserInfo implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", unique = true, nullable = false)
    @NotEmpty(message = "Username Cannot Be Empty!")
    private String username;

    @Column(nullable = false, unique = true)
    @Email(message = "Email Format is Invalid!")
    private String email;

    private String phone;
    private Integer type;

    @Column(name = "create_date", nullable = false, updatable = false)
    @CreationTimestamp
    private Date createDate;

    @Column(name = "modify_date")
    @UpdateTimestamp
    private Date modifyDate;

    private String country;

    @Column(name = "timezone")
    private String timezone;

    private String portraitLink;
    private String comments;

    private static final long serialVersionUID = 20180901L;         // Entity Created Data, Use as Serialization UID

}
