package gew.PubSub.mqtt;

import org.eclipse.paho.client.mqttv3.MqttException;

import java.util.Queue;

/**
 * Basic Client Methods Definition...
 * @author Jason/GeW
 */
public interface BasicClient
{
    boolean isConnected();
    Boolean initialize();
    boolean connect() throws MqttException;
    void disconnect();
    Boolean publish(final String topic, final String payload);
    Boolean publish(final String topic, final String payload, final int qos);
    void autoPublish(final String message);
    void subscribe(final String topic);
    void subscribe(final String topic, final int qos);
    void autoSubscribe();
    void unsubscribe(final String topic);
    boolean cleanRetain(final String topic);
    Queue<String[]> getMessageQueue();
}
