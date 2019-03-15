package gew.data.warehouse.gps.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.ZonedDateTime;

/**
 * @author Jason/GeW
 * @since 2019-03-10
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DeviceConfigDto {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long id;
    private String username;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String apiKey;
    private String deviceId;
    private String function;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private ZonedDateTime modifyDatetime;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean enable;
    private String note;

    public DeviceConfigDto() {

    }

    public DeviceConfigDto(final DeviceConfig config) {
        this.id = config.getId();
        this.username = config.getUser().getUsername();
        this.deviceId = config.getDeviceId();
        this.function = config.getFunction();
        this.modifyDatetime = config.getModifyDatetime();
        this.enable = config.isEnable();
        this.note = config.getNote();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getFunction() {
        return function;
    }

    public void setFunction(String function) {
        this.function = function;
    }

    public ZonedDateTime getModifyDatetime() {
        return modifyDatetime;
    }

    public void setModifyDatetime(ZonedDateTime modifyDatetime) {
        this.modifyDatetime = modifyDatetime;
    }

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    @Override
    public String toString() {
        return "DeviceConfigDto{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", apiKey='" + apiKey + '\'' +
                ", deviceId='" + deviceId + '\'' +
                ", function='" + function + '\'' +
                ", enable=" + enable +
                ", note='" + note + '\'' +
                '}';
    }
}
