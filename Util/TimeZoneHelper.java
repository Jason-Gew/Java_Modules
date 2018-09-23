package gew.util;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


/**
 * Date Time and Time Zone Helper, easy to convert different format of datetime across different time zones.
 * This class is under continuous integration.
 *
 * @author Jason/GeW
 * @since 2018-09-16
 */
public class TimeZoneHelper {

    private static final String DATE_TIME_FORMAT_WITH_SEC = "yyyy-MM-dd HH:mm:ss";
    private static final String DATE_TIME_FORMAT_WITH_MILLIS = "yyyy-MM-dd HH:mm:ss.SSS";

    private static Map<String, String> TIMEZONE_WITH_OFFSET;

    public enum ShortZoneId {
        ACT,
        AET,
        AGT,
        ART,
        AST,
        BET,
        BST,
        CAT,
        CNT,
        CST,
        CTT,
        EAT,
        ECT,
        IET,
        IST,
        JST,
        MIT,
        NET,
        NST,
        PLT,
        PNT,
        PRT,
        PST,
        SST,
        VST,
        EST,
        MST,
        HST
    }

    public static Map<String, String> getTypicalTimeZoneIdWithName() {
        return ZoneId.SHORT_IDS;
    }


    public static Map<String, String> getAllTimeZoneIdWithOffset() {
        Map<String, String> result = new HashMap<>(1024);
        if (TIMEZONE_WITH_OFFSET != null) {
            return TIMEZONE_WITH_OFFSET;
        }
        LocalDateTime datetime = LocalDateTime.now();
        for (String zoneId : ZoneId.getAvailableZoneIds()) {

            ZoneId zone = ZoneId.of(zoneId);
            ZonedDateTime zdt = datetime.atZone(zone);
            ZoneOffset zos = zdt.getOffset();
            //replace Z to +00:00
            String offset = zos.getId().replaceAll("Z", "+00:00");

            result.put(zone.toString(), offset);
        }
        TIMEZONE_WITH_OFFSET = Collections.unmodifiableMap(result);
        return TIMEZONE_WITH_OFFSET;
    }

    /**
     * Retrieve TimeZone Offset By Location Name
     * @param location String Location Name (America/Chicago)
     * @return String UTC Offset (-06:00)
     */
    public static String getTimeZoneOffsetByLocation(String location) {
        if (TIMEZONE_WITH_OFFSET == null) {
            getAllTimeZoneIdWithOffset();
        }
        return TIMEZONE_WITH_OFFSET.get(location);
    }

    /**
     * Get Time Zone Area Name By UTC Offset
     * @param offset String UTC Offset (-06:00)
     * @return String  Location Name (America/Chicago)
     */
    public static Set<String> getTimeZoneIdByOffset(String offset) {
        Set<String> result = new HashSet<>();
        offset = offset.trim().toUpperCase();
        if (offset.contains("UTC")) {
            offset = offset.replace("UTC", "");
        }
        if (offset.length() < 6 || (!offset.contains("+") && !offset.contains("-"))) {
            throw new IllegalArgumentException("Invalid Time Offset Format");
        }
        if (TIMEZONE_WITH_OFFSET == null) {
            getAllTimeZoneIdWithOffset();
        }
        String finalOffset = offset.trim();
        TIMEZONE_WITH_OFFSET.forEach((k, v) -> {if (v.equals(finalOffset)) result.add(k);});
        return Collections.unmodifiableSet(result);
    }

    /**
     * Convert Original ZonedDateTime to Destination ZonedDatetime
     * @param dateTime ZoneDateTime of Original Location
     * @param zoneId Destination ZoneId (String)
     * @return ZoneDataTime of Destination Location
     */
    public static ZonedDateTime toDestinationTimeZone(final ZonedDateTime dateTime, final String zoneId) {
        return toDestinationTimeZone(dateTime, ZoneId.of(zoneId));
    }

    /**
     * Convert Original ZonedDateTime to Destination ZonedDatetime
     * @param dateTime ZoneDateTime of Original Location
     * @param zoneId Destination ZoneId
     * @return ZoneDataTime of Destination Location
     */
    public static ZonedDateTime toDestinationTimeZone(final ZonedDateTime dateTime, final ZoneId zoneId) {
        return dateTime.withZoneSameInstant(zoneId);
    }

    /**
     * Convert Original ZonedDateTime to Destination ZonedDatetime
     * @param dateTime ZoneDateTime of Original Location
     * @param shortZoneId Destination Short ZoneID (CST)
     * @return ZoneDataTime of Destination Location
     */
    public static ZonedDateTime toDestinationTimeZone(final ZonedDateTime dateTime, ShortZoneId shortZoneId) {
        return toDestinationTimeZone(dateTime, ZoneId.SHORT_IDS.get(shortZoneId.toString()));
    }

