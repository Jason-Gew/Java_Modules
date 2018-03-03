package gew.kafka.logger.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * JSON format log details, for C2M component(s).
 * @author Jason/Ge Wu
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class LogMessage
{
    private String timestamp;
    private String ip;
    private Integer port;
    private String mac;
    private LogLevel level;
    private String source;
    private String methodName;
    private String details;

    @JsonProperty("Timestamp")
    public String getTimestamp() { return timestamp; }
    @JsonProperty("Timestamp")
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    @JsonProperty("IP")
    public String getIp() { return ip; }
    @JsonProperty("IP")
    public void setIp(String ip) { this.ip = ip; }

    @JsonProperty("Port")
    public Integer getPort() { return port; }
    @JsonProperty("Port")
    public void setPort(Integer port) { this.port = port; }

    @JsonProperty("MAC")
    public String getMac() { return mac; }
    @JsonProperty("MAC")
    public void setMac(String mac) { this.mac = mac; }

    @JsonProperty("Level")
    public LogLevel getLevel() { return level; }
    @JsonProperty("Level")
    public void setLevel(LogLevel level) { this.level = level; }

    @JsonProperty("Source")
    public String getSource() { return source; }
    @JsonProperty("Source")
    public void setSource(String source) { this.source = source; }

    @JsonProperty("MethodName")
    public String getMethodName() { return methodName; }
    @JsonProperty("MethodName")
    public void setMethodName(String methodName) { this.methodName = methodName; }

    @JsonProperty("Details")
    public String getDetails() { return details; }
    @JsonProperty("Details")
    public void setDetails(String details) { this.details = details; }

    @Override
    public String toString() {
        return "LogMessage{" +
                "timestamp='" + timestamp + '\'' +
                ", ip='" + ip + '\'' +
                ", port=" + port +
                ", mac='" + mac + '\'' +
                ", level='" + level + '\'' +
                ", source='" + source + '\'' +
                ", methodName='" + methodName + '\'' +
                ", details='" + details + '\'' +
                '}';
    }
}
