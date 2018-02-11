package clients.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;

/**
 * For unified REST API Response...
 * @author Jason/GeW
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UnifiedResponse
{
    private Integer status;
    private String statusText;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String timestamp;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer count;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Object resultBody;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Object resultHeader;

    public final static String SUCCESS = "Success";
    public final static String FAILURE = "Fail";
    public final static String UNKNOWN = "Unknown";

    public UnifiedResponse() {  }

    public UnifiedResponse(Integer status, String statusText, Object result)
    {
        this.status = status;
        this.statusText = statusText;
        this.resultBody = result;
        timestamp = utcTimestamp();
    }

    public UnifiedResponse(Integer status, String statusText, Integer count, Object result)
    {
        this.status = status;
        this.statusText = statusText;
        this.count = count;
        this.resultBody = result;
        timestamp = utcTimestamp();
    }

    private String utcTimestamp()
    {
        return Instant.now().toString();
    }

    public Integer getStatus() {
        return status;
    }
    public void setStatus(final Integer status) {
        this.status = status;
    }

    public String getStatusText() {
        return statusText;
    }
    public void setStatusText(final String statusText) {
        this.statusText = statusText;
    }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(final String timestamp) { this.timestamp = timestamp; }

    public Integer getCount() { return count; }
    public void setCount(final Integer count) { this.count = count; }

    public Object getResultBody() {
        return resultBody;
    }
    public void setResultBody(final Object resultBody) {
        this.resultBody = resultBody;
    }

    public Object getResultHeader() { return resultHeader; }
    public void setResultHeader(final Object resultHeader) { this.resultHeader = resultHeader; }

    @Override
    public String toString() {
        return "UnifiedResponse{" +
                "status=" + status +
                ", statusText='" + statusText + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", resultBody=" + resultBody +
                '}';
    }
}
