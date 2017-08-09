package mqtt.pubsub.client;

import java.io.File;
import java.util.Map;
import java.util.HashMap;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;
import org.eclipse.paho.client.mqttv3.MqttTopic;

public class PubSubClient 
{
	private static PubSubClient pubsub_client;
	private static String Config_Path;
	
	private Map<String, String> MqttConfig = new HashMap<>();
	
	
	private PubSubClient(String Config_Path)
	{
		if(Config_Path == null)
		{
			throw new IllegalArgumentException("Invalid Config Path, Invoke setConfigPath First");
		}
	}
	/**
	 * This static method is used to set Config file path for the class.
	 * Should invoke this method to set a valid path first.
	 * @param Path
	 */
	public static void setConfigPath(String Path)
	{
		if(Path == null || Path.isEmpty())
		{
			System.err.println("Invalid Config Path");
			throw new IllegalArgumentException("Invalid Config Path");
		}
		else
		{
			File Config = new File(Path);
			if(Config.exists())
				Config_Path = Path;
			else
				throw new IllegalArgumentException("File Does Not Exist");
		}
	}
	/**
	 * 
	 * @return the current Singleton PubSubClient instance.
	 */
	public static PubSubClient getInstance()
	{
		if(pubsub_client == null)
		{
			pubsub_client = new PubSubClient(Config_Path);
		}
		return pubsub_client;
	}
	
	public String timestamp()
	{
		LocalDateTime Now = LocalDateTime.now();
		DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
		return Now.format(format);
	}
	
}
