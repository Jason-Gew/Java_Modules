
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import java.util.stream.Collectors;

/**
 * @author Jason/GeW
 */
public class HashUtil {


    enum Algorithm {
        MD5("MD5"),
        SHA1("SHA1"),
        SHA256("SHA256"),
        SHA384("SHA384"),
        BCrypt("Bcrypt");

        private final String name;

        Algorithm(String a) {
            name = a;
        }
        public String toString() {
            return this.name;
        }
    }

    private static final int BCRYPT_HASH_ROUND = 10;

    private HashUtil() {
        // Static Class
    }

    private static void checkContent(final String... content) {
        if (content == null || content.length == 0) {
            throw new IllegalArgumentException("Invalid Content");
        }
    }

    private static Algorithm convert(final String algorithm) {
        if (Algorithm.MD5.toString().equalsIgnoreCase(algorithm) || algorithm.toUpperCase().equals("MD-5")) {
            return Algorithm.MD5;
        } else if (Algorithm.SHA1.toString().equalsIgnoreCase(algorithm) || algorithm.toUpperCase().equals("SHA-1")) {
            return Algorithm.SHA1;
        } else if (Algorithm.SHA256.toString().equalsIgnoreCase(algorithm) || algorithm.toUpperCase().equals("SHA-256")) {
            return Algorithm.SHA256;
        } else if (Algorithm.SHA384.toString().equalsIgnoreCase(algorithm) || algorithm.toUpperCase().equals("SHA-384")) {
            return Algorithm.SHA384;
        } else if (Algorithm.BCrypt.toString().equalsIgnoreCase(algorithm)) {
            return Algorithm.BCrypt;
        } else {
            throw new IllegalArgumentException("Algorithm Not In Provide");
        }
    }

    public static String secureHash(final String algorithm, String... data) {

        switch (convert(algorithm)) {
            case MD5:
                return md5(data);
            case SHA1:
                return sha1(data);
            case SHA256:
                return sha256(data);
            case SHA384:
                return sha384(data);
            case BCrypt:
                return bCrypt(data);
            default:
                return sha1(data);
        }
    }


    public static byte[] digest(final String algorithm, final String... content) {
        checkContent(content);

        StringBuilder sb = new StringBuilder();
        Arrays.stream(content).filter(s -> s != null && s.length() > 0).forEach(sb::append);
        try {
            MessageDigest md = MessageDigest.getInstance(algorithm);
            md.update(sb.toString().getBytes(StandardCharsets.UTF_8));
            return md.digest();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return sb.toString().getBytes();
        }
    }

    public static String md5(final String... content) {
        return Base64.getEncoder().encodeToString(digest("MD5", content));
    }

    public static String sha1(final String... content) {
        return Base64.getEncoder().encodeToString(digest("SHA-1", content));
    }

    public static String sha256(final String... content) {
        return Base64.getEncoder().encodeToString(digest("SHA-256", content));
    }

    public static String sha384(final String... content) {
        return Base64.getEncoder().encodeToString(digest("SHA-384", content));
    }

    public static String bCrypt(final String... content) {
        checkContent(content);
        String raw = Arrays.stream(content)
                .filter(s -> s != null && s.length() > 0)
                .collect(Collectors.joining());
        return BCrypt.hashpw(raw, BCrypt.gensalt(BCRYPT_HASH_ROUND));
    }
}


