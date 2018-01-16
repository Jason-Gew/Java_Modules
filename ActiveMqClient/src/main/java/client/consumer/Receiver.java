package client.consumer;

import javax.jms.*;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.activemq.ActiveMQConnectionFactory;

/**
 * Simple Straight Forward Queue (point-2-point) Send/Receive ActiveMQ module.
 * @author Jason/GeW
 * @since 2017-12-23
 */
public class Receiver implements Runnable, MessageListener, ExceptionListener
{

    private final String brokerAddress;
    private final String queueName;
    private String username;
    private String password;
    private Connection connection;
    private MessageConsumer consumer;
    private Session session;


    private static final Logger logger = LoggerFactory.getLogger(Receiver.class);
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


    public Receiver(String brokerAddress, String queueName) {
        this.brokerAddress = brokerAddress;
        this.queueName = queueName;
    }

    public Receiver(String brokerAddress, String queueName, String username, String password) {
        this.brokerAddress = brokerAddress;
        this.queueName = queueName;
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
            if(username == null && password == null)
                connectionFactory = new ActiveMQConnectionFactory(brokerAddress);
            else
                connectionFactory = new ActiveMQConnectionFactory(username, password, brokerAddress);
            connection = connectionFactory.createConnection();
            connection.start();
            connection.setExceptionListener(this);
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination destination = session.createQueue(queueName);
            consumer = session.createConsumer(destination);
            consumer.setMessageListener(this);

            return true;

        } catch (JMSException e) {
            logger.error("-> ActiveMQ (P2P) Receiver Initialize Failed: " + e.getMessage());
            return false;
        } catch (Exception err) {
            logger.error("-> ActiveMQ (P2P) Receiver Initialize Exception: " + err.getMessage());
            return false;
        }
    }

    @Override
    public void run() {

        Boolean flag = init();
        logger.info("-> ActiveMQ P2P Receiver ({}) Client Initialized: {}", queueName, flag);

    }

    @Override
    public void onMessage(Message message) {
        String messageText;
        try {
            if(message instanceof TextMessage)          // Dealing with message in String format.
            {
                TextMessage textMessage = (TextMessage) message;
                messageText = textMessage.getText();
                logger.info("-> Queue [{}]: {}", queueName, messageText);
            }
            else if(message instanceof BytesMessage)    // Dealing with message in Bytes.
            {
                BytesMessage byteMessage = (BytesMessage) message;
                byte[] payload = new byte[(int)byteMessage.getBodyLength()];
                int readLength = byteMessage.readBytes(payload);
                try {
                    messageText = new String(payload, "UTF-8");
                    logger.info("-> Queue [{}]: {}", queueName, messageText);

                } catch (UnsupportedEncodingException e) {
                    logger.error("-> Unable to Decode Message from Queue [{}]: {}", queueName, e.getMessage());
                }

            }
            else    // Other type of message require customized casting or deserialization.
            {
                logger.error("-> Unable to Decode Unknown Message Format on Queue [{}]", queueName);
            }
        } catch (JMSException e) {
            logger.error("-> ActiveMQ Receiver onMessage Process Failed: " + e.getMessage());
        }
    }

    @Override
    public void onException(JMSException e) {
        logger.error("-> ActiveMQ Exception Occurred.  Shutting Down Receiver Client.");
    }

    public boolean close() {
        try {
            if(consumer != null)
                consumer.close();
            if(session != null && connection != null) {
                session.close();
                connection.close();
            }
//            logger.debug("-> ActiveMQ P2P Receiver Closed.");
            return true;

        } catch (JMSException e) {
            logger.warn("-> Failure in Close ActiveMQ P2P Consumer, Session or connection.");
            return false;
        }
    }
}
