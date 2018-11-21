package caching.memcache.client;

import net.rubyeye.xmemcached.exception.MemcachedException;
import net.rubyeye.xmemcached.transcoders.Transcoder;

import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author Jason/GeW
 * @since 2017/12/21
 */
public interface MClient {

    static MClient getInstance() {
        System.err.println("This Static Method Should Not Be Invoked From the Interface.");
        return null;
    }

    static void setClientConfig(MClientConfig clientConfig) {
        System.err.println("This Static Method Should Not Be Invoked From the Interface.");
    }

    boolean initialize();

    <T> Optional<T> get(final String key) throws TimeoutException, MemcachedException;

    <T> Optional<T> get(final String key, final Integer timeout, final TimeUnit unit) throws TimeoutException, MemcachedException;

    Optional<Object> get(final String key, final Transcoder transcoder) throws TimeoutException, MemcachedException;

    Optional<Object> get(final String key, final Integer timeout, final Transcoder transcoder) throws TimeoutException, MemcachedException;

    boolean set(final String key, Object value)throws TimeoutException, MemcachedException;

    boolean set(final String key, Object value, final Integer time, final TimeUnit unit) throws TimeoutException, MemcachedException;

    boolean add(final String key, Object value) throws TimeoutException, MemcachedException;

    boolean add(final String key, Object value, final Integer time, final TimeUnit unit) throws TimeoutException, MemcachedException;

    boolean append(final String key, final Object value) throws TimeoutException, MemcachedException ;

    boolean touch(final String key, final Integer time, final TimeUnit unit) throws TimeoutException, MemcachedException;

    boolean delete(final String key) throws TimeoutException, MemcachedException;

    boolean flushAll();

    void close();
}
