package gew.PubSub.mqtt;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.BlockingQueue;

/**
 * MQTT Callback class, handle arriving message to message queue, etc.
 * @author Jason/Ge Wu
 * @since 2017-08-01
 */
public class ClientCallback implements MqttCallback
{

    private int RECONNECT_TRIAL = 10;
    private boolean queueEnable;
    private MqttClient currentClient;
    private BlockingQueue<String[]> messageQueue;       // Producer

    private static final Logger logger = LogManager.getLogger(ClientCallback.class);

    public ClientCallback(MqttClient currentClient)
    {
        this.currentClient = currentClient;
        queueEnable = false;
    }

    ClientCallback(MqttClient currentClient, BlockingQueue<String[]> messageQueue)
    {
        this.currentClient = currentClient;
        this.messageQueue = messageQueue;
        queueEnable = true;
    }

    @Override
    public void connectionLost(Throwable throwable) {
        logger.info("=> MQTT Connection Lost: " + throwable.getMessage());
        while(RECONNECT_TRIAL != 0 && !currentClient.isConnected()) {
            try {
                logger.info("=> System is trying to reconnect... (" + RECONNECT_TRIAL + ")");
                currentClient.reconnect();
                if (currentClient.isConnected()) {
                    logger.info("=> System Reconnected!");
                    RECONNECT_TRIAL = 10;
                    break;
                }
            } catch (MqttException e) {
                RECONNECT_TRIAL++;
                logger.error("=> MQTT Reconnect: " + e.toString());
            }
            RECONNECT_TRIAL--;
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                logger.warn("=> MQTT Reconnection Interrupted: " + e.toString());
            }
        }
    }

    @Override
    public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
        if(queueEnable)
        {
            String[] data = new String[2];
            String msg = null;
            try {
                msg = new String(mqttMessage.getPayload(), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                logger.error(e.toString());
            }
            data[0] = topic;
            data[1] = msg;
            try {
                messageQueue.put(data);
            } catch(InterruptedException err) {
                logger.info(err.toString());
            }
        } else {
            String msg = null;
            try {
                msg = new String(mqttMessage.getPayload(), "UTF-8");

            } catch (UnsupportedEncodingException e) {
                logger.error(e.toString());
            }
            logger.info("=> [" + topic + "] " + msg);
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
        if(RECONNECT_TRIAL < 10)
            RECONNECT_TRIAL = 10;
    }
}
