package gew.logging.service;

import gew.logging.Main;
import gew.logging.model.UserMessage;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Queue;
import java.util.Random;

@Log4j2
@AllArgsConstructor
public class IndependentProducer implements Runnable {

    private Queue<UserMessage> messageQueue;
    private Integer autoLoopSize;


    private Integer generateRandomInt() {
        Random random = new Random();
        return random.nextInt(10);
    }

    @Override
    public void run() {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(Main.DATETIME_PATTERN);
        if (autoLoopSize != null && autoLoopSize > 0) {
            for (int i = 1; i <= autoLoopSize; i++) {
                UserMessage message = new UserMessage(i, generateRandomInt(),
                        dateTimeFormatter.format(ZonedDateTime.now()), "User MSG [" + i + "]");
                boolean status = messageQueue.add(message);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                log.info("Put User Message {} : {}", message.toString(), status);
            }
        }
        log.info("Producer Thread Finish Sending [{}] Message!", autoLoopSize);
    }
}
