package gew.PubSub;

import gew.PubSub.config.MqttClientConfig;
import gew.PubSub.entity.DataReceiving;
import gew.PubSub.mqtt.BasicClient;
import gew.PubSub.mqtt.Client;
import gew.PubSub.mqtt.SingletonClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Scanner;

/**
 * @author Jason/GeW
 */
public class Application
{

    private static final String EXIT = "/exit";
    private static final String TesTBrokerURL = "iot.eclipse.org:1883";
    private static final String DefaultTopic = "Jason-Test-Message";
    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args)
    {

        builderClientExample();
//        singletonClientExample();
        System.exit(0);
    }

    private static void builderClientExample()
    {
        Boolean status = false;
        BasicClient client = new Client.Builder()
                .setBroker(TesTBrokerURL)
                .setCleanSession(false)
                .setClientID("Jason-Test-Client")
                .setKeepAlive(60)
                .setPubQos(0)
                .setSubQos(0)
                .setEnableLogin(false)
                .setEnableOutQueue(true)
                .setAutoSubTopics(Arrays.asList(DefaultTopic, "Jason-Test"))
                .build();

        try {
            status = client.initialize();
            logger.info("MQTT Client Initialize: " + status);
            status = client.connect();
            if(status)
                logger.info("MQTT Client Connect Success!");
        } catch (MqttException e) {
            logger.error("MQTT Client Initialize/Connect Exception: " + e.getMessage());
        }
        if(status)
        {
            client.autoSubscribe();
            DataReceiving receiving = new DataReceiving(client.getMessageQueue());

            Scanner inputWords = new Scanner(System.in);
            Thread receivingThread = new Thread(receiving);

            receivingThread.start();

            while (true) {
                System.out.print(">  ");
                String message = inputWords.nextLine();
                if(message.equals(EXIT)) {
                    break;
                } else {
                    client.publish(DefaultTopic, message);
                }
            }
            inputWords.close();
            client.disconnect();
            receiving.stop();
            try {
                receivingThread.join();
                Thread.sleep(200);
            } catch (InterruptedException err) {
                err.printStackTrace();
            }


            Runtime.getRuntime().addShutdownHook(new Thread() {

                @Override
                public void run()
                {
                    System.err.println("Console Terminate the System...");

                    receiving.stop();
                    try
                    {
                        receivingThread.join();
                        System.out.println("Consumer Thread Joined!");
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                }
            });

        } else {
            logger.error("MQTT Client Connect Failed......");
        }
    }

    private static void singletonClientExample()
    {
        MqttClientConfig config = new MqttClientConfig();
        config.setBroker(TesTBrokerURL);
        config.setSubQos(0);
        config.setPubQos(0);
        config.setClientID("Jason-Test-Client2");
//        config.setEnableLogin(false);
        config.setEnableOutQueue(true);
        config.setAutoSubTopics(Arrays.asList(DefaultTopic, "Jason-Test"));

        SingletonClient.setClientConfig(config);

        SingletonClient client = SingletonClient.getInstance();

        Boolean status = false;
        try {
            status = client.initialize();
            logger.info(" MQTT Client Initialize: " + status);
            status = client.connect();
            if(status)
                logger.info("MQTT Client Connect Success!");
        } catch (MqttException e) {
            logger.error("MQTT Client Initialize/Connect Exception: " + e.getMessage());
        }

            client.autoSubscribe();
            DataReceiving receiving = new DataReceiving(client.getMessageQueue());

            Scanner inputWords = new Scanner(System.in);
            Thread receivingThread = new Thread(receiving);

            receivingThread.start();

            while (true)
            {
                System.out.print(": ");
                String message = inputWords.nextLine();
                if(message.equals(EXIT)) {
                    break;
                } else {
                    client.publish(DefaultTopic, message);
                }
            }
            inputWords.close();
            client.disconnect();
            receiving.stop();
            try {
                receivingThread.join();
                Thread.sleep(200);
            } catch (InterruptedException err) {
                err.printStackTrace();
            }

    }
}
