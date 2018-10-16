package gew.pubsub.mqtt;

import org.eclipse.paho.client.mqttv3.MqttException;

import java.util.List;
import java.util.Queue;

/**
 * Basic Client Methods Definition...
 * @author Jason/GeW
 */
public interface BasicClient {

    boolean isConnected();

    Boolean initialize();

    boolean connect() throws MqttException;

    void disconnect();

    Boolean publish(final MQTTMessage mqttMessage);

    Boolean publish(final String topic, final String payload);

    Boolean publish(final String topic, final String payload, final int qos);

    Boolean publish(final String topic, final byte[] payload, final int qos, final boolean retain);

    void subscribe(final String topic);

    void subscribe(final String topic, final int qos);

    void subscribe(final List<String> topics);

    void unsubscribe(final String topic);

    boolean cleanRetain(final String topic);

    Queue<MQTTMessage> getMessageQueue();

    void setMessageQueue(Queue<MQTTMessage> messageQueue);
}
