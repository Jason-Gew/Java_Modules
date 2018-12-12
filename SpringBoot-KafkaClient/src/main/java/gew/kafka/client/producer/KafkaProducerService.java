package gew.kafka.client.producer;

import gew.kafka.client.config.KafkaProducerConfig;
import gew.kafka.client.entity.KafkaMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author Jason/GeW
 */
@Service("kafkaProducerService")
public class KafkaProducerService implements KfkProducer {

    @Autowired
    private KafkaProducerConfig producerConfig;

    @Autowired
    private KafkaTemplate<String, String> template;

    private static final Logger logger = LoggerFactory.getLogger(KafkaProducerService.class);

    @Override
    public void sendMessage(String message) {
        if (message != null) {
            template.send(producerConfig.getTopic(), message);
            logger.debug("Sent Message: " + message);
        } else {
            logger.error("Unable to send null message...");
        }
    }

    @Override
    public void sendMessage(String topic, String message) {
        if (message != null && topic != null) {
            template.send(topic, message);
        } else {
            logger.error("Unable to send null message or with null topic...");
        }
    }

    @Override
    public void sendMessage(String topic, String message, String key) {
        if (message != null && topic != null && key != null) {
            template.send(topic, key, message);
        } else {
            logger.error("Unable to send null message or with null topic or null key...");
        }
    }

    @Override
    public void sendMessages(List<String> messages) {
        if (messages != null && !messages.isEmpty()) {
            for (String message : messages) {
                template.send(producerConfig.getTopic(), message);
            }
        }
    }


    @Override
    public void sendKeyPairedMessages(final List<KafkaMessage> messages) {
        if (messages != null && !messages.isEmpty()) {
            for (KafkaMessage message : messages) {
                if (message.getKey() != null && message.getTopic() != null && message.getPartition() != null) {
                    template.send(message.getTopic(), message.getPartition(), message.getKey(), message.getValue());
                } else if (message.getTopic() != null && message.getPartition() != null) {
                    template.send(message.getTopic(), message.getPartition().toString(), message.getValue());
                } else if (message.getTopic() != null && message.getKey() != null) {
                    template.send(message.getTopic(), message.getKey(), message.getValue());
                } else if (message.getKey() != null) {
                    template.send(producerConfig.getTopic(), message.getKey(), message.getValue());
                } else {
                    template.send(producerConfig.getTopic(), message.getValue());
                }
            }
        }
    }
}
