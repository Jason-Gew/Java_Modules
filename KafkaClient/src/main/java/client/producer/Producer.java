package client.producer;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Apache Kafka Producer, support single instance and independent thread + input message queue.
 * @author Jason/GeW
 */
public class Producer implements KfkProducer, Runnable
{
    String server;
    String topic;
    String clientId;
    String retries;
    String acknowledge;
    String serializeClass;
    Boolean enableMessageQueue;

    private AtomicBoolean ctrlSignal;
    private KafkaProducer<String, String> producer;
    private BlockingQueue<String[]> incomingQueue;
    private static final Logger logger = LoggerFactory.getLogger(Producer.class);

    Producer() { }     // Package-private constructor for Builder class to instantiate.


    @Override
    public void init()
    {
        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, server);
        properties.put(ProducerConfig.CLIENT_ID_CONFIG, clientId);
        properties.put(ProducerConfig.RETRIES_CONFIG, retries);
        properties.put(ProducerConfig.ACKS_CONFIG, acknowledge);
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, serializeClass);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, serializeClass);
        producer = new KafkaProducer<>(properties);

        if(enableMessageQueue) {
            incomingQueue = new LinkedBlockingQueue<>();
            ctrlSignal = new AtomicBoolean(true);
        }
    }

    @Override
    public String timestamp() {
        return Instant.now().toString();
    }

    @Override
    public boolean sendMessage(String message) {
        boolean status = false;
        try {
            if(producer != null && message != null) {
                ProducerRecord<String, String> record = new ProducerRecord<>(topic, message);
                producer.send(record);
                status = true;
            } else if (producer == null) {
                logger.error("Kafka Producer Has Not Been Initialized...");
            } else {
                logger.error("Invalid Message!");
            }
        } catch (Exception err) {
            logger.error("Kafka Producer Send Error: " + err.getMessage());
        }
        return status;
    }

    @Override
    public boolean sendMessage(String topic, String message) {
        boolean status = false;
        try {
            if(producer != null && message != null && topic != null) {
                ProducerRecord<String, String> record = new ProducerRecord<>(topic, message);
                producer.send(record);
                status = true;
            } else if (producer == null) {
                logger.error("Kafka Producer Has Not Been Initialized...");
            } else {
                logger.error("Invalid Topic or Message!");
            }
        } catch (Exception err) {
            logger.error("Kafka Producer Send Error: " + err.getMessage());
        }
        return status;
    }

    @Override
    public boolean sendMessage(String topic, String message, String key) {
        boolean status = false;
        try {
            if(producer != null && message != null && topic != null) {
                ProducerRecord<String, String> record = new ProducerRecord<>(topic, key, message);
                producer.send(record);
                status = true;
            } else if (producer == null) {
                logger.error("Kafka Producer Has Not Been Initialized...");
            } else {
                logger.error("Invalid Topic or Message!");
            }
        } catch (Exception err) {
            logger.error("Kafka Producer Send Error: " + err.getMessage());
        }
        return status;
    }


    public BlockingQueue<String[]> getIncomingQueue() {
        if(enableMessageQueue && incomingQueue != null) {
            return incomingQueue;
        }
        else {
            throw new IllegalArgumentException("Message Queue Has Not Been Initialized!");
        }
    }

    @Override
    public void run() {
        if(producer != null && incomingQueue != null) {
            while(ctrlSignal.get()) {
                if(!incomingQueue.isEmpty()) {
                    try {
                        String[] message = incomingQueue.take();
                        switch (message.length)
                        {
                            case 1: sendMessage(message[0]);                        //[0]: message
                                    break;
                            case 2: sendMessage(message[0], message[1]);            //[0]: topic, [1]: message
                                    break;
                            case 3: sendMessage(message[0], message[1], message[2]); //[0]: topic, [1]: message, [2]: key
                                    break;
                            default: logger.error("Invalid Message Array Length: " + message.length);
                        }
                    } catch (InterruptedException e) {
                        logger.warn("Kafka Producer Take Message From Incoming Queue Got Interrupted...");
                    } catch (Exception err) {
                        logger.error("Error in Kafka Producer Queue Mode: " + err.getMessage());
                    }
                } else {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        logger.error("Kafka Producer Idle Got Interrupted: " + e.getMessage());
                    }
                }
            }
        } else {
            throw new IllegalStateException("Kafka Producer or MessageQueue Has Not Been Initialized!");
        }
    }

    public void stop()
    {
        if(producer != null && enableMessageQueue) {
            ctrlSignal.set(false);
        }
    }

    @Override
    public void close() {
        if(producer != null) {
            producer.close();
        }
    }
}
