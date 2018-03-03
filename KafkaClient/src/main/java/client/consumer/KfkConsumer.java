package client.consumer;

import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;

import java.util.Collection;
import java.util.List;
import java.util.Queue;

/**
 * Functions should be implemented in Kafka Consumer/Listener Service.
 * @author Jason/GeW
 */
public interface KfkConsumer
{
    void init();
    void stop();
    void close();
    void subscribe();
    void subscribe(final List<String> topics);
    void subscribe(final Collection<String> topics, ConsumerRebalanceListener listener);
    void setOutputQueue(Queue<String[]> outputQueue);

    static int getPollingTimeout() { return 1000; }
    static void setPollingTimeout(int pollingTimeout) {  }

    static int getIdlePeriod() { return 10; }
    static void setIdlePeriod(int idlePeriod) {  }
}
