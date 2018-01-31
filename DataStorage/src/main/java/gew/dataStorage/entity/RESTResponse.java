package gew.dataStorage.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;

/**
 * For unified REST API Response, should integrate with <ControllerExceptionHandler>
 * @author Jason/GeW
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class RESTResponse
{
    private String status;
    private String message;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String timestamp;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer count;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Object result;

    public final static String SUCCESS = "Success";
    public final static String FAILURE = "Fail";
    public final static String UNKNOWN = "Unknown";

    public RESTResponse() {  }

    public RESTResponse(String status, String message, Object result)
    {
        this.status = status;
        this.message = message;
        this.result = result;
        timestamp = utcTimestamp();
    }

    public RESTResponse(String status, String message, Integer count, Object result)
    {
        this.status = status;
        this.message = message;
        this.count = count;
        this.result = result;
        timestamp = utcTimestamp();
    }

    private String utcTimestamp()
    {
        return Instant.now().toString();
    }

    public String getStatus() {
        return status;
    }
    public void setStatus(final String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }
    public void setMessage(final String message) {
        this.message = message;
    }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(final String timestamp) { this.timestamp = timestamp; }

    public Integer getCount() { return count; }
    public void setCount(final Integer count) { this.count = count; }

    public Object getResult() {
        return result;
    }
    public void setResult(final Object result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "RESTResponse{" +
                "status='" + status + '\'' +
                ", message='" + message + '\'' +
                ", result=" + result +
                '}';
    }
}
