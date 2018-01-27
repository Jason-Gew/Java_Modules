package gew.PubSub.mqtt;


import gew.PubSub.config.MqttClientConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


/**
 * Thread-safe Singleton MQTT Client, only allow one instance which is created by first time calling getInstance()...
 * @author Jason/GeW
 * @since 2017-08-20
 */
public class SingletonClient
{

    private static MqttClientConfig clientConfig;
    private static SingletonClient client;

    private MqttConnectOptions connectOps;
    private MqttClient mqttClient;
    private BlockingQueue<String[]> messageQueue;

    private static final String URL_PREFIX = "tcp://";
    private static final Logger logger = LogManager.getLogger(Client.class);

    private SingletonClient () // Private Constructor for getInstance()
    {
        if(clientConfig == null)
            throw new IllegalArgumentException("Invalid MQTT Client Config, Invoke setConfig First.");
    }

    public static SingletonClient getInstance()
    {
        if(client == null) {
            synchronized (SingletonClient.class) {
                if(client == null)
                    client = new SingletonClient();
            }
        }
        return client;
    }

    public static void setClientConfig(MqttClientConfig clientConfig) {
        SingletonClient.clientConfig = clientConfig;
    }

    public boolean isConnected()
    {
        return mqttClient != null && mqttClient.isConnected();
    }

    public Boolean initialize()
    {
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
                if(clientConfig.getBroker() == null || clientConfig.getBroker().isEmpty()) {
                    throw new IllegalArgumentException ("Broker Address Cannot Be null or Empty!");
                } else if(clientConfig.getBroker().contains(URL_PREFIX)) {
                    if(clientConfig.getClientID() == null || clientConfig.getClientID().isEmpty())
                        clientConfig.setClientID("Default-ClientID:" + System.currentTimeMillis()/1000);
                    mqttClient = new MqttClient(clientConfig.getBroker(), clientConfig.getClientID());
                } else {
                    mqttClient = new MqttClient(URL_PREFIX + clientConfig.getBroker(), clientConfig.getClientID());
                }

                connectOps = new MqttConnectOptions();
                if(clientConfig.getCleanSession() == null)
                    clientConfig.setCleanSession(true);
                connectOps.setCleanSession(clientConfig.getCleanSession());
                if(clientConfig.getEnableLogin() != null && clientConfig.getEnableLogin())
                {
                    connectOps.setUserName(clientConfig.getUsername());
                    connectOps.setPassword(clientConfig.getPassword().toCharArray());
                }
                connectOps.setAutomaticReconnect(true);     // Auto-reconnection must be set true;
                if(clientConfig.getKeepAlive() == null || clientConfig.getKeepAlive() <= 15)
                    clientConfig.setKeepAlive(60);
                connectOps.setKeepAliveInterval(clientConfig.getKeepAlive());
                if(clientConfig.getEnableOutQueue() != null && clientConfig.getEnableOutQueue()) {

                    messageQueue = new LinkedBlockingQueue<>();     // Instantiate LinkedBlockingQueue
                    mqttClient.setCallback(new ClientCallback(mqttClient, messageQueue));
                } else {
                    mqttClient.setCallback(new ClientCallback(mqttClient));
                }
                return true;
            } catch (MqttException err) {
                logger.fatal(err.getMessage());
                return false;
            }
        }
    }

    public boolean connect() throws MqttException
    {
        if(connectOps != null)
        {
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

    public Boolean publish(final String topic, final String payload)
    {
        if(clientConfig.getPubQos() == null || clientConfig.getPubQos() < 0 || clientConfig.getPubQos() > 2)
            clientConfig.setPubQos(0);
        return publish(topic, payload, clientConfig.getPubQos());
    }

    public Boolean publish(final String topic, final String payload, final int qos)
    {
        try
        {
            if(!mqttClient.isConnected() && connectOps != null)
                mqttClient.connect(connectOps);

            mqttClient.publish(topic, payload.getBytes("UTF-8"), qos, false);
            return true;
        }
        catch(UnsupportedEncodingException err)
        {
            logger.error("=> UnSupported String Format: " + err.getMessage());
            return false;
        }
        catch(MqttException err)
        {
            logger.error("=> MQTT Exception: " + err.getMessage());
            return false;
        }
    }

    /**
     * This method will broadcast the message to all auto publish topics!
     * @param message String message
     */
    public void autoPublish(final String message)
    {
        if(clientConfig.getAutoPubTopics() != null) {
            for(String topic : clientConfig.getAutoPubTopics())
                publish(topic, message);
        }
    }

    public void subscribe(final String topic)
    {
        if(clientConfig.getSubQos() == null || clientConfig.getSubQos() < 0 || clientConfig.getSubQos() > 2)
            clientConfig.setSubQos(0);
        subscribe(topic, clientConfig.getSubQos());
    }

    public void subscribe(final String topic, final int qos)
    {
        if(clientConfig.getSubQos() == null || clientConfig.getSubQos() < 0 || clientConfig.getSubQos() > 2)
            clientConfig.setSubQos(0);
        try
        {
            if(qos < 0 || qos > 2)
                mqttClient.subscribe(topic, (clientConfig.getSubQos()));
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
    public void autoSubscribe()
    {
        if(clientConfig.getAutoSubTopics() != null) {
            for(String topic : clientConfig.getAutoSubTopics())
                subscribe(topic);
        }
    }

    public void unsubscribe(final String topic)
    {
        try {
            mqttClient.unsubscribe(topic);
        } catch (MqttException e) {
            logger.error("=> Unsubscribe Topic [{}] Failed: {}", topic, e.toString());
        }
    }

    public boolean cleanRetain(final String topic)
    {
        try
        {
            if(!mqttClient.isConnected() && connectOps != null)
                mqttClient.connect(connectOps);

            mqttClient.publish(topic, "".getBytes(), clientConfig.getPubQos(), true);
            return true;
        } catch (MqttException err) {
            logger.error("=> Clean Retained Message on Topic [{}] Failed: {}", topic, err.getMessage());
            return false;
        }
    }

    public BlockingQueue<String[]> getMessageQueue() {
        if(clientConfig.getEnableOutQueue() != null && clientConfig.getEnableOutQueue())
            return messageQueue;
        else
            throw new IllegalStateException("=> Message Queue has not been enabled!");
    }
}
