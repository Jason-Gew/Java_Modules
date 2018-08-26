package gew.kafka.logger.kafka;

/**
 * Builder class for instantiating the Kafka Producer...
 * @author Jason/Ge Wu
 */
public class ProducerBuilder
{
    private String server;
    private String topic;
    private String clientId;
    private Integer retries;
    private String acknowledge;
    private String serializeClass;
    private Boolean enableMessageQueue;

    public ProducerBuilder() { }

    public ProducerBuilder setServer(String server) {
        this.server = server;
        return this;
    }

    public ProducerBuilder setTopic(String topic) {
        this.topic = topic;
        return this;
    }

    public ProducerBuilder setClientId(String clientId) {
        this.clientId = clientId;
        return this;
    }

    public ProducerBuilder setRetries(Integer retries) {
        this.retries = retries;
        return this;
    }

    public ProducerBuilder setAcknowledge(String acknowledge) {
        this.acknowledge = acknowledge;
        return this;
    }

    public ProducerBuilder setSerializeClass(String serializeClass) {
        this.serializeClass = serializeClass;
        return this;
    }

    public ProducerBuilder setEnableMessageQueue(Boolean enableMessageQueue) {
        this.enableMessageQueue = enableMessageQueue;
        return this;
    }

    public Producer build()
    {
        Producer producer = new Producer();
        if(this.server != null && !this.server.isEmpty()) {
            producer.server = this.server;
        } else {
            throw new IllegalArgumentException("Invalid Kafka Bootstrap Server Address");
        }

        if(this.topic != null && !this.topic.isEmpty()) {
            producer.topic = this.topic;
        }

        if(this.clientId != null && !this.clientId.isEmpty()) {
            producer.clientId = this.clientId;
        } else {
            producer.clientId = "Kafka-Producer-" + System.currentTimeMillis()/1000000;
        }

        if(this.retries!= null && this.retries >= 0)
            producer.retries = this.retries.toString();
        else
            producer.retries = "0";

        if(this.acknowledge != null && !this.acknowledge.isEmpty())
            producer.acknowledge = this.acknowledge;
        else
            producer.acknowledge = "0";

        if(this.serializeClass != null && !this.serializeClass.isEmpty())
            producer.serializeClass = this.serializeClass;
        else
            throw new IllegalArgumentException("Invalid Serialization Class");

        if(this.enableMessageQueue != null)
            producer.enableMessageQueue = this.enableMessageQueue;
        else
            producer.enableMessageQueue = false;

        return producer;
    }
}
