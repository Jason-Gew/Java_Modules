package mqtt.pubsub.client;

import java.util.List;
import java.util.ArrayList;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.BlockingQueue;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;


public class MessageCallback implements MqttCallback
{
	private Boolean Connect_Status;					// Connection Status

	private BlockingQueue<List<String>> MessageQueue;		// Producer
	
	
	private String timestamp()
	{
		LocalDateTime Now = LocalDateTime.now();
		DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
		return Now.format(format);
	}
	
	public MessageCallback(Boolean Connect_Status, BlockingQueue<List<String>> MessageQueue)
	{
		this.Connect_Status = Connect_Status;
		this.MessageQueue = MessageQueue;
	}
	
	public void connectionLost(Throwable Cause) 
	{
		System.out.println("[ " + timestamp() + "] MQTT Connection Lost: " + Cause.getMessage());
		Connect_Status = false;
	}

	public void messageArrived(String Topic, MqttMessage Message) throws Exception 
	{
		List<String> Combined_MSG = new ArrayList<>();
		Combined_MSG.add(Topic);
		String Coming_msg = new String(Message.getPayload(), "UTF-8");
		Combined_MSG.add(Coming_msg);
		System.out.println(timestamp() + ": ["+Topic + "] " + Coming_msg);
		MessageQueue.put(Combined_MSG);
	
		Combined_MSG.clear();
	}

	public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) 
	{
		// No Status
	}

}
