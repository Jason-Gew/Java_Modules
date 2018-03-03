package client.consumer;

import org.apache.kafka.clients.consumer.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;


/**
 * Apache Kafka Consumer, support message queue for data output.
 * @author Jason/GeW
 */
public class Consumer implements KfkConsumer, Runnable
{
    String server;
    String topic;
    String groupId;
    String clientId;
    String sessionTimeoutMs;
    String autoCommit;
    String autoCommitIntervalMs;
    String deserializeClass;
    Boolean enableMessageQueue;

    private AtomicBoolean ctrlSignal;
    private Queue<String[]> outputQueue;
    private KafkaConsumer<String, String> consumer;

    private static int IDLE_PERIOD = 50;
    private static int POLLING_TIMEOUT = 1000;

    private static final Logger logger = LoggerFactory.getLogger(Consumer.class);

    Consumer() { }      // Package-private constructor for Builder class to instantiate.

    public static int getIdlePeriod() { return IDLE_PERIOD; }
    public static void setIdlePeriod(int idlePeriod) { IDLE_PERIOD = idlePeriod; }

    public static int getPollingTimeout() { return POLLING_TIMEOUT; }
    public static void setPollingTimeout(int pollingTimeout) { POLLING_TIMEOUT = pollingTimeout; }

    @Override
    public void init()
    {
        Properties properties = new Properties();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, server);
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        properties.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, sessionTimeoutMs);
        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, autoCommit);
        properties.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, autoCommitIntervalMs);
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, deserializeClass);
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, deserializeClass);
        if(clientId != null)
            properties.put(ConsumerConfig.CLIENT_ID_CONFIG, clientId);
        consumer = new KafkaConsumer<>(properties);

        ctrlSignal = new AtomicBoolean(true);
    }

    protected String timestamp() {
        return Instant.now().toString();
    }

    public void setOutputQueue(Queue<String[]> outputQueue) {
        this.outputQueue = outputQueue;
    }

    @Override
    public void subscribe() {
        if(consumer != null) {
            if(topic == null || topic.isEmpty()) {
                throw new IllegalStateException("No Default Topic(s) for Subscription!");
            } else if(topic.contains(",") && topic.length() > 1) {
                String[] topics = topic.split(",");
                List<String> topicArray = Arrays.asList(topics);
                consumer.subscribe(topicArray);
            } else {
                consumer.subscribe(Collections.singletonList(topic));
            }
        } else {
            throw new IllegalStateException("Kafka Consumer Has Not Been Initialized!");
        }
    }


    @Override
    public void subscribe(final List<String> topics) {
        if(consumer != null) {
            if (topics != null && !topics.isEmpty()) {
                topics.removeIf(topic -> topic == null || topic.isEmpty());
                consumer.subscribe(topics);
            }
        } else {
            throw new IllegalStateException("Kafka Consumer Has Not Been Initialized!");
        }
    }

    @Override
    public void subscribe(Collection<String> topics, ConsumerRebalanceListener listener) {
        //TODO
    }


    @Override
    public void run() {
        if(consumer != null) {
            subscribe();                // Auto Subscribe to Default topics
            while (ctrlSignal.get()) {
                try{
                    ConsumerRecords<String, String> records = consumer.poll(POLLING_TIMEOUT);
                    if(records.isEmpty()) {
                        Thread.sleep(IDLE_PERIOD);
                        continue;
                    }

                    for(ConsumerRecord<String, String> record : records) {
                        logger.info("-> Received from Topic:[{}], Key:[{}], Offset:{}, Data:\n{}",
                                record.topic(), record.key(), record.offset(), record.value());
                        if(enableMessageQueue && outputQueue != null) {
                            String[] message = {record.topic(), record.key(), record.value()};
                            outputQueue.add(message);
                        }
                    }
                } catch (InterruptedException e) {
                    logger.error("-> Kafka Consumer Process Got Interrupted: " + e.getMessage());
                } catch (IllegalStateException err) {
                    logger.error("-> Kafka Consumer State Error: " + err.getMessage());
                    if(err.getMessage().contains("Consumer is not subscribed to any topics")) {
                        close();
                        throw new IllegalStateException(err);
                    }
                } catch (Exception err) {
                    logger.error("-> Kafka Consumer Polling Records Error: " + err.getMessage());
                }
            }
        } else {
            throw new IllegalStateException("Kafka Consumer Has Not Been Initialized!");
        }
    }

    @Override
    public void stop() {
        if(consumer != null)
            ctrlSignal.set(false);
    }

    @Override
    public void close() {
        if(consumer != null)
            consumer.close();
    }
}
