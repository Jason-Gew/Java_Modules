package gew.pubsub.mqtt;

import gew.pubsub.util.NetworkInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * PubSub client based on Paho MQTT, utilized Builder design pattern for easy instantiation...
 * @author Jason/GeW
 * @since 2018-1-26
 */
public class Client implements BasicClient {

    private String broker;
    private Integer keepAlive;
    private boolean cleanSession;
    private String clientID;
    private Integer maxInFlight;
    private boolean enableLogin;
    private String username;
    private char[] password;
    private boolean enableSSL;
    private int pubQos;
    private int subQos;
    private boolean enableOutQueue;

    private MqttClient mqttClient;
    private MqttConnectOptions connectOps;
    private MessageType messageType;
    private Queue<MQTTMessage> messageQueue;

    private static final String URL_PREFIX = "tcp://";
    private static final String SSL_PREFIX = "ssl://";
    private static final Logger logger = LogManager.getLogger(Client.class);

    // Private Constructor, Invoked by Builder
    private Client() { }

    @Override
    public boolean isConnected() {
        return mqttClient != null && mqttClient.isConnected();
    }

    @Override
    public Boolean initialize() {
        if (mqttClient != null && mqttClient.isConnected()) {
            logger.error("MQTT client has been initialized, please disconnect first then re-initialize.");
            return false;
        } else if ( mqttClient != null && !mqttClient.isConnected()) {
            try {
                mqttClient.close();
                Thread.sleep(200);
                mqttClient = null;
                return initialize();
            } catch (MqttException err) {
                logger.fatal(err.getMessage());
                err.printStackTrace();
                return false;
            } catch (InterruptedException err) {
                logger.warn("Re-initialize Got Interrupted!");
                return false;
            }
        } else {
            try {
                if (broker.contains(URL_PREFIX) || broker.contains(SSL_PREFIX)) {
                    mqttClient = new MqttClient(broker, clientID);
                } else if (enableSSL) {
                    mqttClient = new MqttClient(SSL_PREFIX + broker, clientID);
                } else {
                    mqttClient = new MqttClient(URL_PREFIX + broker, clientID);
                }
                connectOps = new MqttConnectOptions();
                connectOps.setCleanSession(cleanSession);
                if (enableLogin) {
                    connectOps.setUserName(username);
                    connectOps.setPassword(password);
                }
                connectOps.setAutomaticReconnect(true);             // Must set AutomaticReconnect true;
                connectOps.setKeepAliveInterval(keepAlive);
                if (maxInFlight != null) {
                    connectOps.setMaxInflight(maxInFlight);
                }
                if (enableOutQueue) {
                    ClientCallback callback = new ClientCallback(messageQueue);
                    mqttClient.setCallback(callback);
                } else {
                    ClientCallback callback = new ClientCallback(messageType);
                    mqttClient.setCallback(callback);
                }
                return true;

            } catch (MqttException err) {
                logger.fatal("=> MQTT Client Initialization Error: " + err.getMessage());
                return false;
            }
        }
    }