    /**
     * Convert Original LocalDateTime to Destination ZonedDatetime
     * @param dateTime LocalDateTime of Original Location
     * @param origin Original Location ZoneId
     * @param destination Destination ZoneId
     * @return ZoneDataTime of Destination Location
     */
    public static ZonedDateTime toDestinationTimeZone(final LocalDateTime dateTime, final ZoneId origin,
                                                      final ZoneId destination) {
        return toDestinationTimeZone(dateTime.atZone(origin), destination);
    }

    /**
     * Convert Original LocalDateTime to Destination ZonedDatetime
     * @param dateTime LocalDateTime of Original Location
     * @param originShortZoneId Original Location ShortZoneId
     * @param destinationShortZoneId Destination Location ShortZoneId
     * @return ZoneDataTime of Destination Location
     */
    public static ZonedDateTime toDestinationTimeZone(final LocalDateTime dateTime, ShortZoneId originShortZoneId,
                                                      ShortZoneId destinationShortZoneId) {
        return toDestinationTimeZone(dateTime.atZone(ZoneId.of(ZoneId.SHORT_IDS.get(originShortZoneId.toString()))),
                ZoneId.SHORT_IDS.get(destinationShortZoneId.toString()));
    }

    /**
     * Convert Original LocalDateTime to Destination ZonedDatetime
     * @param dateTime LocalDateTime of Original Location
     * @param origin Original Location ZoneId (String)
     * @param destination Destination ZoneId (String)
     * @return ZoneDataTime of Destination Location
     */
    public static ZonedDateTime toDestinationTimeZone(final LocalDateTime dateTime, final String origin,
                                                      final String destination) {
        return toDestinationTimeZone(dateTime, ZoneId.of(origin), ZoneId.of(destination));
    }


    /**
     * Convert Original DateTime String to Destination ZonedDatetime
     * @param dateTime LocalDateTime of Original Location
     * @param format Datetime Pattern Format (String)
     * @param origin Original Location ZoneId (String)
     * @param destination Destination ZoneId (String)
     * @return ZoneDataTime of Destination Location
     */
    public static ZonedDateTime toDestinationTimeZone(final String dateTime, final String format,
                                                      final String origin, final String destination) {
        LocalDateTime originDatetime = LocalDateTime.parse(dateTime, DateTimeFormatter.ofPattern(format));
        return toDestinationTimeZone(originDatetime, origin, destination);
    }


    /**
     * Convert Original ZonedDatetime to Destination LocalDateTime
     * @param dateTime ZoneDateTime of Original Location
     * @param destination Destination ZoneId (String)
     * @return LocalDateTime of Destination Location
     */
    public static LocalDateTime toDestinationTime(final ZonedDateTime dateTime, final String destination) {
        return toDestinationTimeZone(dateTime, destination).toLocalDateTime();
    }

    /**
     * Convert Original LocalDateTime to Destination LocalDateTime
     * @param dateTime LocalDateTime of Original Location
     * @param origin Original Location ZoneId (String)
     * @param destination Destination ZoneId (String)
     * @return LocalDateTime of Destination Location
     */
    public static LocalDateTime toDestinationTime(final LocalDateTime dateTime, final String origin,
                                                  final String destination) {
        return toDestinationTimeZone(dateTime, origin, destination).toLocalDateTime();
    }


    /**
     * Convert Original LocalDateTime to Destination LocalDateTime
     * @param dateTime LocalDateTime of Original Location
     * @param origin Original Location ZoneId
     * @param destination Destination ZoneId
     * @return LocalDateTime of Destination Location
     */
    public static LocalDateTime toDestinationTime(final LocalDateTime dateTime, final ZoneId origin,
                                                  final ZoneId destination) {
        return toDestinationTimeZone(dateTime, origin, destination).toLocalDateTime();
    }


    /**
     * Convert Original LocalDateTime to Destination LocalDateTime
     * @param dateTime LocalDateTime of Original Location
     * @param originShortZoneId Original Location ShortZoneId
     * @param destinationShortZoneId Destination ShortZoneId
     * @return LocalDateTime of Destination Location
     */
    public static LocalDateTime toDestinationTime(final LocalDateTime dateTime, ShortZoneId originShortZoneId,
                                                  ShortZoneId destinationShortZoneId) {
        return toDestinationTimeZone(dateTime, originShortZoneId, destinationShortZoneId).toLocalDateTime();
    }


    /**
     * Convert Original DateTime String to Destination LocalDateTime
     * @param dateTime Original Location Datetime (String)
     * @param format Datetime Pattern Format (String)
     * @param origin  Original Location ZoneId (String)
     * @param destination Destination ZoneId (String)
     * @return LocalDateTime of Destination Location
     */
    public static LocalDateTime toDestinationTime(final String dateTime, final String format,
                                                  final String origin, final String destination) {
        return toDestinationTimeZone(dateTime, format, origin, destination).toLocalDateTime();
    }


