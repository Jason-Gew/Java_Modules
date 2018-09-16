package gew.caching.util;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * Time Interval Helper, Conversion Between Days, Hours, Minutes, Seconds and Milliseconds.
 * Provide User Friendly Method to Input Time Unit as Below:
 * (days/day/d, hours/hour/h, minutes/minute/min/m, seconds/second/sec/s)
 * This helper class will be under continuous development.
 *
 * @author Jason/GeW
 * @since 2018-09-14
 */
public class TimeIntervalHelper {

    private static final String DIGIT_CHARACTER_SPLIT = "(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)";

    private TimeIntervalHelper() {

    }

    public static Long convertDaysToMilliSeconds(long days) {
        Duration duration = Duration.ofDays(days);
        return duration.getSeconds() * 1000;
    }

    public static Long convertHoursToMilliSeconds(long hours) {
        Duration duration = Duration.ofHours(hours);
        return duration.getSeconds() * 1000;
    }

    public static Long convertMinutesToMilliSeconds(long minutes) {
        Duration duration = Duration.ofMinutes(minutes);
        return duration.getSeconds() * 1000;
    }

    public static long convertSecondsToMilliSeconds(long seconds) {
        return seconds * 1000;
    }


    public static long convertMilliSecondsToSeconds(long milliseconds) {
        return milliseconds / 1000;
    }


    public static long convertTimeToMilliSeconds(int hours, int minutes, int seconds) {
        return convertHoursToMilliSeconds(hours)
                + convertMinutesToMilliSeconds(minutes)
                + convertSecondsToMilliSeconds(seconds);
    }

    public static long convertTimeToMilliSeconds(int days, int hours, int minutes, int seconds) {
        return convertDaysToMilliSeconds(days)
                + convertHoursToMilliSeconds(hours)
                + convertMinutesToMilliSeconds(minutes)
                + convertSecondsToMilliSeconds(seconds);
    }

    public static Long convertTimeToMilliSeconds(long time, final String unit) {
        if (unit == null || unit.isEmpty()) {
            throw new IllegalArgumentException("Invalid Time Unit");
        } else {
            switch (unit.toLowerCase().trim()) {
                case "days":
                case "day":
                case "d":
                    return convertDaysToMilliSeconds(time);
                case "hours":
                case "hour":
                case "h":
                    return convertHoursToMilliSeconds(time);
                case "minutes":
                case "minute":
                case "min":
                case "m":
                    return convertMinutesToMilliSeconds(time);
                case "seconds":
                case "second":
                case "sec":
                case "s":
                    return convertSecondsToMilliSeconds(time);
                default:
                    throw new IllegalArgumentException("Unidentified Time Unit: " + unit);
            }
        }
    }

    public static Long convertTimeToMilliSeconds(long time, final TimeUnit unit) {
        Long result;
        if (unit == null) {
            throw new IllegalArgumentException("Invalid Time Unit");
        }
        switch (unit) {
            case DAYS:
                result = convertDaysToMilliSeconds(time);
                break;
            case HOURS:
                result = convertHoursToMilliSeconds(time);
                break;
            case MINUTES:
                result = convertMinutesToMilliSeconds(time);
                break;
            case SECONDS:
                result = convertSecondsToMilliSeconds(time);
                break;
            default:
                throw new IllegalArgumentException("Unsupported Time Unit");
        }
        return result;
    }

    public static Long convertTimeToMilliSeconds(final String timeWithUnit) {
        if (timeWithUnit == null || timeWithUnit.isEmpty()) {
            throw new IllegalArgumentException("Invalid TimeWithUnit Input");
        }
        String[] components = timeWithUnit.split(DIGIT_CHARACTER_SPLIT);
        if (components.length < 2) {
            throw new IllegalArgumentException("Missing Time Unit Field");
        } else {
            return convertTimeToMilliSeconds(Long.parseLong(components[0].trim()), components[1].trim());
        }
    }

    public static TimeUnit convertTimeUnitFromString(final String unit) {
        if (unit == null) {
            throw new IllegalArgumentException("Invalid Time Unit");
        }
        switch (unit.toLowerCase().trim()) {
            case "days":
            case "day":
            case "d":
                return TimeUnit.DAYS;
            case "hours":
            case "hour":
            case "h":
                return TimeUnit.HOURS;
            case "minutes":
            case "minute":
            case "min":
            case "m":
                return TimeUnit.MINUTES;
            case "seconds":
            case "second":
            case "sec":
            case "s":
                return TimeUnit.SECONDS;
            case "milliseconds":
            case "millisecond":
            case "ms":
                return TimeUnit.MILLISECONDS;
            default:
                throw new IllegalArgumentException("Unidentified Time Unit: " + unit);
        }
    }

    public static Duration convertMilliSecondsToDuration(long milliseconds) {
        return Duration.ofMillis(milliseconds);
    }
}
