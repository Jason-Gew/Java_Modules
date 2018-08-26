package gew.logging.service;

import gew.logging.Main;
import gew.logging.model.UserMessage;
import lombok.extern.log4j.Log4j2;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;

@Log4j2
public class IndependentConsumer implements Runnable {

    private Queue<UserMessage> messageQueue;
    private AtomicInteger receivedMessageNo = new AtomicInteger(1);

    public IndependentConsumer(Queue<UserMessage> messageQueue) {
        this.messageQueue = messageQueue;
    }

    @Override
    public void run() {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(Main.DATETIME_PATTERN);
        while (!Thread.currentThread().isInterrupted()) {
            UserMessage message = messageQueue.poll();
            if (message != null) {
                receivedMessageNo.incrementAndGet();
                log.info("Consumer Get Message [{}] @ {}", message.toString(), dateTimeFormatter.format(ZonedDateTime.now()));
            }
        }
    }
}
