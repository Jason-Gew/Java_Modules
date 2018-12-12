package gew.kafka.client.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author Jason/GeW
 * @since 2018-1-16
 */
@Component
@ConfigurationProperties(prefix = "kafka.consumer")
public class KafkaConsumerConfig {

    private String server;
    private String topic;
    private String groupId;
    private String sessionTimeoutMs;
    private Boolean autoCommit;
    private String autoCommitInterval;
    private String deserializeClass;

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

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getSessionTimeoutMs() {
        return sessionTimeoutMs;
    }

    public void setSessionTimeoutMs(String sessionTimeoutMs) {
        this.sessionTimeoutMs = sessionTimeoutMs;
    }

    public Boolean getAutoCommit() {
        return autoCommit;
    }

    public void setAutoCommit(Boolean autoCommit) {
        this.autoCommit = autoCommit;
    }

    public String getAutoCommitInterval() {
        return autoCommitInterval;
    }

    public void setAutoCommitInterval(String autoCommitInterval) {
        this.autoCommitInterval = autoCommitInterval;
    }

    public String getDeserializeClass() {
        return deserializeClass;
    }

    public void setDeserializeClass(String deserializeClass) {
        this.deserializeClass = deserializeClass;
    }
}
