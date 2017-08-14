package mqtt.pubsub.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;

/**
 * PubSubClient based on MQTT (Paho)
 * @author Jason Gew
 *
 */
public class PubSubClient 
{
	private static PubSubClient pubsub_client;
	private static String Config_Path;
	
	
	private Map<String, Object> MqttConfig = new HashMap<>();
	private boolean CleanSession;
	private Boolean Status;

	private MqttConnectOptions ConnectOps;
	private MqttClient MQTTClient;
	
	private BlockingQueue<String[]> MessageQueue = new LinkedBlockingQueue<>();
	
	private static final Logger logger = LogManager.getLogger(PubSubClient.class);
	
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
	public static final PubSubClient getInstance()
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
	
	private void Load_Config()
	{
		InputStream Inputs = null;
		Properties Config = new Properties();

		try
		{
			Inputs = new FileInputStream(Config_Path);
			Config.load(Inputs);
			
			String temp = null;
			temp = Config.getProperty("MQTT.Broker");
			if(temp == null || temp.isEmpty())
			{
				throw new IllegalArgumentException("Missing \"MQTT.Broker\" Key or Invalid Value in Config File!");
			}
			else
			{
				logger.info("System set MQTT Broker: " + temp);
				if(temp.contains("tcp://"))
				{
					MqttConfig.put("Broker", temp);
				}
				else
				{
					MqttConfig.put("Broker", "tcp://"+temp);
				}
			}
			
			temp = Config.getProperty("MQTT.KeepAlive");
			if(temp == null || temp.isEmpty())
			{
				logger.info("Missing \"MQTT.KeepAlive\" Key or Invalid Value, System Set to Default 60 Seconds");
				MqttConfig.put("KeepAlive", 60);
			}
			else
			{
				logger.info("System set MQTT KeepAlive " + temp + " seconds");
				MqttConfig.put("KeepAlive", Integer.valueOf(temp));
			}
			
			temp = Config.getProperty("MQTT.CleanSession");
			if(temp == null || temp.isEmpty())
			{
				logger.info("Missing \"MQTT.CleanSession\" Key or Invalid Value, System Set to Default true");
				MqttConfig.put("CleanSession", true);
				CleanSession = true;
			}
			else
			{
				MqttConfig.put("CleanSession", Boolean.valueOf(temp));
				if(Boolean.valueOf(temp))
					CleanSession = true;
				else
					CleanSession = false;
			}
			
			temp = Config.getProperty("MQTT.ClientID");
			if(temp == null || temp.length() < 6)
			{
				String ID_Generate = new String("Default_MQTT_Client_"+String.valueOf(System.currentTimeMillis()/1000));
				logger.info("Missing \"MQTT.ClientID\" Key or Invalid Value, System Set to " + ID_Generate);
				MqttConfig.put("ClientID", ID_Generate);
			}
			else
			{
				MqttConfig.put("ClientID", temp);
			}
			
			temp = Config.getProperty("MQTT.Publish.Qos");
			switch(temp)
			{
				case "0":
					logger.info("System set MQTT Publish QoS:0");
					MqttConfig.put("Publish_Qos",0);
					break;
				
				case "1":
					logger.info("System set MQTT Publish QoS:1");
					MqttConfig.put("Publish_Qos",1);
					break;
				
				case "2":
					logger.info("System set MQTT Publish QoS:2");
					MqttConfig.put("Publish_Qos",2);
					break;
					
				default:
					logger.warn("Invalid Publish Qos " + temp + ", System set QoS: 1");
					MqttConfig.put("Publish_Qos",1);
					break;
			}
			
			temp = Config.getProperty("MQTT.Subscribe.Qos");
			switch(temp)
			{
				case "0":
					logger.info("System set MQTT Subscribe QoS:0");
					MqttConfig.put("Subscribe_Qos",0);
					break;
				
				case "1":
					logger.info("System set MQTT Subscribe QoS:1");
					MqttConfig.put("Subscribe_Qos",1);
					break;
				
				case "2":
					logger.info("System set MQTT Subscribe QoS:2");
					MqttConfig.put("Subscribe_Qos",2);
					break;
					
				default:
					logger.warn("Invalid Publish Qos " + temp + ", System set QoS: 1");
					MqttConfig.put("Subscribe_Qos",1);
					break;
			}
		}
		catch (FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Boolean Initialize()
	{
		if( MQTTClient != null && MQTTClient.isConnected())
		{
			System.err.println("MQTT client has been initialized, please disconnect first then re-initiaze.");
			return false;
		}
		else if( MQTTClient != null && !MQTTClient.isConnected())
		{
			try
			{
				MQTTClient.close();
				MQTTClient = null;
				Initialize();
				return true;
			}
			catch (MqttException err)
			{
				logger.fatal(err.toString());
				err.printStackTrace();
				return false;
			}
		}
		else
		{
			Load_Config();
			try
			{
				MQTTClient = new MqttClient((String) MqttConfig.get("Broker"), (String) MqttConfig.get("ClientID"));
				ConnectOps = new MqttConnectOptions();
				ConnectOps.setCleanSession(CleanSession);
				ConnectOps.setKeepAliveInterval((int) MqttConfig.get("KeepAlive"));
					
				MQTTClient.setCallback(new MessageCallback(MQTTClient, MessageQueue));
				
				return true;
			}
			catch(MqttException err)
			{
				logger.fatal(err.toString());
				return false;
			}
		}
	}
	
	public void connect() throws MqttSecurityException, MqttException
	{
		MQTTClient.connect(ConnectOps);
	}
	
	public boolean disconnect()
	{
		try
		{
			MQTTClient.disconnect();
			return true;
		}
		catch (MqttException e)
		{
			logger.warn("MQTT Client Disconnection Error: " + e.toString());
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean publish(final String Topic, final String Payload)
	{
		try
		{
			if(!MQTTClient.isConnected())
				MQTTClient.reconnect();
			MQTTClient.publish(Topic, Payload.getBytes(), (int) MqttConfig.get("Publish_Qos"), false);
			return true;
		}
		catch(MqttException err)
		{
			logger.error(err.toString());
			return false;
		}
	}
	
	public boolean publish(final String Topic, final String Payload, final boolean Retain)
	{
		try
		{
			if(!MQTTClient.isConnected())
				MQTTClient.reconnect();
			MQTTClient.publish(Topic, Payload.getBytes(), (int) MqttConfig.get("Publish_Qos"), Retain);
			return true;
		}
		catch(MqttException err)
		{
			logger.error(err.toString());
			return false;
		}
	}
	
	public void subscribe(final String Topic)
	{	
		try
		{
			MQTTClient.subscribe(Topic, (int) MqttConfig.get("Subscribe_Qos"));	
		}
		catch (MqttException e)
		{
			e.printStackTrace();
		}

	}
	
	public void subscribe(final String[] Topics)
	{
		int[] qos = new int[Topics.length];
		for(int i = 0; i < Topics.length; i++)
		{
			qos[i] = (int) MqttConfig.get("Subscribe_Qos");
		}
		try
		{
			MQTTClient.subscribe(Topics, qos);
		}
		catch (MqttException e)
		{
			e.printStackTrace();
		}
	}
	
	public boolean clean_retain(final String Topic)
	{
		try
		{
			if(!MQTTClient.isConnected())
				MQTTClient.reconnect();
			MQTTClient.publish(Topic, "".getBytes(), (int) MqttConfig.get("Publish_Qos"), true);
			return true;
		}
		catch(MqttException err)
		{
			logger.error(err.toString());
			return false;
		}
	}
	public final BlockingQueue<String[]> getMessageQueue()
	{
		return MessageQueue;
	}

	
}
