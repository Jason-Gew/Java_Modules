package gew.kafka.client.producer;

import gew.kafka.client.entity.KafkaMessage;

import java.util.List;

/**
 * Functions should be implemented in Kafka Producer<String, String>.
 * @author Jason/GeW
 */
public interface KfkProducer {
    /**
     * Send String message to default topic, no callback ack return.
     * @param message String message
     */
    void sendMessage(final String message);

    /**
     * Send String message to custom topic, no callback ack return.
     * @param topic String topic
     * @param message String message
     */
    void sendMessage(final String topic, final String message);

    /**
     * Send String key, String message to custom topic, no callback ack return.
     * @param topic String topic
     * @param message String message
     * @param key String key
     */
    void sendMessage(final String topic, final String message, final String key);

    /**
     * Send List of String messages to default topic, no callback ack return.
     * @param messages List of String messages
     */
    void sendMessages(final List<String> messages);

    /**
     * Send custom format messages, could contain key, value, topic and partition number. No callback ack return.
     * @param messages KafkaMessage Object Instance
     */
    void sendKeyPairedMessages(final List<KafkaMessage> messages);

}
