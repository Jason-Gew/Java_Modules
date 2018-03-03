package client;


import client.consumer.Consumer;
import client.consumer.ConsumerBuilder;
import client.producer.Producer;
import client.producer.ProducerBuilder;

import java.util.Queue;
import java.util.Scanner;

/**
 * Example for using the library
 * @author Jason/GeW
 */
public class Application
{

    private static final String server = "10.0.1.100:9092";
    private static final String clientId = "Jason-Test-Client";
    private static final String topic = "Jason-Test";
    private static final int retries = 0;
    private static final String requireAck = "0";
    private static final String serializeClass = "org.apache.kafka.common.serialization.StringSerializer";
    private static final String deserializeClass = "org.apache.kafka.common.serialization.StringDeserializer";

    private static final String END = "/exit";

    public static void main(String[] args)
    {
//        producerExample1();
//        producerExample2();
        consumerExample1();
    }

    private static void producerExample1()
    {
        Producer producer = new ProducerBuilder()
                .setServer(server)
                .setClientId(clientId)
                .setTopic(topic)
                .setRetries(retries)
                .setAcknowledge(requireAck)
                .setSerializeClass(serializeClass)
                .build();

        Scanner input = new Scanner(System.in);
        producer.init();

        while(true) {
            System.out.print("> ");
            String message = input.nextLine();
            String key = null;
            if(!message.equalsIgnoreCase(END)) {
                if(message.contains(",") && message.length() > 1) {
                    String[] splits = message.split(",");
                    key = splits[0];
                    message = message.substring(message.indexOf(",")+1).trim();
                }
                producer.sendMessage(topic, message, key);
            } else {
                break;
            }
        }
        input.close();
        producer.close();

    }

    private static void producerExample2()
    {
        Producer producer = new ProducerBuilder()
                .setServer(server)
                .setClientId(clientId)
                .setTopic(topic)
                .setRetries(retries)
                .setAcknowledge(requireAck)
                .setEnableMessageQueue(true)
                .setSerializeClass(serializeClass)
                .build();

        producer.init();
        Queue<String[]> queue = producer.getIncomingQueue();
        Scanner input = new Scanner(System.in);
        Thread producerThread = new Thread(producer);

        producerThread.start();

        while(true) {
            System.out.print("> ");
            String message = input.nextLine();
            String key = null;
            if(!message.equalsIgnoreCase(END)) {
                if(message.contains(",") && message.length() > 1) {
                    String[] splits = message.split(",");
                    key = splits[0];
                    message = message.substring(message.indexOf(",")+1).trim();
                }
                String[] msg = {topic, message, key};
                queue.add(msg);
            } else {
                break;
            }
        }
        input.close();
        producer.stop();
        producer.close();
        try {
            producerThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void consumerExample1()
    {
        Consumer consumer = new ConsumerBuilder()
                .setServer(server)
                .setTopic(topic)
                .setClientId(clientId)
                .setGroupId("Jason-Test")
                .setEnableMessageQueue(false)
                .setDeserializeClass(deserializeClass)
                .build();

        consumer.init();

        Thread consumerThread = new Thread(consumer);
        consumerThread.start();

    }
}
