package gew.data.warehouse.gps.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author Jason/GeW
 */
public class DatetimeConverter {

    public static final String UTC_DATETIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    public static final String UTC_DATETIME_WITH_MILLS_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    public static final String LOCAL_DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String LOCAL_DATETIME_WITH_MILLS_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
    public static final String US_DATETIME_FORMAT = "MM/dd/yyyy HH:mm:ss";
    public static final String US_DATETIME_WITH_MILLS_FORMAT = "MM/dd/yyyy HH:mm:ss.SSS";
    public static final DateTimeFormatter UTC_ISO_FORMAT = DateTimeFormatter.ISO_INSTANT;
    public static final DateTimeFormatter LOCAL_DATETIME_ISO_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    public static final DateTimeFormatter ZONED_DATETIME_ISO_FORMAT = DateTimeFormatter.ISO_ZONED_DATE_TIME;

    private DatetimeConverter() {
        // Static Class
    }

    public static Long toUnixTimestamp(final String raw) {
        if (raw == null || raw.isEmpty()) {
            throw new IllegalArgumentException("Invalid Timestamp: " + raw);
        }
        String stamp = raw.trim().toUpperCase();
        if (stamp.contains("T") && stamp.contains("Z") && !stamp.contains(" ")) {
            switch (stamp.length()) {
                case 20:
                    return toEpochTime(stamp, UTC_DATETIME_FORMAT, "Greenwich");

                case 24:
                    return toEpochTime(stamp, UTC_DATETIME_WITH_MILLS_FORMAT, "Greenwich");

                default:
                    throw new IllegalArgumentException("Unrecognized Timestamp: " + raw);
            }
        } else if (!stamp.contains("T") && !stamp.contains("Z") && stamp.contains(" ")) {
            switch (stamp.length()) {
                case 19:
                    return toEpochTime(stamp, LOCAL_DATETIME_FORMAT, "Greenwich");

                case 23:
                    return toEpochTime(stamp, LOCAL_DATETIME_WITH_MILLS_FORMAT, "Greenwich");

                default:
                    throw new IllegalArgumentException("Unrecognized Timestamp: " + raw);
            }
        } else if (!stamp.contains("T") && !stamp.contains("Z") && stamp.contains("/")) {
            switch (stamp.length()) {
                case 20:
                    return toEpochTime(stamp, US_DATETIME_FORMAT, "UTC");

                case 24:
                    return toEpochTime(stamp, US_DATETIME_WITH_MILLS_FORMAT, "UTC");

                default:
                    throw new IllegalArgumentException("Unrecognized Timestamp: " + raw);
        } else {
            try {
                CharSequence charSequence = new StringBuilder(stamp);
                return ZonedDateTime.parse(charSequence, ZONED_DATETIME_ISO_FORMAT).toEpochSecond();
            } catch (Exception err) {
                throw new IllegalArgumentException("Convert Timestamp Failed: " + err.getMessage(), err.getCause());
            }
        }
    }

    public static String toUTCDatetime(Long timestamp) {
        if (timestamp == null || timestamp < 1) {
            return null;
        } else {
            return Instant.ofEpochSecond(timestamp).toString();
        }
    }

    /**
     * Convert DateTime String to Destination Epoch Second
     * @param dateTime Origin Local Datetime
     * @param origin Origin ZoneId
     * @return Epoch Second (Long)
     */
    public static Long toEpochTime(final LocalDateTime dateTime, final ZoneId origin) {
        return dateTime.atZone(origin).toEpochSecond();
    }

    /**
     * Convert DateTime String to Destination Epoch Second
     * @param dateTime Origin Local Datetime (String)
     * @param format Datetime Pattern Format (String)
     * @param origin origin Origin ZoneId (String)
     * @return Epoch Second (Long)
     */
    public static Long toEpochTime(final String dateTime, final String format, final String origin) {
        return toEpochTime(LocalDateTime.parse(dateTime,
                DateTimeFormatter.ofPattern(format)), ZoneId.of(origin));
    }
}
