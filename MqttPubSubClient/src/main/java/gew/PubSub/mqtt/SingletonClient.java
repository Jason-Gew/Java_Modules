package gew.pubsub.mqtt;


import gew.pubsub.config.MQTTClientConfig;
import gew.pubsub.util.NetworkInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.List;
import java.util.Queue;


/**
 * Thread-safe Singleton MQTT Client, only allow one instance which is created by first time calling getInstance()...
 * @author Jason/GeW
 * @since 2017-08-20
 */
public class SingletonClient implements BasicClient {

    private static MQTTClientConfig clientConfig;
    private static volatile SingletonClient client;

    private MqttConnectOptions connectOps;
    private MqttClient mqttClient;
    private Queue<MQTTMessage> messageQueue;

    private static final String URL_PREFIX = "tcp://";
    private static final String SSL_PREFIX = "ssl://";
    private static final Logger logger = LogManager.getLogger(SingletonClient.class);

    private SingletonClient () {
        if (clientConfig == null)
            throw new IllegalArgumentException("Invalid MQTT Client Config, Invoke setConfig First.");
    }

    public static SingletonClient getInstance() {
        if (client == null) {
            synchronized (SingletonClient.class) {
                if (client == null)
                    client = new SingletonClient();
            }
        }
        return client;
    }

    public static void setClientConfig(MQTTClientConfig clientConfig) {
        SingletonClient.clientConfig = clientConfig;
    }

    @Override
    public boolean isConnected() {
        return mqttClient != null && mqttClient.isConnected();
    }

    @Override
    public Boolean initialize() {
        if (mqttClient != null && mqttClient.isConnected()) {
            logger.error("MQTT client has been initialized, please disconnect first then re-initialize.");
            return false;
        } else if (mqttClient != null && !mqttClient.isConnected()) {
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
                if (clientConfig.getClientID() == null || clientConfig.getClientID().isEmpty()) {
                    String mac = NetworkInfo.getMacAddress();
                    try {
                        MessageDigest md5 = MessageDigest.getInstance("MD5");
                        md5.update(mac.getBytes());
                        mac = Base64.getEncoder().encodeToString(md5.digest());
                    } catch (NoSuchAlgorithmException e) {
                        mac = Long.valueOf(System.currentTimeMillis()).toString();
                    }
                    clientConfig.setClientID(mac);
                    System.err.println("Default MQTT Client ID: " + mac);
                }

                if (clientConfig.getBroker() == null || clientConfig.getBroker().isEmpty()) {
                    throw new IllegalArgumentException ("Broker Address Cannot Be null or Empty!");
                } else if (clientConfig.getBroker().contains(URL_PREFIX) || clientConfig.getBroker().contains(SSL_PREFIX)) {
                    mqttClient = new MqttClient(clientConfig.getBroker(), clientConfig.getClientID());
                } else if (clientConfig.getEnableSSL() != null && clientConfig.getEnableSSL()) {
                    mqttClient = new MqttClient(SSL_PREFIX + clientConfig.getBroker(), clientConfig.getClientID());
                } else {
                    mqttClient = new MqttClient(URL_PREFIX + clientConfig.getBroker(), clientConfig.getClientID());
                }

                connectOps = new MqttConnectOptions();

                if(clientConfig.getCleanSession() == null) {
                    clientConfig.setCleanSession(true);
                } else {
                    connectOps.setCleanSession(clientConfig.getCleanSession());
                }

                if (clientConfig.getEnableLogin() != null && clientConfig.getEnableLogin()) {
                    connectOps.setUserName(clientConfig.getUsername());
                    connectOps.setPassword(clientConfig.getPassword().toCharArray());
                }

                if (clientConfig.getKeepAlive() == null || clientConfig.getKeepAlive() <= 15) {
                    clientConfig.setKeepAlive(60);
                } else {
                    connectOps.setKeepAliveInterval(clientConfig.getKeepAlive());
                }

                if (clientConfig.getMaxInFlight() != null && clientConfig.getMaxInFlight() > 10) {
                    connectOps.setMaxInflight(clientConfig.getMaxInFlight());
                }

                connectOps.setAutomaticReconnect(true);     // Auto-reconnection must be set true;

                if (clientConfig.getEnableOutQueue() != null && clientConfig.getEnableOutQueue()
                        && messageQueue != null) {
                    mqttClient.setCallback(new ClientCallback(messageQueue));
                } else {
                    mqttClient.setCallback(new ClientCallback(MessageType.PLAIN_TEXT));
                }
                return true;

            } catch (MqttException err) {
                logger.fatal("=> MQTT Client Initialize Error: " + err.getMessage());
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
        if (clientConfig.getPubQos() == null || clientConfig.getPubQos() < 0 || clientConfig.getPubQos() > 2)
            clientConfig.setPubQos(1);
        return publish(mqttMessage.getTopic(), mqttMessage.getMessage(),
                clientConfig.getPubQos(), mqttMessage.getRetained());
    }

    @Override
    public Boolean publish(final String topic, final String payload) {
        if (clientConfig.getPubQos() == null || clientConfig.getPubQos() < 0 || clientConfig.getPubQos() > 2)
            clientConfig.setPubQos(1);
        return publish(topic, payload, clientConfig.getPubQos());
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
                qos = clientConfig.getPubQos();
            }
            mqttClient.publish(topic, payload, qos, retain);
            return true;

        } catch (MqttException err) {
            logger.error("=> MQTT Exception: " + err.getMessage());
            return false;
        }
    }

    @Override
    public void subscribe(List<String> topics) {
        if (clientConfig.getSubQos() == null || clientConfig.getSubQos() < 0 || clientConfig.getSubQos() > 2) {
            clientConfig.setSubQos(1);
        }
        topics.forEach(this::subscribe);
    }

    @Override
    public void subscribe(final String topic) {
        if (clientConfig.getSubQos() == null || clientConfig.getSubQos() < 0 || clientConfig.getSubQos() > 2) {
            clientConfig.setSubQos(1);
        }
        subscribe(topic, clientConfig.getSubQos());

    }

    @Override
    public void subscribe(final String topic, final int qos) {
        try {
            if (qos < 0 || qos > 2) {
                mqttClient.subscribe(topic, (clientConfig.getSubQos()));
            } else {
                mqttClient.subscribe(topic, qos);
            }
        } catch (MqttException e) {
            logger.error("=> Subscribe Topic [{}] Failed: {}", topic, e.toString());
        }
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
            if (!mqttClient.isConnected() && connectOps != null) {
                mqttClient.connect(connectOps);
            }
            mqttClient.publish(topic, new byte[0], clientConfig.getPubQos(), true);
            return true;

        } catch (MqttException err) {
            logger.error("=> Clean Retained Message on Topic [{}] Failed: {}", topic, err.getMessage());
            return false;
        }
    }

    @Override
    public Queue<MQTTMessage> getMessageQueue() {
        if (clientConfig.getEnableOutQueue() != null && clientConfig.getEnableOutQueue()) {
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
}
