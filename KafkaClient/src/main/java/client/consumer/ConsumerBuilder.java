package client.consumer;


/**
 * Builder class for instantiating the Kafka Consumer...
 * @author Jason/GeW
 */
public class ConsumerBuilder
{
    private String server;
    private String topic;
    private String groupId;
    private String clientId;
    private Integer sessionTimeoutMs;
    private Boolean autoCommit;
    private Integer autoCommitIntervalMs;
    private String deserializeClass;
    private Boolean enableMessageQueue;

    public ConsumerBuilder() { }

    public ConsumerBuilder setServer(String server) {
        this.server = server;
        return this;
    }

    public ConsumerBuilder setTopic(String topic) {
        this.topic = topic;
        return this;
    }

    public ConsumerBuilder setGroupId(String groupId) {
        this.groupId = groupId;
        return this;
    }

    public ConsumerBuilder setClientId(String clientId) {
        this.clientId = clientId;
        return this;
    }

    public ConsumerBuilder setSessionTimeoutMs(Integer sessionTimeoutMs) {
        this.sessionTimeoutMs = sessionTimeoutMs;
        return this;
    }

    public ConsumerBuilder setAutoCommit(Boolean autoCommit) {
        this.autoCommit = autoCommit;
        return this;
    }

    public ConsumerBuilder setAutoCommitIntervalMs(Integer autoCommitIntervalMs) {
        this.autoCommitIntervalMs = autoCommitIntervalMs;
        return this;
    }

    public ConsumerBuilder setDeserializeClass(String deserializeClass) {
        this.deserializeClass = deserializeClass;
        return this;
    }

    public ConsumerBuilder setEnableMessageQueue(Boolean enableMessageQueue) {
        this.enableMessageQueue = enableMessageQueue;
        return this;
    }

    public Consumer build()
    {
        Consumer consumer = new Consumer();
        if(this.server != null && !this.server.isEmpty()) {
            consumer.server = this.server;
        } else {
            throw new IllegalArgumentException("Invalid Kafka Bootstrap Server Address");
        }

        if(this.topic != null && !this.topic.isEmpty()) {
            consumer.topic = this.topic;
        } else {
            System.err.println("No Default Topic");
        }

        if(this.groupId != null && !this.groupId.isEmpty()) {
            consumer.groupId = this.groupId;
        } else {
            consumer.groupId = "Kafka-Consumer-Default";
        }

        if(this.clientId != null && !this.clientId.isEmpty())
            consumer.clientId = this.clientId;

        if(this.sessionTimeoutMs != null && this.sessionTimeoutMs > 10000) {
            consumer.sessionTimeoutMs = this.sessionTimeoutMs.toString();
        } else {
            consumer.sessionTimeoutMs = "30000";
        }

        if(this.autoCommit != null)
            consumer.autoCommit = this.autoCommit.toString();
        else
            consumer.autoCommit = "true";

        if(this.autoCommitIntervalMs != null && this.autoCommitIntervalMs > 1000) {
            consumer.autoCommitIntervalMs = this.autoCommitIntervalMs.toString();
        } else {
            consumer.autoCommitIntervalMs = "5000";
        }

        if(this.deserializeClass != null && !this.deserializeClass.isEmpty())
            consumer.deserializeClass = this.deserializeClass;
        else
            throw new IllegalArgumentException("Invalid Deserialization Class");

        if(this.enableMessageQueue != null)
            consumer.enableMessageQueue = this.enableMessageQueue;
        else
            consumer.enableMessageQueue = false;

        return consumer;
    }
}
