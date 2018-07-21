package caching.memcache;

import caching.memcache.client.MClient;
import caching.memcache.client.MClientConfig;
import caching.memcache.client.MClientImpl;
import caching.memcache.model.Message;
import caching.memcache.util.ObjectEncoder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.Arrays;
import java.util.Optional;

/**
 * @author Jason/GeW
 */
public class Main {

    private static final String ADDRESS = "localhost:11211";

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {

        MClientConfig config = new MClientConfig();
        config.setAddress(ADDRESS);
        MClientImpl.setClientConfig(config);
        MClient cache = MClientImpl.getInstance();

        boolean status = cache.initialize();
        if (status) {
            logger.info("-> Memcached Client Initialize Success!");
        } else {
            logger.error("-> Memcached Client Initialize Failed!");
            System.exit(1);
        }

        /* Get Normal String Value */
//        getString(cache, "test");

        /* Set JSON Value in String */
        Message message = new Message(123, "Hello World!", Instant.now().toString(),
                Arrays.asList("Test", "Message", "List"));
//        setJson(cache, "test3", message);

        /* Set Serialize Object and Encode */
        setSerializedObject(cache, "test9", message);

        /* Get Serialized Object and Decode */
        getAndDeserializeObject(cache, "test9");

        //  According to the test, storing Object content in JSON format occupies less memory, then byte array.
        //  Keep object serialized and encoded to String by Base64 occupies the highest memory.

        cache.close();
    }

    private static void getString(MClient cache, String key) {

        try {
            Optional<String> result2 = cache.get(key);
            result2.ifPresent(System.out::println);

        } catch (Exception e) {
            logger.error(e.getMessage());
        }

    }

    private static void setJson(MClient cache, String key, Message message) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            String value = mapper.writeValueAsString(message);
            boolean status = cache.set(key, value);
            logger.info("Set JSON Value for Key [{}] Result: {}", key, status);

        } catch (JsonProcessingException e) {
            logger.error(e.getMessage());
        } catch (Exception err) {
            logger.error("Set JSON Value Error: " + err.getMessage());
        }
    }

    private static void setSerializedObject(MClient cache, String key, Message message) {

        try {
            boolean status = cache.set(key, ObjectEncoder.encodeToByteArray(message));
            logger.info("Set Serialized Object for Key [{}] Result: {}", key, status);

        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    private static void getAndDeserializeObject(MClient cache, String key) {

        try {
            Optional<byte[]> value = cache.get(key);
            if (value.isPresent()) {
                Message message = (Message) ObjectEncoder.decodeFromByteArray(value.get());
                logger.info("Get Message: " + message.toString());
            }

        } catch (Exception e) {
            logger.error(e.getMessage());
        }

    }
}
