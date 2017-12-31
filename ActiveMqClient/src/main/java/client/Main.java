package client;


import client.consumer.Receiver;

import client.consumer.Subscriber;
import client.producer.Publisher;
import client.producer.Sender;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Simple ActiveMQ P2P Send/Receive and Publish/Subscribe Modules
 * @author Jason/GeW
 */
public class Main
{

    private static final String brokerAddress = "tcp://localhost:61616";
    private static final String username = "admin";
    private static final String password = "admin";
    private static final String queueName = "test";
    private static final String topic = "test2";

    private static final String exitKey = "/exit";

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args)
    {
        logger.info("---- Start ----");
//        test1();
        test2();
    }

    public static void test1()
    {
        BlockingQueue<String> msgQueue = new LinkedBlockingQueue<>();

        Receiver receiver = new Receiver(brokerAddress, queueName, username, password);
        Thread receiveProcess = new Thread(receiver);

        Scanner scanner = new Scanner(System.in);

        Sender sender = new Sender(brokerAddress, queueName, username, password);
        sender.setEnableQueue(true);
        sender.setMessageQueue(msgQueue);
        Thread sendProcess = new Thread(sender);

        try
        {
            receiveProcess.start();
            Thread.sleep(1000);
            sendProcess.start();
            Thread.sleep(1000);    // Proper delay is indispensable

            while(true)
            {
                System.out.print("Input: ");
                String msg = scanner.nextLine();
                if(msg.equals(exitKey))
                    break;
                else
                    msgQueue.add(msg);
            }

        } catch (InterruptedException e) {
            System.out.println("=> System Interrupted!");
        } finally {

            try {
                receiver.close();
                sender.setSignal(false);
                sender.close();
                receiveProcess.join();
                sendProcess.join();
                System.out.println("All Threads Joined!");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.exit(0);
    }

    public static void test2()
    {
        BlockingQueue<String> msgQueue = new LinkedBlockingQueue<>();

        Publisher publisher = new Publisher(brokerAddress, topic, username, password);
        publisher.setEnableQueue(true);
        publisher.setMessageQueue(msgQueue);
        Thread publishThread = new Thread(publisher);

        Subscriber subscriber = new Subscriber(brokerAddress, topic);
        Thread subscribeThread = new Thread(subscriber);

        Subscriber sub2 = new Subscriber(brokerAddress, "test3");
        Thread subscribeThread2 = new Thread(sub2);

        Scanner scanner = new Scanner(System.in);

        try{
            subscribeThread.start();
            subscribeThread2.start();
            Thread.sleep(1000);
            publishThread.start();
            Thread.sleep(1000);

            while (true) {
                System.out.print(">: ");
                String msg = scanner.nextLine();
                if(msg.equals(exitKey))
                    break;
                else
                    msgQueue.add(msg);
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            subscriber.close();
            sub2.close();
            publisher.setSignal(false);
            publisher.close();
            try {
                subscribeThread.join();
                subscribeThread2.join();
                publishThread.join();
                System.out.println("All Threads Joined!");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
