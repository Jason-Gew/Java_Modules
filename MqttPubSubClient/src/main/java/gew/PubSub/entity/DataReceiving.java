package gew.PubSub.entity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Jason/GeW
 */
public class DataReceiving implements Runnable
{

    private Queue<String[]> messageQ;
    private AtomicBoolean controlBit;

    private static final Logger logger = LoggerFactory.getLogger(DataReceiving.class);

    public DataReceiving(Queue<String[]> messageQ) {
        this.messageQ = messageQ;
        controlBit = new AtomicBoolean(true);
    }

    public void stop()
    {
        controlBit.set(false);
    }

    @Override
    public void run() {
        while (controlBit.get()) {
            while (!messageQ.isEmpty()) {
                System.err.println("Queue Size: " + messageQ.size());
                try {
//                    String[] message = messageQ.take();     // Use for BlockingQueue Only
                    String[] message = messageQ.poll();

                    if(message.length == 2)
                    {
                        String topic = message[0];
                        String payload = message[1];
                        logger.info("> Received Message From Topic [{}]:\n{}", topic, payload);
                    }
                    else
                    {
                        logger.warn("-> Message From Invalid Format, Length of Message Array: {}", message.length);
                    }

                } catch (Exception e) {
                    logger.warn("-> Reading Data from Queue Got Interrupted...");
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
