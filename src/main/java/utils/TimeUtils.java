package utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.chrono.ChronoZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAccessor;
import java.time.zone.ZoneRulesException;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A Java class containing util methods for date and time based operations.
 */
public class TimeUtils {

  private static final Map<Character, DayOfWeek> charToDayMap = new HashMap<>();

  static {
    charToDayMap.put('M', DayOfWeek.MONDAY);
    charToDayMap.put('T', DayOfWeek.TUESDAY);
    charToDayMap.put('W', DayOfWeek.WEDNESDAY);
    charToDayMap.put('R', DayOfWeek.THURSDAY);
    charToDayMap.put('F', DayOfWeek.FRIDAY);
    charToDayMap.put('S', DayOfWeek.SATURDAY);
    charToDayMap.put('U', DayOfWeek.SUNDAY);
  }

  /**
   * Converts a character array representing days into a set of DayOfWeek enums. Any duplicate
   * characters in the character are ignored.
   *
   * @param days character array
   * @return set of DayOfWeek enums
   */
  public static Set<DayOfWeek> getDaysOfWeek(char[] days) {
    Set<DayOfWeek> daysOfWeek = new HashSet<>();
    for (char day : days) {
      daysOfWeek.add(charToDayMap.get(Character.toUpperCase(day)));
    }

    return daysOfWeek;
  }

  /**
   * Parses the given dateTimeString to create a ZonedDateTime object.
   *
   * @param dateTimeString string in "yyyy-MM-dd'T'HH:mm" format
   * @param zone           ZoneId object representing a timezone
   * @return ChronoZonedDateTime object representing the given date and time
   * @throws DateTimeParseException if the date string is invalid
   */
  public static ChronoZonedDateTime<LocalDate> parseDateTimeString(String dateTimeString,
      ZoneId zone) throws DateTimeParseException {
    if (dateTimeString == null || dateTimeString.isEmpty() || zone == null) {
      return null;
    }

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
    LocalDateTime dateTime = LocalDateTime.parse(dateTimeString, formatter);

    return dateTime.atZone(zone);
  }

  /**
   * Parses the given dateTimeString to create a ZonedDateTime object with the given pattern.
   *
   * @param dateTimeString string in "yyyy-MM-dd'T'HH:mm" format
   * @param zone           ZoneId object representing a timezone
   * @param pattern        pattern string
   * @return ChronoZonedDateTime object representing the given date and time
   * @throws DateTimeParseException if the date string is invalid
   */
  public static ChronoZonedDateTime<LocalDate> parseDateTimeStringWithPattern(String dateTimeString,
      ZoneId zone, String pattern) throws DateTimeParseException {
    if (dateTimeString == null || dateTimeString.isEmpty() || zone == null
        || pattern == null || pattern.isEmpty()) {
      return null;
    }

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
    LocalDateTime dateTime = LocalDateTime.parse(dateTimeString, formatter);

    return dateTime.atZone(zone);
  }

  /**
   * Parses the given dateString to create a ZonedDateTime object with the time set to the start of
   * the day. Uses the US/Eastern timezone.
   *
   * @param dateString string in "yyyy-MM-dd" format
   * @param zone       ZoneId object representing a timezone
   * @return ChronoZonedDateTime object representing the given date
   * @throws DateTimeParseException if the date string is invalid
   */
  public static ChronoZonedDateTime<LocalDate> parseDateString(String dateString, ZoneId zone)
      throws DateTimeParseException {
    if (dateString == null || dateString.isEmpty() || zone == null) {
      return null;
    }

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    LocalDate date = LocalDate.parse(dateString, formatter);

    return date.atStartOfDay(zone);
  }

