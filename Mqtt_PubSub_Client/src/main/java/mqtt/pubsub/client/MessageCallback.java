package mqtt.pubsub.client;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.BlockingQueue;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;


public class MessageCallback implements MqttCallback
{
	private int reconnect_trial = 10;
	private boolean queue_enable;
	private MqttClient CurrentClient;
//	private BlockingQueue<List<String>> MessageQueue;		// Producer
	private BlockingQueue<String[]> MessageQueue;
	
	private static final Logger logger = LogManager.getLogger(MessageCallback.class);
	
	private String timestamp()
	{
		LocalDateTime Now = LocalDateTime.now();
		DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
		return Now.format(format);
	}
	
	public MessageCallback(MqttClient CurrentClient)
	{
		this.CurrentClient = CurrentClient;
		queue_enable = false;
	}
	
	public MessageCallback(MqttClient CurrentClient, BlockingQueue<String[]> MessageQueue)
	{
		this.CurrentClient = CurrentClient;
		this.MessageQueue = MessageQueue;
		queue_enable = true;
	}
	
	public void connectionLost(Throwable Cause) 
	{
		System.out.println("[" + timestamp() + "] MQTT Connection Lost: " + Cause.getMessage());
		while(reconnect_trial != 0 && !CurrentClient.isConnected())
		{	
			try
			{
				System.out.println("[" + timestamp() + "] System is trying to reconnect... ("+reconnect_trial+")");
				CurrentClient.reconnect();
				if(CurrentClient.isConnected())
				{
					System.out.println("[" + timestamp() + "] System Reconnected!");
					reconnect_trial = 10;
					break;
				}
			}
			catch(MqttException e)
			{
				reconnect_trial++;
				System.err.println("MQTT Reconnect: " + e.toString());
				logger.warn(e.toString());
			}
			reconnect_trial--;
			try
			{
				Thread.sleep(10000);
			}
			catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void messageArrived(String Topic, MqttMessage Message) throws Exception 
	{
		if(queue_enable)
		{
			String[] data = new String[2];
			String msg = new String(Message.getPayload(), "UTF-8");
//			System.out.println(timestamp() + ": ["+Topic + "] " + msg);
			data[0] = Topic;
			data[1] = msg;
			MessageQueue.put(data);
		}
		else
		{
			String msg = new String(Message.getPayload(), "UTF-8");
			System.out.println(timestamp() + ": ["+Topic + "] " + msg);
		}
		
	}

	public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) 
	{
		reconnect_trial = 10;
	}

}
