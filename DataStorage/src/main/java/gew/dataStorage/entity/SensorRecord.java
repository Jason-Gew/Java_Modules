package gew.dataStorage.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

/**
 * Table/Entity to store into the database
 * @author Jason/GeW
 */
@Entity
public class SensorRecord
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(updatable = false)
    private Date timestamp;
    private Double temperature;
    private Double humidity;
    private Integer light;
    private Integer signal;
    private Boolean flood;
    private String note;

    public SensorRecord() {  }

    public SensorRecord(Integer id, Double temperature, Double humidity, Integer light, Integer signal, Boolean flood, String note)
    {
        this.id = id;
        this.temperature = temperature;
        this.humidity = humidity;
        this.light = light;
        this.signal = signal;
        this.flood = flood;
        this.note = note;
    }

    public Integer getId() {
        return id;
    }
    public void setId(final Integer id) {
        this.id = id;
    }

    public Date getTimestamp() { return timestamp; }
    public void setTimestamp(final Date timestamp) { this.timestamp = timestamp; }

    public Double getTemperature() {
        return temperature;
    }
    public void setTemperature(final Double temperature) {
        this.temperature = temperature;
    }

    public Double getHumidity() {
        return humidity;
    }
    public void setHumidity(final Double humidity) {
        this.humidity = humidity;
    }

    public Integer getLight() {
        return light;
    }
    public void setLight(final Integer light) {
        this.light = light;
    }

    public Integer getSignal() {
        return signal;
    }
    public void setSignal(final Integer signal) {
        this.signal = signal;
    }

    public Boolean getFlood() {
        return flood;
    }
    public void setFlood(final Boolean flood) {
        this.flood = flood;
    }

    public String getNote() {
        return note;
    }
    public void setNote(final String note) {
        this.note = note;
    }

    @PrePersist
    protected void onCreate()
    {
        timestamp = new Date();
    }

//    @PreUpdate
//    protected void onUpdate()
//    {
//        timestamp = new Date();
//    }

    @Override
    public String toString() {
        return "SensorRecord{" +
                "id=" + id +
                ", timestamp=" + timestamp +
                ", temperature=" + temperature +
                ", humidity=" + humidity +
                ", light=" + light +
                ", signal=" + signal +
                ", flood=" + flood +
                ", note='" + note + '\'' +
                '}';
    }
}
