package caching.memcache.client;

import com.google.code.yanf4j.config.Configuration;
import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.MemcachedClientBuilder;
import net.rubyeye.xmemcached.XMemcachedClientBuilder;
import net.rubyeye.xmemcached.exception.MemcachedException;
import net.rubyeye.xmemcached.transcoders.Transcoder;
import net.rubyeye.xmemcached.utils.AddrUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.TimeoutException;

/**
 * @author Jason/GeW
 * @since 2017/12/21
 */
public class MClientImpl implements MClient {

    private MemcachedClient mcClient;

    private static volatile MClientImpl instance;
    private static MClientConfig CLIENT_CONFIG;
    private static final Logger logger = LoggerFactory.getLogger(MClient.class);

    private MClientImpl() {
        if (CLIENT_CONFIG == null || CLIENT_CONFIG.getAddress() == null) {
            throw new IllegalArgumentException("Invalid Memcached Client Config");
        }
    }

    public static MClientImpl getInstance() {
        if (instance == null) {
            synchronized (MClientImpl.class) {
                if (instance == null) {
                    instance = new MClientImpl();
                }
            }
        }
        return instance;
    }

    public static void setClientConfig(MClientConfig clientConfig) {
        MClientImpl.CLIENT_CONFIG = clientConfig;
    }

    @Override
    public boolean initialize() {
        boolean status = false;
        MemcachedClientBuilder builder = new XMemcachedClientBuilder(AddrUtil.getAddresses(CLIENT_CONFIG.getAddress()));
        builder.setConnectTimeout(CLIENT_CONFIG.getConnectTimeout());
        builder.setConnectionPoolSize(CLIENT_CONFIG.getConnectionPoolSize());
        builder.setEnableHealSession(CLIENT_CONFIG.getHealSession());
        builder.setHealSessionInterval(CLIENT_CONFIG.getHealSessionInterval());
        Configuration configuration = XMemcachedClientBuilder.getDefaultConfiguration();
        configuration.setSessionIdleTimeout(CLIENT_CONFIG.getIdleTimeout());
        builder.setConfiguration(configuration);

        try {
            mcClient = builder.build();
            status = true;
        } catch (IOException e) {
            logger.error("Initialize Memcached Client Failed: ", e.getMessage());
        }
        return status;
    }

    @Override
    public <T> Optional<T> get(String key) throws TimeoutException, MemcachedException {
        Optional<T> value = Optional.empty();

        try {
            T t = mcClient.get(key);
            value = Optional.of(t);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return value;
    }

    @Override
    public <T> Optional<T> get(String key, Integer timeout) throws TimeoutException, MemcachedException {
        Optional<T> value = Optional.empty();
        try {
            T t = mcClient.get(key, timeout);
            value = Optional.of(t);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return value;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Optional<Object> get(String key, Transcoder transcoder) throws TimeoutException, MemcachedException {
        Optional<Object> value = Optional.empty();
        if (transcoder == null) {
            throw new IllegalArgumentException("Invalid Transcoder");
        }
        try {
            Object object = mcClient.get(key, transcoder);
            if (object != null) {
                value = Optional.of(object);
            }
        } catch (InterruptedException e) {
            logger.error("Fetch Value for Key [{}] Got Interrupted...", e.getMessage());
        }
        return value;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Optional<Object> get(String key, Integer timeout, Transcoder transcoder) throws TimeoutException, MemcachedException {
        Optional<Object> value = Optional.empty();
        if (transcoder == null) {
            throw new IllegalArgumentException("Invalid Transcoder");
        }
        try {
            Object object = mcClient.get(key, timeout, transcoder);
            if (object != null) {
                value = Optional.of(object);
            }
        } catch (InterruptedException e) {
            logger.error("Fetch Value for Key [{}] Got Interrupted...", e.getMessage());
        }
        return value;
    }

    @Override
    public boolean set(String key, Object value) throws TimeoutException, MemcachedException {
        return set(key, 0, value);
    }

    @Override
    public boolean set(String key, Integer timeout, Object value) throws TimeoutException, MemcachedException {
        boolean status = false;
        try {
            status = mcClient.set(key, timeout, value);
        } catch (InterruptedException e) {
            logger.error("Set Key [{}] with Value:[{}] Got Interrupted...", key, value);
        }
        return status;
    }

    @Override
    public boolean add(String key, Object value) throws TimeoutException, MemcachedException {
        return add(key, 0, value);
    }

    @Override
    public boolean add(String key, Integer timeout, Object value) throws TimeoutException, MemcachedException {
        boolean status = false;
        try {
            status = mcClient.add(key, timeout, value);
        } catch (InterruptedException e) {
            logger.error("Add Key [{}] Got Interrupted with Value: {}", key, value);
        }
        return status;
    }

    @Override
    public boolean append(String key, Object value) throws TimeoutException, MemcachedException {
        boolean status = false;
        try {
            status = mcClient.append(key, value);
        } catch (InterruptedException e) {
            logger.error("Append Key [{}] Got Interrupted with Value: {}", key, value);
        }
        return status;
    }

    @Override
    public boolean touch(String key, Integer timeout) throws TimeoutException, MemcachedException {
        boolean status = false;
        try {
            status = mcClient.touch(key, timeout);
        } catch (InterruptedException e) {
            logger.error("Touch Key [{}] Got Interrupted...", key);
        }
        return status;
    }

    @Override
    public boolean delete(String key) throws TimeoutException, MemcachedException {
        boolean status = false;
        try {
            status = mcClient.delete(key);
        } catch (InterruptedException e) {
            logger.error("Delete Key [{}] Got Interrupted...", key);
        }
        return status;
    }

    @Override
    public boolean flushAll() {
        boolean status = false;
        try {
            mcClient.flushAll();
            status = true;
        } catch (InterruptedException e) {
            logger.error("Flush All Data Got Interrupted...");
        } catch (Exception err) {
            logger.error("Flush All Error: {}", err.getMessage());
        }
        return status;
    }

    @Override
    public void close() {
        try {
            if (mcClient != null) {
                mcClient.shutdown();
            }
        } catch (IOException e) {
            logger.warn("Memcached Client Close Error: " + e.getMessage());
        }
    }
}
