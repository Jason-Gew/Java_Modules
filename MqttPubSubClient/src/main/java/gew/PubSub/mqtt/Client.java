package gew.PubSub.mqtt;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * PubSub client based on Paho MQTT, utilized Builder design pattern for easy instantiation...
 * @author Jason/GeW
 * @since 2018-1-26
 */
public class Client implements BasicClient
{
    private String broker;
    private Integer keepAlive;
    private boolean cleanSession;
    private String clientID;
    private boolean autoReconnect;
    private boolean enableLogin;
    private String username;
    private char[] password;
    private int pubQos;
    private int subQos;
    private List<String> autoPubTopics;
    private List<String> autoSubTopics;
    private boolean enableOutQueue;

    private MqttClient mqttClient;
    private MqttConnectOptions connectOps;
    private Queue<String[]> messageQueue;

    private static final String URL_PREFIX = "tcp://";
    private static final Logger logger = LogManager.getLogger(Client.class);


    private Client()        // Private Constructor, Invoked by Builder
    {

    }

    @Override
    public boolean isConnected()
    {
        return mqttClient != null && mqttClient.isConnected();
    }

    @Override
    public Boolean initialize()
    {
        if (mqttClient != null && mqttClient.isConnected()) {
            logger.error("MQTT client has been initialized, please disconnect first then re-initialize.");
            return false;
        } else if ( mqttClient != null && !mqttClient.isConnected()) {
            try {
                mqttClient.close();
                Thread.sleep(500);
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
                if(broker.contains(URL_PREFIX))
                    mqttClient = new MqttClient(broker, clientID);
                else
                    mqttClient = new MqttClient(URL_PREFIX + broker, clientID);
                connectOps = new MqttConnectOptions();
                connectOps.setCleanSession(cleanSession);
                if(enableLogin)
                {
                    connectOps.setUserName(username);
                    connectOps.setPassword(password);
                }
                connectOps.setAutomaticReconnect(autoReconnect);     // Better to set AutomaticReconnect true;

                connectOps.setKeepAliveInterval(keepAlive);
                if(enableOutQueue) {
//                    messageQueue = new LinkedBlockingQueue<>();     // Instantiate LinkedBlockingQueue
                    messageQueue = new ConcurrentLinkedQueue<>();     // Instantiate ConcurrentLinkedQueue
                    ClientCallback callback = new ClientCallback(mqttClient, messageQueue);
                    callback.setAutoReconnect(autoReconnect);
                    mqttClient.setCallback(callback);
                } else {
                    ClientCallback callback = new ClientCallback(mqttClient);
                    callback.setAutoReconnect(autoReconnect);
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
    public boolean connect() throws MqttException
    {
        if(connectOps != null)
        {
            mqttClient.connect(connectOps);
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                logger.warn("Connection May be Interrupted!");
            }
            return isConnected();
        } else {
            throw new IllegalStateException("Initialize the Client Before Making Connection.");
        }
    }

    @Override
    public void disconnect()
    {
        try
        {
            mqttClient.disconnect();
        }
        catch (MqttException e)
        {
            logger.warn("=> MQTT Client Disconnection Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public Boolean publish(final String topic, final String payload)
    {
        return publish(topic, payload, pubQos);
    }
    @Override
    public Boolean publish(final String topic, final String payload, final int qos) // Message will lose if connection break.
    {
        try
        {
            // The below mechanism will help connect when connection lost...
            if(!mqttClient.isConnected() && connectOps != null) {
                mqttClient.connect(connectOps);
                logger.info("=> Re-established Connection~");
            }

            mqttClient.publish(topic, payload.getBytes("UTF-8"), qos, false);
            return true;
        }
        catch(UnsupportedEncodingException err)
        {
            logger.error("UnSupported String Format: " + err.getMessage());
            return false;
        }
        catch(MqttException err)
        {
            logger.error("=> Exception during publish: " + err.getMessage());
            return false;
        }
    }

    /**
     * This method will broadcast the message to all auto publish topics!
     * @param message String message
     */
    @Override
    public void autoPublish(final String message)
    {
        for(String topic : autoPubTopics)
            publish(topic, message);
    }

    @Override
    public void subscribe(final String topic)
    {
        subscribe(topic, subQos);
    }
    @Override
    public void subscribe(final String topic, final int qos)
    {
        try
        {
            if(qos < 0 || qos > 2)
                mqttClient.subscribe(topic, subQos);
            else
                mqttClient.subscribe(topic, qos);
        }
        catch (MqttException e)
        {
            logger.error("=> Subscribe Topic [{}] Failed: {}", topic, e.toString());
        }
    }

    /**
     * This method will subscribe all topics in auto subscribe topics list!
     */
    @Override
    public void autoSubscribe()
    {
        for(String topic : autoSubTopics)
            subscribe(topic);
    }

    @Override
    public void unsubscribe(final String topic)
    {
        try {
            mqttClient.unsubscribe(topic);
        } catch (MqttException e) {
            logger.error("=> Unsubscribe Topic [{}] Failed: {}", topic, e.toString());
        }
    }

    @Override
    public boolean cleanRetain(final String topic)
    {
        try
        {
            if(!mqttClient.isConnected() && connectOps != null)
                mqttClient.connect(connectOps);

            mqttClient.publish(topic, "".getBytes(), pubQos, true);
            return true;
        } catch (MqttException err) {
            logger.error("=> Clean Retained Message on Topic [{}] Failed: {}", topic, err.getMessage());
            return false;
        }
    }

    @Override
    public Queue<String[]> getMessageQueue() {
        if(enableOutQueue)
            return messageQueue;
        else
            throw new IllegalStateException("Message Queue has not been enabled!");
    }


    /**
     * Client Builder class for easy instantiation and auto setting default parameters.
     * @author Jason/GeW
     */
    public static class Builder
    {
        private String broker;
        private Integer keepAlive;
        private Boolean cleanSession;
        private String clientID;
        private Boolean autoReconnect;
        private Boolean enableLogin;
        private String username;
        private String password;
        private Integer pubQos;
        private Integer subQos;
        private List<String> autoPubTopics;
        private List<String> autoSubTopics;
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

        public Builder setAutoReconnect(Boolean autoReconnect) {
            this.autoReconnect = autoReconnect;
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

        public Builder setPubQos(Integer pubQos) {
            this.pubQos = pubQos;
            return this;
        }

        public Builder setSubQos(Integer subQos) {
            this.subQos = subQos;
            return this;
        }

        public Builder setAutoPubTopics(List<String> autoPubTopics) {
            this.autoPubTopics = autoPubTopics;
            return this;
        }

        public Builder setAutoSubTopics(List<String> autoSubTopics) {
            this.autoSubTopics = autoSubTopics;
            return this;
        }

        public Builder setEnableOutQueue(Boolean enableOutQueue) {
            this.enableOutQueue = enableOutQueue;
            return this;
        }

        public Client build() {
            Client client = new Client();
            if(this.broker != null && !this.broker.isEmpty()) {
                client.broker = this.broker;
            } else {
                throw new IllegalArgumentException("Broker Address Cannot Be null or Empty!");
            }

            if(this.cleanSession != null)
                client.cleanSession = this.cleanSession;
            else
                client.cleanSession = true;

            if(this.keepAlive != null && this.keepAlive > 15)
                client.keepAlive = this.keepAlive;
            else
                client.keepAlive = 60;

            if(this.clientID != null && !this.clientID.isEmpty())
                client.clientID = this.clientID;
            else
                client.clientID = "Default-ClientID:" + System.currentTimeMillis()/1000;

            if(this.autoReconnect != null)
                client.autoReconnect = this.autoReconnect;
            else
                client.autoReconnect = true;

            if(this.enableLogin != null)
                client.enableLogin = this.enableLogin;
            else
                client.enableLogin = false;

            client.username = this.username;

            if(this.password != null) {
                client.password = this.password.toCharArray();
            } else {
                client.password = new char[0];
            }

            if(this.pubQos != null && this.pubQos >= 0 && this.pubQos <= 2) {
                client.pubQos = this.pubQos;
            } else {
                System.err.println("Invalid Default Publish QoS ["+this.pubQos+"], System set to 0");
                client.pubQos = 0;
            }

            if(this.subQos != null && this.subQos >= 0 && this.subQos <= 2) {
                client.subQos = this.subQos;
            } else {
                System.err.println("Invalid Default Subscribe QoS ["+this.subQos+"], System set to 0");
                client.subQos = 0;
            }

            if(this.autoPubTopics != null)
                client.autoPubTopics = this.autoPubTopics;
            else
                client.autoPubTopics = new ArrayList<>();

            if(this.autoSubTopics != null)
                client.autoSubTopics = this.autoSubTopics;
            else
                client.autoSubTopics = new ArrayList<>();

            if(this.enableOutQueue != null)
                client.enableOutQueue = this.enableOutQueue;
            else
                client.enableOutQueue = false;

            return client;
        }
    }

}