  /**
   * Formats a ChronoZonedDateTime object into a string.
   *
   * @param dateTime ChronoZonedDateTime object
   * @param pattern  formatter pattern
   * @return dateTime as a string
   */
  public static String format(TemporalAccessor dateTime, String pattern) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
    return formatter.format(dateTime);
  }

  /**
   * Parses the given dateTimeString to create a ZonedDateTime object with the time set to the start
   * of the day.
   *
   * @param dateTimeString string in "yyyy-MM-dd'T'HH:mm" format
   * @param zone           ZoneId object representing a timezone
   * @return ChronoZonedDateTime object representing the given date and time
   * @throws DateTimeParseException if the date string is invalid
   */
  public static ChronoZonedDateTime<LocalDate> parseDateTimeAtStartOfDay(String dateTimeString,
      ZoneId zone) throws DateTimeParseException {
    return parseDateTimeString(dateTimeString, zone).toLocalDate().atStartOfDay(zone);
  }

  /**
   * Check if the given ChronoZonedDateTime object is at midnight.
   *
   * @param dateTime ChronoZonedDateTime object
   * @param zone     ZoneId object representing a timezone
   * @return true if it is at midnight, false otherwise
   */
  public static boolean isMidnight(ChronoZonedDateTime<LocalDate> dateTime, ZoneId zone) {
    ChronoZonedDateTime<LocalDate> midnight = dateTime.toLocalDate().atStartOfDay(zone);
    return midnight.isEqual(dateTime);
  }

  /**
   * Calculates the difference in minutes between two ChronoZonedDateTime objects. If dateTime2 is
   * after dateTime1, then the difference is positive, otherwise the difference is negative.
   *
   * @param dateTime1 ChronoZonedDateTime object
   * @param dateTime2 ChronoZonedDateTime object
   * @return difference in minutes as a long
   */
  public static long getDifferenceInMinutes(ChronoZonedDateTime<LocalDate> dateTime1,
      ChronoZonedDateTime<LocalDate> dateTime2) {
    return ChronoUnit.MINUTES.between(dateTime1, dateTime2);
  }

  /**
   * Calculates the difference in days between two ChronoZonedDateTime objects. If dateTime2 is
   * after dateTime1, then the difference is positive, otherwise the difference is negative.
   * Converts the difference to minutes before returning.
   *
   * @param dateTime1 ChronoZonedDateTime object
   * @param dateTime2 ChronoZonedDateTime object
   * @return difference in days as a long
   */
  public static long getDifferenceInDays(ChronoZonedDateTime<LocalDate> dateTime1,
      ChronoZonedDateTime<LocalDate> dateTime2) {
    long difference = ChronoUnit.DAYS.between(dateTime1, dateTime2);
    // get value in minutes
    return difference * 24 * 60;
  }

  /**
   * Get the ZoneId that corresponds to the given string.
   *
   * @param timeZone string representing a time zone id
   * @return ZoneId object
   * @throws ZoneRulesException if the given string does not match a ZoneId
   */
  public static ZoneId getZoneId(String timeZone) throws ZoneRulesException {
    return ZoneId.of(timeZone);
  }

  /**
   * Get all timezones available in the ZoneId class.
   *
   * @return list of zone ID strings
   */
  public static List<String> getAllTimezones() {
    return new ArrayList<>(ZoneId.getAvailableZoneIds());
  }

  /**
   * Convert a ChronoZonedDateTime object to a Date object.
   *
   * @param dateTime ChronoZonedDateTime object
   * @return Date object
   */
  public static Date asDate(ChronoZonedDateTime<LocalDate> dateTime) {
    String pattern = "yyyy-MM-dd'T'HH:mm";
    String dateTimeString = format(dateTime, pattern);
    DateFormat formatter = new SimpleDateFormat(pattern);
    try {
      return formatter.parse(dateTimeString);
    } catch (ParseException e) {
      return null;
    }
  }

  /**
   * Get a ChronoZonedDateTime object at the start of the day from a Date object.
   *
   * @param date Date object
   * @param zone timezone as ZoneId
   * @return ChronoZonedDateTime object
   */
  public static ChronoZonedDateTime<LocalDate> parseDateAtStartOfDay(Date date, ZoneId zone) {
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
    String dateTimeString = formatter.format(date);
    return parseDateTimeAtStartOfDay(dateTimeString, zone);
  }

  /**
   * Retrieves the maximum value represented by a ChronoZonedDateTime object.
   *
   * @return ChronoZonedDateTime object containing maximum value
   */
  public static ChronoZonedDateTime<LocalDate> getMaximumTime() {
    return ZonedDateTime.parse("9999-12-30T00:00:00.000000-04:00[America/New_York]");
  }

}