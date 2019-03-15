
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;


/**
 * REST Response for formatting to a unified HTTP Response.
 * @author Jason/GeW
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class RestResponse {

    private Integer code;
    private Status status;
    private String message;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String timestamp;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer count;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Object result;

    public RestResponse() {

    }

    public RestResponse(Integer code, Status status, String message) {
        this.code = code;
        this.status = status;
        this.message = message;
        this.timestamp = Instant.now().toString();
    }

    public RestResponse(Integer code, Status status, String message, Object result) {
        this.code = code;
        this.status = status;
        this.message = message;
        this.result = result;
        this.timestamp = Instant.now().toString();
    }

    public RestResponse(Integer code, Status status, String message, String timestamp, Object result) {
        this.code = code;
        this.status = status;
        this.message = message;
        this.result = result;
        this.timestamp = timestamp;
    }

    public RestResponse(Integer code, Status status, String message, Integer count, Object result) {
        this.code = code;
        this.status = status;
        this.message = message;
        this.count = count;
        this.result = result;
        this.timestamp = Instant.now().toString();
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
        return "RestResponse{" +
                "code=" + code +
                ", status=" + status +
                ", message='" + message + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", count=" + count +
                ", result=" + result +
                '}';
    }
}
