package mqtt.pubsub.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;

public class Main 
{
	private static final String Pub_Topic = "jason/test123";
	private static final String Sub_Topic = "jason/test123";
	
	private static String config = "config\\config.properties";
	
	private static boolean control_bit = true; 
	
	public static void main(String[] args) 
	{
		
		PubSubClient.setConfigPath(config);
		PubSubClient singleClient = PubSubClient.getInstance();
		
		boolean flag;
		flag = singleClient.Initialize();
		try
		{
			singleClient.connect();
		}
		catch (MqttException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		singleClient.subscribe(Sub_Topic);
		ReceivingMessage ComingMsg = new ReceivingMessage(singleClient.getMessageQueue());
		Scanner input_words = new Scanner(System.in);
		Thread Coming = new Thread(ComingMsg);
		Coming.start();
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
					flag = singleClient.publish(Pub_Topic, words);
				System.out.println("Message Publish Status: "+flag);
			}
		}
		input_words.close();
		flag = singleClient.disconnect();
		if(flag)
		{
			System.out.println("Disconnected with MQTT Broker!");
		}
		ComingMsg.setControl_Bit(false);
		try
		{
			Coming.join();
			System.out.println("Consumer Thread Joined!");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}

class ReceivingMessage implements Runnable
{
	private BlockingQueue<String[]> MessageQ;
	boolean Control_Bit = true;
	
	public void setControl_Bit(final boolean Control_Bit)
	{
		this.Control_Bit = Control_Bit;
	}
	
	public ReceivingMessage(BlockingQueue<String[]> MessageQ)
	{
		this.MessageQ = MessageQ;
	}
	
	@Override
	public void run()
	{
		while(Control_Bit)
		{

			if(!MessageQ.isEmpty())
			{
				try
				{
					String[] MSG = MessageQ.take();
					System.out.println("Topic: " + MSG[0] + " Payload: " + MSG[1]);
				} catch (InterruptedException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
	}

}
