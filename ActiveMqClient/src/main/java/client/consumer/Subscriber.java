package client.consumer;

import javax.jms.BytesMessage;
import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.TextMessage;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.activemq.ActiveMQConnectionFactory;

/**
 * Simple Publish/Subscribe on Topic(s) ActiveMQ module.
 * @author Jason/GeW
 * @since 2017-12-24
 */
public class Subscriber implements Runnable, MessageListener, ExceptionListener
{
    private final String brokerAddress;
    private final String topic;
    private String username;
    private String password;
    private Connection connection;
    private MessageConsumer consumer;
    private Session session;

    private static final Logger logger = LoggerFactory.getLogger(Subscriber.class);
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public Subscriber(String brokerAddress, String topic) {
        this.brokerAddress = brokerAddress;
        this.topic = topic;
    }

    public Subscriber(String brokerAddress, String topic, String username, String password) {
        this.brokerAddress = brokerAddress;
        this.topic = topic;
        this.username = username;
        this.password = password;
    }

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
            connection.setExceptionListener(this);
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination destination = session.createTopic(topic);
            consumer = session.createConsumer(destination);
            consumer.setMessageListener(this);

            return true;

        } catch (JMSException e) {
            logger.error("-> ActiveMQ Subscriber Initialize Failed: " + e.getMessage());
            return false;
        } catch (Exception err) {
            logger.error("-> ActiveMQ Subscriber Initialize Exception: " + err.getMessage());
            return false;
        }
    }

    @Override
    public void run() {
        Boolean flag = init();
        logger.info("-> ActiveMQ Subscriber ({}) Client Initialized: {}", topic, flag);
    }

    @Override
    public void onException(JMSException e) {
        logger.error("-> ActiveMQ Exception Occurred.  Shutting Down Receiver Client.");
    }

    @Override
    public void onMessage(Message message) {
        String messageText;
        try {
            if(message instanceof TextMessage)          // Dealing with message in String format.
            {
                TextMessage textMessage = (TextMessage) message;
                messageText = textMessage.getText();
                logger.info("-> Topic [{}]: {}", topic, messageText);
            }
            else if(message instanceof BytesMessage)    // Dealing with message in Bytes.
            {
                BytesMessage byteMessage = (BytesMessage) message;
                byte[] payload = new byte[(int)byteMessage.getBodyLength()];
                int readLength = byteMessage.readBytes(payload);
                try {
                    messageText = new String(payload, "UTF-8");
                    logger.info("-> Topic [{}]: {}", topic, messageText);

                } catch (UnsupportedEncodingException e) {
                    logger.error("-> Unable to Decode Message from Topic [{}]: {}", topic, e.getMessage());
                }
            }
            else    // Other type of message require customized casting or deserialization.
            {
                logger.error("-> Unable to Decode Unknown Message Format on Topic [{}]", topic);
            }
        } catch (JMSException e) {
            logger.error("-> ActiveMQ Subscriber onMessage Process Failed: " + e.getMessage());
        }
    }

    public boolean close() {
        try {
            if(consumer != null)
                consumer.close();
            if(session != null && connection != null) {
                session.close();
                connection.close();
            }
//            logger.debug("-> ActiveMQ Subscriber Closed.");
            return true;

        } catch (JMSException e) {
            logger.warn("-> Failure in Close ActiveMQ Consumer, Session or connection.");
            return false;
        }
    }
}
