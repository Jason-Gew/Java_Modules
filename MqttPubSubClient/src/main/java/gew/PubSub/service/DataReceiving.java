package gew.pubsub.service;

import gew.pubsub.mqtt.MQTTMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Jason/GeW
 */
public class DataReceiving implements Runnable {

    private Queue<MQTTMessage> messageQ;
    private AtomicBoolean controlBit;

    private static final String FILE_PREFIX = "files";
    private static final String FILE_TOPIC = "/File";
    private static final Logger logger = LoggerFactory.getLogger(DataReceiving.class);

    public DataReceiving(Queue<MQTTMessage> messageQ) {
        this.messageQ = messageQ;
        controlBit = new AtomicBoolean(true);
    }

    public void stop() {
        controlBit.set(false);
    }

    @Override
    public void run() {
        while (controlBit.get()) {
            while (!messageQ.isEmpty()) {
                try {
//                    MQTTMessage message = messageQ.take();     // Use for BlockingQueue Only
                    MQTTMessage message = messageQ.poll();

                    if (message.getTopic().contains(FILE_TOPIC)) {
                        String fileName = message.getTopic().substring(message.getTopic().lastIndexOf("/") + 1);
                        try (FileOutputStream outputStream = new FileOutputStream(FILE_PREFIX + "/" + fileName)) {
                            outputStream.write(message.getMessage());
                            logger.info("-> File [ {} ] Has Been Stored Success", fileName);
                        } catch (IOException err) {
                            logger.error("-> Store File [ {} ] Failed: {}", fileName, err.getMessage());
                        }
                    } else {
                        logger.info("-> Received Message From Topic [{}]: {}", message.getTopic(),
                                new String(message.getMessage(), StandardCharsets.UTF_8));
                    }

                } catch (Exception e) {
                    logger.error("-> Reading Data Exception: {}", e.getMessage());
                }
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException err) {
                logger.error("-> Data Receiving Got Interrupted...");
            }
        }
        logger.info("-> Data Receiving Terminated...");
    }
}
