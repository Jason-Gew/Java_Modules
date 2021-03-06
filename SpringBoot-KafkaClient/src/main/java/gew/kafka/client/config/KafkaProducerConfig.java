package gew.kafka.client.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author Jason/GeW
 * @since 2018-1-16
 */
@Component
@ConfigurationProperties(prefix = "kafka.producer")
public class KafkaProducerConfig {

    private String server;
    private String topic;
    private String clientId;
    private Integer retries;
    private String requireAck;
    private String serializeClass;

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public Integer getRetries() {
        return retries;
    }

    public void setRetries(Integer retries) {
        this.retries = retries;
    }

    public String getRequireAck() {
        return requireAck;
    }

    public void setRequireAck(String requireAck) {
        this.requireAck = requireAck;
    }

    public String getSerializeClass() {
        return serializeClass;
    }

    public void setSerializeClass(String serializeClass) {
        this.serializeClass = serializeClass;
    }

}
