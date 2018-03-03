package gew.kafka.logger.service;

import gew.kafka.logger.entity.LogMessage;
import gew.kafka.logger.entity.LogLevel;
import gew.kafka.logger.kafka.Producer;
import gew.kafka.logger.util.NetworkInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author Jason/Ge Wu
 */
public class KafkaLoggerManager implements KafkaLogger
{
    private static Producer producer;
    private static String LOCAL_IP;
    private static Integer PORT;
    private static String MAC;

    private String defaultTopic;

    private static KafkaLoggerManager instance;
    private static LogLevel RootLevel = LogLevel.ERROR;

    private static final Logger logger = LoggerFactory.getLogger(KafkaLoggerManager.class);

    public static KafkaLoggerManager getInstance() {
        if (instance == null) {
             instance = new KafkaLoggerManager();
        }
        return instance;
    }

    public static void setProducer(Producer producer) { KafkaLoggerManager.producer = producer; }

    public static void setLocalIp(String localIp) { LOCAL_IP = localIp; }

    public static void setPORT(Integer PORT) { KafkaLoggerManager.PORT = PORT; }

    public static void setMAC(String MAC) { KafkaLoggerManager.MAC = MAC; }

    public static void setRootLevel(LogLevel level)
    {
        RootLevel = level;
    }

    public void setDefaultTopic(String defaultTopic) { this.defaultTopic = defaultTopic; }

    private KafkaLoggerManager() {
/*        if(producer == null)
            throw new IllegalStateException("Must Initialize or Set Producer First!");*/
    }

    @Override
    public String timestamp() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        return now.format(formatter);
    }

    @Override
    public void getSystemInfo() {
        LOCAL_IP = NetworkInfo.getLocalIP();
        MAC = NetworkInfo.getMacAddress();
    }

    @Override
    public void logging(LogMessage logMessage) {
        if(logMessage != null) {
            if (logMessage.getLevel().compareTo(RootLevel) >= 0) {
                ObjectMapper mapper = new ObjectMapper();
                try {
                    String finalMessage = mapper.writeValueAsString(logMessage);
                    if (producer != null && defaultTopic != null) {
                        producer.sendMessage(defaultTopic, finalMessage, logMessage.getLevel().getValue());

                    } else if (producer != null) {
                        producer.sendKeyPairedMessage(logMessage.getLevel().getValue(), finalMessage);

                    } else {
                        System.err.println("Producer Has Not Been Initialized.");
                    }
                } catch (JsonProcessingException e) {
                    logger.error(e.getMessage());
                }
            }
        }
    }

    @Override
    public void logging(String topic, LogMessage logMessage) {
        if(logMessage != null && topic != null && !topic.isEmpty()) {
            if (logMessage.getLevel().compareTo(RootLevel) >= 0) {
                ObjectMapper mapper = new ObjectMapper();
                try {
                    String finalMessage = mapper.writeValueAsString(logMessage);
                    if (producer != null) {
                        producer.sendMessage(topic, finalMessage);
                    }
                } catch (JsonProcessingException err) {
                    logger.error(err.getMessage());
                }
            }
        }
    }

    @Override
    public void debug(String classFile, String methodName, String message) {
        debug(null, classFile, methodName, message);
    }

    @Override
    public void debug(String topic, String classFile, String methodName, String message) {
        if(message != null && !message.isEmpty()) {
            LogMessage logMessage = new LogMessage();
            logMessage.setTimestamp(timestamp());
            logMessage.setIp(LOCAL_IP);
            logMessage.setPort(PORT);
            logMessage.setMac(MAC);
            logMessage.setLevel(LogLevel.DEBUG);
            logMessage.setSource(classFile);
            logMessage.setMethodName(methodName);
            logMessage.setDetails(message);
            if(topic != null && !topic.isEmpty()) {
                logging(topic, logMessage);
            } else {
                logging(logMessage);
            }
        }
    }

    @Override
    public void info(String classFile, String methodName, String message) {
        info(null, classFile, methodName, message);
    }

    @Override
    public void info(String topic, String classFile, String methodName, String message) {
        if(message != null && !message.isEmpty()) {
            LogMessage logMessage = new LogMessage();
            logMessage.setTimestamp(timestamp());
            logMessage.setIp(LOCAL_IP);
            logMessage.setPort(PORT);
            logMessage.setMac(MAC);
            logMessage.setLevel(LogLevel.INFO);
            logMessage.setSource(classFile);
            logMessage.setMethodName(methodName);
            logMessage.setDetails(message);
            if(topic != null && !topic.isEmpty()) {
                logging(topic, logMessage);
            } else {
                logging(logMessage);
            }
        }
    }

    @Override
    public void warn(String classFile, String methodName, String message) {
        warn(null, classFile, methodName, message);
    }

    @Override
    public void warn(String topic, String classFile, String methodName, String message) {
        if(message != null && !message.isEmpty()) {
            LogMessage logMessage = new LogMessage();
            logMessage.setTimestamp(timestamp());
            logMessage.setIp(LOCAL_IP);
            logMessage.setPort(PORT);
            logMessage.setMac(MAC);
            logMessage.setLevel(LogLevel.WARN);
            logMessage.setSource(classFile);
            logMessage.setMethodName(methodName);
            logMessage.setDetails(message);
            if(topic != null && !topic.isEmpty()) {
                logging(topic, logMessage);
            } else {
                logging(logMessage);
            }
        }
    }

    @Override
    public void error(String classFile, String methodName, String message) {
        error(null, classFile, methodName, message);
    }

    @Override
    public void error(String topic, String classFile, String methodName, String message) {
        if(message != null && !message.isEmpty()) {
            LogMessage logMessage = new LogMessage();
            logMessage.setTimestamp(timestamp());
            logMessage.setIp(LOCAL_IP);
            logMessage.setPort(PORT);
            logMessage.setMac(MAC);
            logMessage.setLevel(LogLevel.ERROR);
            logMessage.setSource(classFile);
            logMessage.setMethodName(methodName);
            logMessage.setDetails(message);
            if(topic != null && !topic.isEmpty()) {
                logging(topic, logMessage);
            } else {
                logging(logMessage);
            }
        }
    }

    @Override
    public void fatal(String classFile, String methodName, String message) {
        fatal(null, classFile, methodName, message);
    }

    @Override
    public void fatal(String topic, String classFile, String methodName, String message) {
        if(message != null && !message.isEmpty()) {
            LogMessage logMessage = new LogMessage();
            logMessage.setTimestamp(timestamp());
            logMessage.setIp(LOCAL_IP);
            logMessage.setPort(PORT);
            logMessage.setMac(MAC);
            logMessage.setLevel(LogLevel.FATAL);
            logMessage.setSource(classFile);
            logMessage.setMethodName(methodName);
            logMessage.setDetails(message);
            if(topic != null && !topic.isEmpty()) {
                logging(topic, logMessage);
            } else {
                logging(logMessage);
            }
        }
    }

    @Override
    protected void finalize() throws Throwable {
        producer.stop();
        super.finalize();
    }
}
