package gew.pubsub.mqtt;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;

public class MQTTMessage {

    private String topic;
    private byte[] message;
    private ZonedDateTime datetime;
    private boolean retained;
    private Integer messageId;
    private Boolean error;
    private Throwable exception;

    public MQTTMessage() {

    }

    public MQTTMessage(String topic, byte[] message) {
        this.error = false;
        this.topic = topic;
        this.message = message;
        this.datetime = ZonedDateTime.now(ZoneId.systemDefault());
    }

    public MQTTMessage(String topic, byte[] message, ZonedDateTime datetime) {
        this.error = false;
        this.topic = topic;
        this.message = message;
        this.datetime = datetime;
    }


    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public byte[] getMessage() {
        return message;
    }

    public void setMessage(byte[] message) {
        this.message = message;
    }

    public ZonedDateTime getDatetime() {
        return datetime;
    }

    public void setDatetime(ZonedDateTime datetime) {
        this.datetime = datetime;
    }

    public Boolean getRetained() {
        return retained;
    }

    public void setRetained(Boolean retained) {
        this.retained = retained;
    }

    public Integer getMessageId() {
        return messageId;
    }

    public void setMessageId(Integer messageId) {
        this.messageId = messageId;
    }

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public Throwable getException() {
        return exception;
    }

    public void setException(Throwable exception) {
        this.exception = exception;
    }

    @Override
    public String toString() {
        return "MQTTMessage{" +
                "topic='" + topic + '\'' +
                ", message=" + Arrays.toString(message) +
                ", datetime=" + datetime +
                ", retained=" + retained +
                ", messageId=" + messageId +
                ", error=" + error +
                ", exception=" + exception.getMessage() +
                '}';
    }
}
