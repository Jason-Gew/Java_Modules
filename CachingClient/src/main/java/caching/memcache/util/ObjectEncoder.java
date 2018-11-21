package caching.memcache.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Base64;

/**
 * @author Jason/GeW
 */
public class ObjectEncoder {

    public static String encodeToString(final Object object) throws Exception {

        byte[] arrayObject = encodeToByteArray(object);
        return Base64.getEncoder().encodeToString(arrayObject);
    }

    public static Object decodeFromString(final String value) throws Exception {

        byte arrayObject[] = Base64.getDecoder().decode(value);
        return decodeFromByteArray(arrayObject);
    }

    public static byte[] encodeToByteArray(final Object object) throws Exception {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);

        objectOutputStream.writeObject(object);
        objectOutputStream.flush();

        return byteArrayOutputStream.toByteArray();
    }

    public static Object decodeFromByteArray(final byte[] arrayObject) throws Exception {

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(arrayObject);
        ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);

        return objectInputStream.readObject();
    }
}
