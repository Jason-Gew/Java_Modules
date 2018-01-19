package gew.kafka.client.consumer;

import gew.kafka.client.config.KafkaConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;

import java.util.Optional;


public class KafkaConsumerService implements KfkComsumer{

    @Autowired
    private KafkaConsumerConfig consumerConfig;

    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumerService.class);


    @KafkaListener(topics = "${kafka.consumer.topic}", group = "${kafka.consumer.groupId}")
    public void listen(ConsumerRecord<?, ?> record)
    {
        Optional<?> kafkaMessage = Optional.ofNullable(record.value());
        if(kafkaMessage.isPresent())
        {
            Object message = kafkaMessage.get();
            try {
                if(message instanceof String)
                {
                    logger.info("Partition[{}] -> Received Key[{}], Message: {}", record.partition(), record.key(), message);
                }
                else
                {
                    logger.info("Partition[{}] -> Received Key[{}], Message: {}", record.partition(), record.key(), message);
                }
            } catch (Exception err) {
                logger.error("Error in Receiving Message...");
            }
        }
    }
}
