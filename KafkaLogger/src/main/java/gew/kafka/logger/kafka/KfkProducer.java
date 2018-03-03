package gew.kafka.logger.kafka;

import java.util.Queue;

/**
 * Functions should be implemented in Kafka Producer<String, String>.
 * @author Jason/Ge Wu
 */
public interface KfkProducer
{
    void init();
    void close();
    String timestamp();
    boolean sendMessage(final String message);
    boolean sendMessage(final String topic, final String message);
    boolean sendMessage(final String topic, final String message, final String key);
    boolean sendKeyPairedMessage(final String key, final String message);
    Queue<String[]> getIncomingQueue();
}
