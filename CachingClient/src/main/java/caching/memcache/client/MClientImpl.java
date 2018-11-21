package caching.memcache.client;

import caching.memcache.util.TimeIntervalHelper;
import com.google.code.yanf4j.config.Configuration;
import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.MemcachedClientBuilder;
import net.rubyeye.xmemcached.XMemcachedClientBuilder;
import net.rubyeye.xmemcached.auth.AuthInfo;
import net.rubyeye.xmemcached.command.BinaryCommandFactory;
import net.rubyeye.xmemcached.exception.MemcachedException;
import net.rubyeye.xmemcached.transcoders.Transcoder;
import net.rubyeye.xmemcached.utils.AddrUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Memcached Singleton Client
 * @author Jason/GeW
 * @since 2017/12/21
 */
public class MClientImpl implements MClient {

    private MemcachedClient mcClient;

    private static volatile MClientImpl instance;
    private static MClientConfig CLIENT_CONFIG;
    private static final int MAX_INTERVAL = TimeIntervalHelper.convertDaysToMilliSeconds(30).intValue() / 1000;
    private static final Logger logger = LoggerFactory.getLogger(MClient.class);


    private MClientImpl() {
        if (CLIENT_CONFIG == null || CLIENT_CONFIG.getAddress() == null || !CLIENT_CONFIG.getAddress().contains(":")) {
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
        if (CLIENT_CONFIG.isEnableSASL()) {
            logger.info("Enable SASL for Connecting Memcached Server: {}", CLIENT_CONFIG.getAddress());
            builder.addAuthInfo((AddrUtil.getOneAddress(CLIENT_CONFIG.getAddress())),
                    AuthInfo.typical(CLIENT_CONFIG.getUsername(), CLIENT_CONFIG.getPassword()));
        }
        builder.setCommandFactory(new BinaryCommandFactory());
        Configuration configuration = XMemcachedClientBuilder.getDefaultConfiguration();
        configuration.setSessionIdleTimeout(CLIENT_CONFIG.getIdleTimeout());
        builder.setConfiguration(configuration);

        try {
            mcClient = builder.build();
            status = true;
        } catch (IOException e) {
            logger.error("Initialize Memcached Client Failed: {}", e.getMessage());
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
    public <T> Optional<T> get(String key, Integer timeout, TimeUnit unit) throws TimeoutException, MemcachedException {
        Optional<T> value = Optional.empty();
        try {
            T t = mcClient.get(key, TimeIntervalHelper.convertTimeToMilliSeconds(timeout, unit));
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
        return set(key, value, 0, null);
    }

    @Override
    public boolean set(String key, Object value, Integer time, TimeUnit unit) throws TimeoutException, MemcachedException {
        boolean status = false;
        try {
            if (time == null || time <= 0) {
                status = mcClient.set(key, 0, value);
            } else if (time > MAX_INTERVAL) {
                logger.info("Key [{}] Expiry Time [{}] Longer Than Max Interval 30 Days, Consider As UNIX Timestamp",
                        key, time);
                status = mcClient.set(key, time, value);
            } else {
                status = mcClient.set(key, TimeIntervalHelper.convertTimeToMilliSeconds(time, unit).intValue() / 1000,
                        value);
            }
        } catch (InterruptedException e) {
            logger.error("Set Key [{}] with Value:[{}] Got Interrupted...", key, value);
        }
        return status;
    }

    @Override
    public boolean add(String key, Object value) throws TimeoutException, MemcachedException {
        return add(key, value, 0, null);
    }

    @Override
    public boolean add(String key, Object value, Integer time, TimeUnit unit) throws TimeoutException, MemcachedException {
        boolean status = false;
        try {
            if (time == null || time <= 0) {
                status = mcClient.add(key, 0, value);
            } else if (time > MAX_INTERVAL) {
                logger.info("Key [{}] Expiry Time [{}] Longer Than Max Interval 30 Days, Consider As UNIX Timestamp",
                        key, time);
                status = mcClient.add(key, time, value);
            } else {
                status = mcClient.add(key, TimeIntervalHelper.convertTimeToMilliSeconds(time, unit).intValue() / 1000,
                        value);
            }
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
    public boolean touch(String key, Integer time, TimeUnit unit) throws TimeoutException, MemcachedException {
        boolean status = false;
        try {
            if (time == null || time <= 0) {
                status = mcClient.touch(key, 0);
            } else if (time > MAX_INTERVAL) {
                logger.info("Key [{}] Expiry Time [{}] Longer Than Max Interval 30 Days, Consider As UNIX Timestamp",
                        key, time);
                status = mcClient.touch(key, time);
            } else {
                status = mcClient.touch(key, TimeIntervalHelper.convertTimeToMilliSeconds(time, unit).intValue() / 1000);
            }
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
