package client.producer;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.activemq.ActiveMQConnectionFactory;

/**
 * Simple Publish/Subscribe on Topic(s) ActiveMQ module.
 * @author Jason/GeW
 * @since 2017-12-24
 */
public class Publisher implements Runnable
{

    private final String brokerAddress;
    private final String topic;
    private String username;
    private String password;
    private Connection connection;
    private MessageProducer producer;
    private Session session;
    private boolean persist;

    private boolean enableQueue;
    private BlockingQueue<String> messageQueue;
    private String message;
    private boolean signal = true;

    private static final Logger logger = LoggerFactory.getLogger(Publisher.class);
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public Publisher(String brokerAddress, String topic) {
        this.brokerAddress = brokerAddress;
        this.topic = topic;
    }

    public Publisher(String brokerAddress, String topic, String username, String password) {
        this.brokerAddress = brokerAddress;
        this.topic = topic;
        this.username = username;
        this.password = password;
    }

    /**
     * For continuous sending String message within same thread.
     * @return boolean Queue enable status
     */
    public boolean isEnableQueue() { return enableQueue; }
    public void setEnableQueue(final boolean enableQueue) { this.enableQueue = enableQueue; }

    public boolean isPersist() { return persist; }
    public void setPersist(final boolean persist) { this.persist = persist; }

    public void setMessageQueue(BlockingQueue<String> messageQueue) { this.messageQueue = messageQueue; }

    /**
     * Single Message for sending to the Queue, support multi-threading.
     * @param message String message
     */
    public void setMessage(final String message) { this.message = message; }

    public void setSignal(final boolean signal) { this.signal = signal; }

    private static String currentDatetime()
    {
        LocalDateTime now = LocalDateTime.now();
        return FORMATTER.format(now);
    }

    private Boolean init()
    {
        ActiveMQConnectionFactory connectionFactory;
        try {
            if (username == null && password == null)
                connectionFactory = new ActiveMQConnectionFactory(brokerAddress);
            else
                connectionFactory = new ActiveMQConnectionFactory(username, password, brokerAddress);
            connection = connectionFactory.createConnection();
            connection.start();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination destination = session.createTopic(topic);
            producer = session.createProducer(destination);
            if(persist)
                producer.setDeliveryMode(DeliveryMode.PERSISTENT);
            else
                producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

            return true;

        } catch (JMSException e) {
            logger.error("=> ActiveMQ Publisher Initialize Failed: " + e.getMessage());
            return false;
        } catch (Exception err) {
            logger.error("=> ActiveMQ Publisher Initialize Exception: " + err.getMessage());
            return false;
        }
    }

    @Override
    public void run() {
        boolean flag = init();
        logger.info("=> ActiveMQ Publisher ({}) Client Initialized: {}", topic, flag);

        if(enableQueue && messageQueue != null)
        {
            while (signal)
            {
                if(!messageQueue.isEmpty())
                {
                    try {
                        message = messageQueue.take();
                        TextMessage msg = session.createTextMessage(message);
                        producer.send(msg);

                    } catch (InterruptedException e) {
                       logger.error("=> ActiveMQ Publisher from Queue Got Interrupted...");
                    } catch (JMSException e) {
                        logger.error("=> ActiveMQ Publisher Send Failed: {}", e.getMessage());
                    } catch (NullPointerException err) {
                        logger.error("=> Invalid Message!");
                    } catch (Exception err) {
                        logger.error("=> ActiveMQ Exception on Sending Message: {}", err.getMessage());
                    }
                }
            }
        }
        else
        {
            try {
                TextMessage msg = session.createTextMessage(message);
                producer.send(msg);
            } catch (JMSException e) {
                logger.error("=> ActiveMQ Publisher Send Failed: " + e.getMessage());
            } catch (NullPointerException err) {
                logger.error("=> Invalid Message!");
            } catch (Exception err) {
                logger.error("=> ActiveMQ Exception on Sending Message: " + err.getMessage());
            }
        }
    }

    public boolean close() {
        try {
            if(producer != null)
                producer.close();
            if(session != null && connection != null) {
                session.close();
                connection.close();
            }
 //           logger.debug("=> ActiveMQ Publisher Closed.");
            return true;

        } catch (JMSException e) {
            logger.warn("=> Failure to Close ActiveMQ Producer, Session or connection.");
            return false;
        }
    }
}
