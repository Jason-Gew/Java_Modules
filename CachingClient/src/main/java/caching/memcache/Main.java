package caching.memcache;

import caching.memcache.client.MClient;
import caching.memcache.client.MClientConfig;
import caching.memcache.client.MClientImpl;
import caching.memcache.model.Message;
import caching.memcache.util.ObjectEncoder;
import caching.memcache.util.UniqueTranscoder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.rubyeye.xmemcached.transcoders.Transcoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * @author Jason/GeW
 */
public class Main {

    private static final String ADDRESS = "localhost:11211";

    private static final Logger logger = LoggerFactory.getLogger(Main.class);


    public static void main(String[] args) throws InterruptedException {

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
//        getString(cache, "test1");

        /* Set JSON Value in String */
        Message message = new Message(123456, "Hello World!", Instant.now().toString(),
                Arrays.asList("Test", "Message", "List", "Var"));
        setJson(cache, "test-json", message);

        /* Set Serialize Object and Encode */
        setSerializedObject(cache, "test-object", message);

        Thread.sleep(500);
        /* Get Serialized Object and Decode */
        getAndDeserializeObject(cache, "test-object");

        //  According to the test, storing Object content in JSON format occupies less memory, then byte array.
        //  Keep object serialized and encoded to String by Base64 occupies the highest memory.

        //  Get the value from memcached which is set by the C# Enyim client. It does not support GZIP Flag,
        //  So that we have to re-write StringTranscoder or Any Unique Transcoder which extends PrimitiveTypeTranscoder.
        getValueByUniqueTranscoder(cache, "test-c#", new UniqueTranscoder());

        cache.close();
    }

    private static void getString(MClient cache, String key) {
        try {
            Optional<String> result2 = cache.get(key);
            result2.ifPresent(System.out::println);

        } catch (Exception e) {
            if (e instanceof NullPointerException) {
                logger.error("Value for Key [{}] Does Not Exist... ", key);
            } else {
                logger.error("Get Value Error: " + e.getMessage());
            }
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
            boolean status = cache.set(key, ObjectEncoder.encodeToByteArray(message), 1, TimeUnit.HOURS);
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
                logger.info("Get De-Serialized Message for key [{}]: {}", key, message.toString());
            }
        } catch (Exception e) {
            if (e instanceof NullPointerException) {
                logger.error("De-Serialized Value for Key [{}] Does Not Exist...", key);
            } else {
                logger.error(e.getMessage());
            }
        }
    }

    private static void getValueByUniqueTranscoder(MClient cache, String key, Transcoder transcoder) {
        try {
            Optional<Object> value = cache.get(key, transcoder);
            if (value.isPresent()) {
                logger.info("Get Unique Transcoder Value For Key [" + key + "]: " + value.get());
            } else {
                logger.warn("Unique Transcoder Value for Key [{}] Does Not Exist!", key);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }
}