    /**
     * Convert Original DateTime String to Destination DateTime String
     * @param dateTime Original Location Datetime (String)
     * @param format Datetime Pattern Format (String)
     * @param originShortZoneId Original Location ShortZoneId
     * @param destinationShortZoneId Destination ShortZoneId
     * @return Destination Datetime (String)
     */
    public static String toDestinationTimeString(final String dateTime, final String format,
                                                 ShortZoneId originShortZoneId, ShortZoneId destinationShortZoneId) {
        return toDestinationTime(LocalDateTime
                .parse(dateTime, DateTimeFormatter.ofPattern(format)), originShortZoneId, destinationShortZoneId)
                .format(DateTimeFormatter.ofPattern(format));
    }

    /**
     * Convert Original DateTime String to Destination DateTime String
     * @param dateTime Original Location Datetime (String)
     * @param format Datetime Pattern Format (String)
     * @param origin Original Location ZoneId (String)
     * @param destination Destination ZoneId (String)
     * @return Destination Datetime (String)
     */
    public static String toDestinationTimeString(final String dateTime, final String format,
                                                 final String origin, final String destination) {
        return toDestinationTime(dateTime, format, origin, destination).format(DateTimeFormatter.ofPattern(format));
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

    /**
     * Convert Epoch Second Time to Destination LocalDatetime
     * @param epochSecond Epoch Second (Long)
     * @param destination Destination ZoneId
     * @return Destination (LocalDatetime)
     */
    public static LocalDateTime convertFromEpochTime(final Long epochSecond, final ZoneId destination) {
        return LocalDateTime.ofInstant(Instant.ofEpochSecond(epochSecond), destination);
    }

    /**
     * Convert Epoch Second Time to Destination LocalDatetime
     * @param epochSecond Epoch Second (Long)
     * @param destination Destination ZoneId (String)
     * @return Destination (LocalDatetime)
     */
    public static LocalDateTime convertFromEpochTime(final Long epochSecond, final String destination) {
        return convertFromEpochTime(epochSecond, ZoneId.of(destination));
    }

    /**
     * Convert Epoch Second Time to Destination LocalDatetime
     * @param epochSecond Epoch Second (Long)
     * @param destination Destination ZoneId (String)
     * @param format Datetime Pattern Format (String)
     * @return Destination (LocalDatetime)
     */
    public static String convertFromEpochTime(final Long epochSecond, final ZoneId destination, final String format) {
        return convertFromEpochTime(epochSecond, destination).format(DateTimeFormatter.ofPattern(format));
    }


    private static void printTimeZoneInfo() {
        getTypicalTimeZoneIdWithName().forEach((k, v) -> System.out.println(k + " : " + v));
        getAllTimeZoneIdWithOffset().forEach((k, v) -> System.out.println(k + " : " + v));
        System.out.println(getTimeZoneIdByOffset("UTC +08:00"));
    }

    // Test Driver
    public static void main(String[] args) {

        String dateInString1 = "2018-09-30 20:30:40";
        LocalDateTime localDateTime1 = LocalDateTime.now();
        ZonedDateTime zonedDateTime1 = ZonedDateTime.now();

        String zoneIdString0 = "+00:00";
        String zoneIdName0 = "Greenwich";

        String zoneIdString1 = "+08:00";
        String zoneIdName1 = "Asia/Shanghai";

        String zoneIdName2 = "America/Chicago";

        ZonedDateTime destinationTimeZone = toDestinationTimeZone(localDateTime1, zoneIdName2, zoneIdName0);
        System.out.println("-> To Time Zone: " + zoneIdName0 + "  " + destinationTimeZone);

        destinationTimeZone = toDestinationTimeZone(zonedDateTime1, ShortZoneId.PST);
        System.out.println("-> To Time Zone: " + ShortZoneId.PST + "  " + destinationTimeZone);

        String destinationTimeString = toDestinationTimeString(dateInString1, DATE_TIME_FORMAT_WITH_SEC,
                                                                zoneIdName2, zoneIdName1);
        System.out.println("-> To Static Time From Zone [" + zoneIdName2 + "] To Zone [" + zoneIdName1
                            + "] : " + destinationTimeString);

        destinationTimeString = toDestinationTimeString(dateInString1, DATE_TIME_FORMAT_WITH_SEC,
                ShortZoneId.CST, ShortZoneId.PST);
        System.out.println("\n-> Convert Static Time From CST to PST: " + "  " + destinationTimeString);


        Long destinationEpochTime = toEpochTime(localDateTime1, ZoneId.of(zoneIdName2));
        System.out.println("\n-> To Epoch Time for Zone: " + zoneIdName2 + "  " + destinationEpochTime);


        LocalDateTime destinationTime = convertFromEpochTime(System.currentTimeMillis()/1000,
                                                            ZoneId.of(zoneIdName2));
        System.out.println("Convert Current Epoch Second to [" + zoneIdName2 + "] : " + destinationTime);


        String timeString = convertFromEpochTime(System.currentTimeMillis()/1000,
                                                ZoneId.of(zoneIdName1), DATE_TIME_FORMAT_WITH_SEC);
        System.out.println("Convert Current Epoch Second to [" + zoneIdName1 + "] : " + timeString);
    }


}
