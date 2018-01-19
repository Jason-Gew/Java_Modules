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

    public String getServer() { return server; }
    public void setServer(final String server) { this.server = server; }

    public String getTopic() { return topic; }
    public void setTopic(final String topic) { this.topic = topic; }

    public String getGroupId() { return groupId; }
    public void setGroupId(final String groupId) { this.groupId = groupId; }

    public Boolean getAutoCommit() { return autoCommit; }
    public void setAutoCommit(final Boolean autoCommit) { this.autoCommit = autoCommit; }

    public String getAutoCommitInterval() { return autoCommitInterval; }
    public void setAutoCommitInterval(final String autoCommitInterval) { this.autoCommitInterval = autoCommitInterval; }

    public String getSessionTimeoutMs() { return sessionTimeoutMs; }
    public void setSessionTimeoutMs(final String sessionTimeoutMs) { this.sessionTimeoutMs = sessionTimeoutMs; }

    public String getDeserializeClass() { return deserializeClass; }
    public void setDeserializeClass(final String deserializeClass) { this.deserializeClass = deserializeClass; }
}
