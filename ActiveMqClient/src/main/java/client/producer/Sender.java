package client.producer;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.activemq.ActiveMQConnectionFactory;

import java.util.concurrent.BlockingQueue;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Simple Straight Forward Queue (point-2-point) Send/Receive ActiveMQ module.
 * @author Jason/GeW
 * @since 2017-12-23
 */
public class Sender implements Runnable
{

    private final String brokerAddress;
    private final String queueName;
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

    private static final Logger logger = LoggerFactory.getLogger(Sender.class);
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


    public Sender(String brokerAddress, String queueName) {
        this.brokerAddress = brokerAddress;
        this.queueName = queueName;
    }

    public Sender(String brokerAddress, String queueName, String username, String password) {
        this.brokerAddress = brokerAddress;
        this.queueName = queueName;
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
            if(username == null && password == null)
                connectionFactory = new ActiveMQConnectionFactory(brokerAddress);
            else
                connectionFactory = new ActiveMQConnectionFactory(username, password, brokerAddress);
            connection = connectionFactory.createConnection();
            connection.start();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination destination = session.createQueue(queueName);
            producer = session.createProducer(destination);

            if(persist)
                producer.setDeliveryMode(DeliveryMode.PERSISTENT);
            else
                producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

            return true;

        } catch (JMSException e) {
            logger.error("=> ActiveMQ (P2P) Sender Initialize Failed: " + e.getMessage());
            return false;
        } catch (Exception err) {
            logger.error("=> ActiveMQ (P2P) Sender Initialize Exception: " + err.getMessage());
            return false;
        }
    }

    @Override
    public void run() {
        boolean flag = init();
        logger.error("=> ActiveMQ P2P Sender ({}) Client Initialized: {}", queueName, flag);

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
                        e.printStackTrace();
                    } catch (JMSException e) {
                        logger.error("=> ActiveMQ Sender Send Failed: " + e.getMessage());
                    } catch (NullPointerException err) {
                        logger.error("=> Invalid Message!");
                    } catch (Exception err) {
                        logger.error("=> Sender Exception on Sending Message: " + err.getMessage());
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
                logger.error(" => ActiveMQ Sender Send Failed: " + e.getMessage());
            } catch (NullPointerException err) {
                logger.error(" => Invalid Message!");
            } catch (Exception err) {
                logger.error(" => Sender Exception on Sending Message: " + err.getMessage());
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
//            logger.debug("=> ActiveMQ P2P Producer Closed.");
            return true;

        } catch (JMSException e) {
            logger.warn("=> Failure to Close ActiveMQ P2P Producer, Session or connection.");
            return false;
        }
    }
}
