package client.producer;

import java.util.concurrent.BlockingQueue;

/**
 * Functions should be implemented in Kafka Producer<String, String>.
 * @author Jason/GeW
 */
public interface KfkProducer
{
    void init();
    void close();
    String timestamp();
    boolean sendMessage(final String message);
    boolean sendMessage(final String topic, final String message);
    boolean sendMessage(final String topic, final String message, final String key);
    BlockingQueue<String[]> getIncomingQueue();
}
