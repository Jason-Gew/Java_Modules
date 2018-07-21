package caching.memcache.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.List;

/**
 * For JSON Object Example
 * @author Jason/GeW
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Message implements Serializable {

    private Integer id;
    private String message;
    private String datetime;
    private List<String> element;

    private static final long serialversionUID = 1L;

    public Message() {

    }

    public Message(Integer id, String message, String datetime, List<String> element) {
        this.id = id;
        this.message = message;
        this.datetime = datetime;
        this.element = element;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public List<String> getElement() {
        return element;
    }

    public void setElement(List<String> element) {
        this.element = element;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", message='" + message + '\'' +
                ", datetime='" + datetime + '\'' +
                ", element=" + element +
                '}';
    }
}
