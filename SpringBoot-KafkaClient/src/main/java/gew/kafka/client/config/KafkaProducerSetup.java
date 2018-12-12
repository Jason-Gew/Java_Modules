package gew.kafka.client.config;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Jason/GeW
 * @since 2018-1-16
 */
@Configuration
public class KafkaProducerSetup {

    @Autowired
    private KafkaProducerConfig config;

    @Bean
    public ProducerFactory<String, String> producerFactory() {
        return new DefaultKafkaProducerFactory<>(createProducerConfig());
    }

    private Map<String, Object> createProducerConfig() {

        Map<String, Object> properties = new HashMap<>();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, config.getServer());
        properties.put(ProducerConfig.RETRIES_CONFIG, config.getRetries());
        properties.put(ProducerConfig.CLIENT_ID_CONFIG, config.getClientId());
        properties.put(ProducerConfig.ACKS_CONFIG, config.getRequireAck());
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, config.getSerializeClass());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, config.getSerializeClass());
        return properties;
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}
