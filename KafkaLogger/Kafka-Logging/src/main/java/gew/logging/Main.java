package gew.logging;

import gew.logging.model.UserMessage;
import gew.logging.service.IndependentConsumer;
import gew.logging.service.IndependentProducer;
import gew.logging.util.NetworkInfo;
import lombok.extern.log4j.Log4j2;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Log4j2
public class Main {

    public static String DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss z";

    public static void main(String[] args) {
        preInit(args);
        ZonedDateTime zonedDateTime = ZonedDateTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DATETIME_PATTERN);
        log.info("Example Logging Started @ " + dateTimeFormatter.format(zonedDateTime));
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            log.warn("System Sleep Got Interrupted");
        } catch (Exception err) {
            log.error(err.getMessage());
        }
        startExample();
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        log.info("System Stopped  @  " + dateTimeFormatter.format(ZonedDateTime.now()));
    }

    private static void preInit(String[] args) {
        String localIP = NetworkInfo.getLocalIP();
        String macAddress = NetworkInfo.getMacAddress();

        System.setProperty("ip", localIP);
        System.setProperty("mac", macAddress);
    }

    private static boolean startExample() {
        Queue<UserMessage> queue = new ConcurrentLinkedQueue<>();
        IndependentConsumer consumer = new IndependentConsumer(queue);
        Thread consumerThread = new Thread(consumer);
        consumerThread.setDaemon(true);
        consumerThread.setName("Consumer-1");
        IndependentProducer producer = new IndependentProducer(queue, 10);
        Thread producerThread = new Thread(producer);
        producerThread.setName("Producer-1");

        consumerThread.start();
        producerThread.start();

        log.info("All Threads Start!");
        while (!producerThread.isAlive()) {
            consumerThread.interrupt();
        }
        return true;
    }
}
