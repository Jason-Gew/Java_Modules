package gew.kafka.client.producer;

import java.util.List;
import java.util.Map;

/**
 * Functions should be implemented in Kafka Producer<String, String>.
 * @author Jason/GeW
 */
public interface KfkProducer {

    void sendMessage(final String message);
    void sendMessage(final String topic, final String message);
    void sendMessage(final String topic, final String message, final String key);
    void sendMessages(final List<String> messages);
    void sendMessages(final String topic, final List<String> messages);
    void sendKeyPairedMessages(final String topic, final List<Map<String, String>> messages);
}
