package gew.kafka.client.entity;


public class KafkaMessage
{
    private String topic;
    private final String key;
    private final String value;
    private Integer partition;

    public KafkaMessage(String key, String value)
    {
        this.key = key;
        if(value == null)
            throw new IllegalArgumentException("Message value cannot be null!");
        else
            this.value = value;
    }

    public KafkaMessage(String topic, String key, String value)
    {
        this.key = key;
        if(topic == null || topic.isEmpty())
            throw new IllegalArgumentException("Topic cannot be null or empty!");
        else
            this.topic = topic;
        if(value == null)
            throw new IllegalArgumentException("Message value cannot be null!");
        else
            this.value = value;
    }

    public String getTopic() { return topic; }
    public void setTopic(final String topic)
    {
        if(topic == null || topic.isEmpty())
            throw new IllegalArgumentException("Topic cannot be null or empty!");
        else
            this.topic = topic;
    }

    public String getKey() { return key; }

    public String getValue() { return value; }

    public Integer getPartition() { return partition; }
    public void setPartition(final Integer partition) { this.partition = partition; }
}
