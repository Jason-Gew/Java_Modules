package gew.kafka.client.controller;

import gew.kafka.client.config.KafkaProducerConfig;
import gew.kafka.client.producer.KfkProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author Jason/GeW
 */
@RestController
@RequestMapping("/kafka")
public class ManualMessageController {

    @Autowired
    private KafkaProducerConfig producerConfig;

    @Autowired
    private KfkProducer producer;

    private static final Logger logger = LoggerFactory.getLogger(ManualMessageController.class);

    @GetMapping(value = "/send", params = {"message"})
    public ResponseEntity<Map<String, Object>> send(@RequestParam(value = "message") final String message,
                                                    @RequestParam(value = "key") Optional<String> key)
    {
        Map<String, Object> result = new HashMap<>();
        ZonedDateTime timestamp = ZonedDateTime.now();
        result.put("Timestamp", timestamp.toString());
        if(message != null && !message.isEmpty())
        {
            if(key.isPresent())
                producer.sendMessage(producerConfig.getTopic(), message, key.get());
            else
                producer.sendMessage(producerConfig.getTopic(), message);
            result.put("Result", "Producer is sending the data...");
        }
        else
        {
            result.put("Result", "Invalid Message.");
        }

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping(value = "/send", produces = "application/json")
    public ResponseEntity<Map<String, Object>> send(@RequestBody final Map<String, String> message)
    {
        Map<String, Object> result = new HashMap<>();
        ZonedDateTime timestamp = ZonedDateTime.now();
        result.put("Timestamp", timestamp.toString());
        logger.info("Get Kafka Message from Post Request: " + message.toString());
        if(!message.isEmpty() && message.containsKey("Message"))
        {
            if(message.containsKey("Key") && message.get("Key")!= null)
                producer.sendMessage(producerConfig.getTopic(), message.get("Message"), message.get("Key"));
            else
                producer.sendMessage(producerConfig.getTopic(), message.get("Message"));
            result.put("Result", "Producer is sending the data...");
        }
        else
        {
            result.put("Result", "Invalid Message.");
        }

        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
