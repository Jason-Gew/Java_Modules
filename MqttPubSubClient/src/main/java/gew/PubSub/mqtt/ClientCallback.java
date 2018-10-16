package gew.pubsub.mqtt;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.util.Queue;

/**
 * MQTT Callback class, handle arriving MQTTMessage to message queue, etc.
 * @author Jason/Ge Wu
 * @since 2017-08-01
 */
public class ClientCallback implements MqttCallback {

    private boolean queueEnable;
    private MessageType messageType;
    private Queue<MQTTMessage> messageQueue;

    private static final Logger logger = LogManager.getLogger(ClientCallback.class);

    public ClientCallback(MessageType messageType) {
        queueEnable = false;
        this.messageType = messageType;
    }

    ClientCallback(Queue<MQTTMessage> messageQueue) {
        queueEnable = true;
        this.messageQueue = messageQueue;
    }

    @Override
    public void connectionLost(Throwable throwable) {
        logger.info("=> MQTT Connection Lost: " + throwable.getMessage());
    }

    @Override
    public void messageArrived(String topic, MqttMessage mqttMessage) {
        if (queueEnable) {
            MQTTMessage message = new MQTTMessage(topic, mqttMessage.getPayload(), ZonedDateTime.now());
            message.setMessageId(mqttMessage.getId());
            messageQueue.add(message);
        } else {
            String msg = null;
            if (messageType != null && (messageType == MessageType.PLAIN_TEXT
                    || messageType == MessageType.JSON_STRING)) {
                msg = new String(mqttMessage.getPayload(), StandardCharsets.UTF_8);
            } else {
                try {
                    msg = new String(mqttMessage.getPayload(), StandardCharsets.UTF_8);
                } catch (Exception err) {
                    logger.error("=> Encode MQTT Message from Topic [{}] to String Failed: {}", topic, err.getMessage());
                }
            }
            logger.info("=> Message From Topic [{}]:\n{}", topic, msg);
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
        logger.debug("Message Delivery Complete");
    }
}
