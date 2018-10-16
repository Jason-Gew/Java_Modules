package gew.pubsub;

import gew.pubsub.config.MQTTClientConfig;
import gew.pubsub.mqtt.Client;
import gew.pubsub.mqtt.MQTTMessage;
import gew.pubsub.service.DataReceiving;
import gew.pubsub.mqtt.BasicClient;
import gew.pubsub.mqtt.SingletonClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author Jason/GeW
 */
public class Application {

    private static final String EXIT = "/exit";
    private static final String TEST_FILE_PATH = "files/testing.jpg";
    private static final String TEST_BROKER_URL = "ssl://iot.eclipse.org:8883";
    private static final String DEFAULT_STRING_TOPIC = "Jason/Message/String/";
    private static final String DEFAULT_FILE_TOPIC = "Jason/Message/File/#";
    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {

//        builderClientExample();
        singletonClientExample();
//        singletonClientSendingFile(TEST_FILE_PATH);
        System.exit(0);
    }

    private static void builderClientExample() {
        Boolean status = false;
        BasicClient client = new Client.Builder()
                .setBroker(TEST_BROKER_URL)
                .setCleanSession(true)
                .setClientID("Jason-Test-Client")
                .setKeepAlive(120)
                .setPubQos(0)
                .setSubQos(0)
                .setEnableLogin(false)
                .setEnableOutQueue(true)
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

        if (status) {
            client.subscribe(Arrays.asList(DEFAULT_STRING_TOPIC + "#", DEFAULT_FILE_TOPIC));
            DataReceiving receiving = new DataReceiving(client.getMessageQueue());

            Scanner inputWords = new Scanner(System.in);
            Thread receivingThread = new Thread(receiving);
            receivingThread.start();

            while (!Thread.currentThread().isInterrupted()) {
                System.out.print(">  ");
                String message = inputWords.nextLine();
                if(message.equals(EXIT)) {
                    break;
                } else {
                    client.publish(DEFAULT_STRING_TOPIC, message);
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

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.err.println("Console Terminate the System...");
                receiving.stop();
                try
                {
                    receivingThread.join();
                    logger.info("Consumer Thread Joined!");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }));

        } else {
            logger.error("MQTT Client Connect Failed......");
        }
    }

    private static void singletonClientExample() {
        MQTTClientConfig config = new MQTTClientConfig();
        config.setBroker(TEST_BROKER_URL);
        config.setSubQos(1);
        config.setPubQos(1);
        config.setMaxInFlight(20);
        config.setEnableSSL(true);
//        config.setClientID("Jason-Test-123");
        config.setEnableOutQueue(true);


        SingletonClient.setClientConfig(config);

        SingletonClient client = SingletonClient.getInstance();
        client.setMessageQueue(new LinkedBlockingQueue<>());

        Boolean status;
        try {
            status = client.initialize();
            logger.info(" MQTT Client Initialize: " + status);
            status = client.connect();
            if (status)
                logger.info("MQTT Client Connect Success!");
        } catch (MqttException e) {
            logger.error("MQTT Client Initialize/Connect Exception: " + e.getMessage());
            System.exit(1);
        }

        DataReceiving receiving = new DataReceiving(client.getMessageQueue());

        client.subscribe(DEFAULT_STRING_TOPIC);
        client.subscribe(DEFAULT_FILE_TOPIC);

        Scanner inputWords = new Scanner(System.in);
        Thread receivingThread = new Thread(receiving);

        receivingThread.start();

        while (!Thread.currentThread().isInterrupted()) {
            System.out.print(": ");
            String message = inputWords.nextLine();
            if (message.equals(EXIT)) {
                break;
            } else {
                client.publish(DEFAULT_STRING_TOPIC, message);
            }
        }
        inputWords.close();
        client.disconnect();
        receiving.stop();
        try {
            receivingThread.join();
            Thread.sleep(200);
            logger.info("Consumer Thread Joined!");
        } catch (InterruptedException err) {
            err.printStackTrace();
        }
    }

    private static void singletonClientSendingFile(final String path) {
        MQTTClientConfig config = new MQTTClientConfig();
        config.setBroker(TEST_BROKER_URL);
        config.setSubQos(1);
        config.setPubQos(1);
        config.setClientID("This is a test client 888");
        config.setEnableOutQueue(true);

        SingletonClient.setClientConfig(config);

        SingletonClient client = SingletonClient.getInstance();
        client.setMessageQueue(new LinkedList<>());

        Boolean status;
        try {
            status = client.initialize();
            logger.info(" MQTT Client Initialize: " + status);
            status = client.connect();
            if (status)
                logger.info("MQTT Client Connect Success!");
        } catch (MqttException e) {
            logger.error("MQTT Client Initialize/Connect Exception: " + e.getMessage());
            System.exit(1);
        }
        DataReceiving receiving = new DataReceiving(client.getMessageQueue());

        client.subscribe(DEFAULT_FILE_TOPIC);
        Thread receivingThread = new Thread(receiving);
        receivingThread.start();

        try  {
            Path filePath = Paths.get(path);
            byte[] data = Files.readAllBytes(filePath);
            MQTTMessage message = new MQTTMessage("Jason/Message/File/"
                    + String.valueOf(System.currentTimeMillis()/10000) + ".jpg",
                    data, ZonedDateTime.now());
            status = client.publish(message);
            logger.info("Message Publish Status: " + status);
        } catch (IOException err) {
            logger.error("Read Test File Failed: " + err.getMessage());
            System.exit(1);
        }

        try {
            Thread.sleep(6000);
            client.disconnect();
            receiving.stop();
            receivingThread.join();
            Thread.sleep(100);
            logger.info("Consumer Thread Joined!");
        } catch (InterruptedException err) {
            err.printStackTrace();
        }
    }
}
