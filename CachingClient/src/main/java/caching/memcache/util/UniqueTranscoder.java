package caching.memcache.util;

import net.rubyeye.xmemcached.transcoders.BaseSerializingTranscoder;
import net.rubyeye.xmemcached.transcoders.CachedData;
import net.rubyeye.xmemcached.transcoders.PrimitiveTypeTranscoder;

import java.io.UnsupportedEncodingException;

/**
 * For Decoding Memcached String Value Which is Set By C# Enyim Client.
 * (No GZIP Flag Supported)
 * @author Jason/GeW
 * @since 2018-1-15
 */
public class UniqueTranscoder extends PrimitiveTypeTranscoder<String> {

    private String charset = BaseSerializingTranscoder.DEFAULT_CHARSET;
    private static final int STRING_FLAG = 0;

    public UniqueTranscoder() {
        this(BaseSerializingTranscoder.DEFAULT_CHARSET);
    }

    public UniqueTranscoder(String charset) {
        this.charset = charset;
    }

    @Override
    public CachedData encode(String s) {
        byte[] b;

        try {
            b = s.getBytes(this.charset);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        return new CachedData(STRING_FLAG, b);
    }

    @Override
    public String decode(CachedData cachedData) {
        String rv = null;
        try {
            if (cachedData.getData() != null) {
                rv = new String(cachedData.getData(), this.charset);
            }
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        return rv;
    }
}