    @Override
    public boolean connect() throws MqttException {
        if (connectOps != null) {
            mqttClient.connect(connectOps);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                logger.warn("Connection May be Interrupted!");
            }
            return isConnected();
        } else {
            throw new IllegalStateException("Initialize the Client Before Making Connection.");
        }
    }

    @Override
    public void disconnect() {
        try {
            mqttClient.disconnect();
        } catch (MqttException e) {
            logger.warn("=> MQTT Client Disconnection Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public Boolean publish(MQTTMessage mqttMessage) {
        return publish(mqttMessage.getTopic(), mqttMessage.getMessage(), pubQos, mqttMessage.getRetained());
    }

    @Override
    public Boolean publish(final String topic, final String payload) {
        return publish(topic, payload, pubQos);
    }

    @Override
    public Boolean publish(final String topic, final String payload, final int qos) {
        return publish(topic, payload.getBytes(StandardCharsets.UTF_8), qos, false);
    }

    @Override
    public Boolean publish(String topic, byte[] payload, int qos, boolean retain) {
        try {
            if (!mqttClient.isConnected() && connectOps != null) {
                mqttClient.connect(connectOps);
                logger.info("=> Re-established Connection~");
            }
            if (qos < 0 || qos > 2) {
                qos = pubQos;
            }
            mqttClient.publish(topic, payload, qos, retain);
            return true;

        } catch (MqttException err) {
            logger.error("=> MQTT Exception: " + err.getMessage());
            return false;
        }
    }

    @Override
    public void subscribe(final String topic) {
        subscribe(topic, subQos);
    }

    @Override
    public void subscribe(final String topic, final int qos) {
        try {
            if(qos < 0 || qos > 2) {
                mqttClient.subscribe(topic, subQos);
            } else {
                mqttClient.subscribe(topic, qos);
            }
        } catch (MqttException e) {
            logger.error("=> Subscribe Topic [{}] Failed: {}", topic, e.toString());
        }
    }

    @Override
    public void subscribe(List<String> topics) {
        topics.forEach(t -> subscribe(t, subQos));
    }

    @Override
    public void unsubscribe(final String topic) {
        try {
            mqttClient.unsubscribe(topic);
        } catch (MqttException e) {
            logger.error("=> Unsubscribe Topic [{}] Failed: {}", topic, e.toString());
        }
    }

    @Override
    public boolean cleanRetain(final String topic) {
        try {
            if (!mqttClient.isConnected() && connectOps != null)
                mqttClient.connect(connectOps);
            mqttClient.publish(topic, new byte[0], pubQos, true);
            return true;
        } catch (MqttException err) {
            logger.error("=> Clean Retained Message on Topic [{}] Failed: {}", topic, err.getMessage());
            return false;
        }
    }

    @Override
    public Queue<MQTTMessage> getMessageQueue() {
        if (enableOutQueue) {
            return messageQueue;
        } else {
            logger.error("Message Queue Has Not Been Enabled or Instantiated");
            return null;
        }
    }

    @Override
    public void setMessageQueue(Queue<MQTTMessage> messageQueue) {
        if (messageQueue != null)
            this.messageQueue = messageQueue;
    }

    /**
     * Client Builder class for easy instantiation and auto setting default parameters.
     * @author Jason/GeW
     */
    public static class Builder {

        private String broker;
        private Integer keepAlive;
        private Boolean cleanSession;
        private Integer maxInFlight;
        private String clientID;
        private Boolean enableLogin;
        private String username;
        private String password;
        private Boolean enableSSL;
        private Integer pubQos;
        private Integer subQos;
        private MessageType messageType;
        private Boolean enableOutQueue;

        public Builder() { }

        public Builder setBroker(String broker) {
            this.broker = broker;
            return this;
        }

        public Builder setKeepAlive(Integer keepAlive) {
            this.keepAlive = keepAlive;
            return this;
        }

        public Builder setCleanSession(Boolean cleanSession) {
            this.cleanSession = cleanSession;
            return this;
        }

        public Builder setClientID(String clientID) {
            this.clientID = clientID;
            return this;
        }

        public Builder setMaxInFlight(Integer maxInFlight) {
            this.maxInFlight = maxInFlight;
            return this;
        }

        public Builder setEnableLogin(Boolean enableLogin) {
            this.enableLogin = enableLogin;
            return this;
        }

        public Builder setUsername(String username) {
            this.username = username;
            return this;
        }

        public Builder setPassword(String password) {
            this.password = password;
            return this;
        }

        public Builder setEnableSSL(Boolean enableSSL) {
            this.enableSSL = enableSSL;
            return this;
        }

        public Builder setPubQos(Integer pubQos) {
            this.pubQos = pubQos;
            return this;
        }

        public Builder setSubQos(Integer subQos) {
            this.subQos = subQos;
            return this;
        }

        public Builder setMessageType(MessageType messageType) {
            this.messageType = messageType;
            return this;
        }

        public Builder setEnableOutQueue(Boolean enableOutQueue) {
            this.enableOutQueue = enableOutQueue;
            return this;
        }

        public Client build() {
            Client client = new Client();
            if (this.broker != null && !this.broker.isEmpty()) {
                client.broker = this.broker;
            } else {
                throw new IllegalArgumentException("Broker Address Cannot Be null or Empty!");
            }

            if (this.cleanSession != null) {
                client.cleanSession = this.cleanSession;
            } else {
                client.cleanSession = true;
            }

            if (this.keepAlive != null && this.keepAlive > 15) {
                client.keepAlive = this.keepAlive;
            } else {
                client.keepAlive = 60;
            }

            if (this.clientID != null && !this.clientID.isEmpty()) {
                client.clientID = this.clientID;
            } else {
                String mac = NetworkInfo.getMacAddress();
                try {
                    MessageDigest md5 = MessageDigest.getInstance("MD5");
                    md5.update(mac.getBytes());
                    mac = Base64.getEncoder().encodeToString(md5.digest());
                } catch (NoSuchAlgorithmException e) {
                    mac = Long.valueOf(System.currentTimeMillis()).toString();
                }
                client.clientID = mac;
            }

            if (this.maxInFlight != null && this.maxInFlight > 10)
                client.maxInFlight = this.maxInFlight;

            if (this.enableLogin != null && this.enableLogin) {
                client.enableLogin = true;
                client.username = this.username;
                if (this.password != null) {
                    client.password = this.password.toCharArray();
                } else {
                    client.password = new char[0];
                }
            } else {
                client.enableLogin = false;
            }

            if (this.enableSSL != null) {
                client.enableSSL = this.enableSSL;
            } else {
                client.enableSSL = false;
            }

            if (this.messageType != null) {
                client.messageType = this.messageType;
            } else {
                client.messageType = MessageType.PLAIN_TEXT;
            }

            if (this.pubQos != null && this.pubQos >= 0 && this.pubQos <= 2) {
                client.pubQos = this.pubQos;
            } else {
                System.err.println("Invalid Default Publish QoS ["+this.pubQos+"], System set to 0");
                client.pubQos = 0;
            }

            if (this.subQos != null && this.subQos >= 0 && this.subQos <= 2) {
                client.subQos = this.subQos;
            } else {
                System.err.println("Invalid Default Subscribe QoS ["+this.subQos+"], System set to 0");
                client.subQos = 0;
            }

            if (this.enableOutQueue != null && this.enableOutQueue) {
                client.enableOutQueue = true;
                if (client.messageQueue == null) {
                    client.messageQueue = new ConcurrentLinkedQueue<>();
                }
            } else {
                client.enableOutQueue = false;
            }
            return client;
        }
    }

}