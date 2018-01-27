package gew.PubSub.entity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class DataReceiving implements Runnable
{

    private BlockingQueue<String[]> messageQ;
    private AtomicBoolean controlBit;

    private static final Logger logger = LoggerFactory.getLogger(DataReceiving.class);

    public DataReceiving(BlockingQueue<String[]> messageQ) {
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
                try {
                    String[] message = messageQ.take();
                    if(message.length == 2)
                    {
                        String topic = message[0];
                        String payload = message[1];
                        logger.info("> Received Message From Topic [{}] : {}", topic, payload);
                    }
                    else
                    {
                        logger.warn("-> Message From Invalid Format, Length of Message Array: {}", message.length);
                    }

                } catch (InterruptedException e) {
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
