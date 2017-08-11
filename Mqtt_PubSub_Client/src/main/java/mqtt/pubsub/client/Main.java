package mqtt.pubsub.client;

import java.util.Scanner;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;
import org.eclipse.paho.client.mqttv3.MqttTopic;

public class Main 
{
	private static final String Broker = "tcp://iot.eclipse.org:1883";
	private static final String ClientID = "Jason/GeW_2017";
	private static final String Pub_Topic = "jason/test123";
	private static final String Sub_Topic = "jason/test123";
	private static int Qos = 1;
	private static MqttClient Testclient;
	private static MqttConnectOptions Ops;
	
	private static String config = "C:\\Users\\Skywalker\\Desktop\\projects\\Mqtt_PubSub_Client\\config\\config.properties";
	
	private static boolean control_bit = true; 
	
	public static void main(String[] args) 
	{
/*		Scanner input_words = new Scanner(System.in);
		initialize();
		boolean check = connect();
		if(check)
		{
			System.out.println("-> System Connected With Broker...");
		}
		subscribe_topic(Sub_Topic);
		while(control_bit)
		{
			System.out.print("> ");
			if(input_words.hasNextLine())
			{
				String words = input_words.nextLine();
				if(words.equals("Exit"))
				{
					System.out.println("System Terminating...");
					control_bit = false;
			//		break;
				}
				else
					publish_msg(Pub_Topic, "["+timestamp()+"] "+words);
			}
		}
		input_words.close();
		check = disconnect();
		if(check)
		{
			System.out.println("Disconnected with MQTT Broker!");
		}*/
		
		PubSubClient.setConfigPath(config);
		PubSubClient singleClient = PubSubClient.getInstance();
		
		boolean check = singleClient.Initialize();
		if(check)
		{
			try {
				singleClient.connect();
			} catch (MqttException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			singleClient.publish("jason/test123", singleClient.timestamp());
		
		}
		singleClient.disconnect();
		
	}
	
	public static void initialize()
	{
		try
		{
			Testclient = new MqttClient(Broker, ClientID);
			Ops = new MqttConnectOptions();
			Ops.setCleanSession(true);
			Ops.setKeepAliveInterval(60000);
			Testclient.setCallback(new MqttCallback() {
				 
	            public void connectionLost(Throwable cause) 
	            {
	            	System.out.println("-> MQTT Connection Lost: " + cause.getMessage());
	            	System.err.println("-> Reconnecting...");
	            	try {
						Testclient.reconnect();
					} catch (MqttException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	            }
	 
	            public void messageArrived(String topic, MqttMessage message) throws Exception 
	            {
	            	String CurrentTime = timestamp();
	            	String Coming_msg = new String(message.getPayload(), "UTF-8");
	                System.out.println(CurrentTime + ": ["+topic + "] " + Coming_msg);
	            }
	            
	            public void deliveryComplete(IMqttDeliveryToken token) 
	            {
	            	System.out.println("Publish Success");
	            }
	        });
			
			
		}
		catch (MqttException err) 
		{
			// TODO Auto-generated catch block
			err.printStackTrace();
		}
	}
	
	public static boolean connect()
	{
		try
		{
			Testclient.connect(Ops);
			return true;
		}
		catch (MqttSecurityException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
			
		}
		catch (MqttException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	
	public static String timestamp()
	{
		LocalDateTime dt = LocalDateTime.now();
		DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
		String current_ts = dt.format(format);
		return current_ts;		
	}
	
	public static boolean publish_msg(String topic, String payload)
	{
		try 
		{
			Testclient.publish(topic, payload.getBytes(), Qos, false);
			return true;
		}
		catch (MqttPersistenceException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		catch (MqttException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	
	public static void subscribe_topic(String topic)
	{
		try
		{
			Testclient.subscribe(topic, Qos);
		}
		catch (MqttException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static boolean disconnect()
	{
		try
		{
			Testclient.disconnect();
			return true;
		}
		catch (MqttException e)
		{
			e.printStackTrace();
			return false;
		}
	}

}
