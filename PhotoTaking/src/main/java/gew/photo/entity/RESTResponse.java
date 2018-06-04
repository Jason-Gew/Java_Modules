package gew.photo.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;

/**
 * REST Response for a unified format.
 * @author Jason/Ge Wu
 * @since 2017-08-18
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class RESTResponse
{
    private Integer code;
    private Status status;
    private String message;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer count;
    private String timestamp;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Object result;

    public RESTResponse() { }

    public RESTResponse(Integer code, Status status, String message, Object result) {
        this.code = code;
        this.status = status;
        this.message = message;
        this.result = result;
        this.timestamp = GenerateTimestamp();
    }

    public RESTResponse(Integer code, Status status, String message, String timestamp, Object result) {
        this.code = code;
        this.status = status;
        this.message = message;
        this.timestamp = timestamp;
        this.result = result;
    }

    private String GenerateTimestamp() {
        return Instant.now().toString();
    }

    public Integer getCode() { return code; }
    public void setCode(Integer code) { this.code = code; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Integer getCount() { return count; }
    public void setCount(Integer count) { this.count = count; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    public Object getResult() { return result; }
    public void setResult(Object result) { this.result = result; }

    @Override
    public String toString() {
        return "RESTResponse{" +
                "code=" + code +
                ", status=" + status +
                ", message='" + message + '\'' +
                ", count=" + count +
                ", timestamp='" + timestamp + '\'' +
                ", result=" + result +
                '}';
    }
}
