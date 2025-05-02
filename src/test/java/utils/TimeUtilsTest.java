package utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.chrono.ChronoZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * A JUnit test class for testing the TimeUtils class.
 */
public class TimeUtilsTest {

  @Test
  public void testGetDaysOfWeek() {
    char[] days = new char[]{'M', 'T', 'F', 'S'};
    Set<DayOfWeek> expected = Set.of(
        DayOfWeek.MONDAY,
        DayOfWeek.TUESDAY,
        DayOfWeek.FRIDAY,
        DayOfWeek.SATURDAY
    );
    assertEquals(expected, TimeUtils.getDaysOfWeek(days));
  }

  @Test
  public void testGetDaysOfWeekDuplicates() {
    char[] days = new char[]{'M', 'M', 'F', 'S', 'T'};
    Set<DayOfWeek> expected = Set.of(
        DayOfWeek.MONDAY,
        DayOfWeek.TUESDAY,
        DayOfWeek.FRIDAY,
        DayOfWeek.SATURDAY
    );
    assertEquals(expected, TimeUtils.getDaysOfWeek(days));
  }

  @Test
  public void testGetDaysOfWeekLowerCase() {
    char[] days = new char[]{'m', 't', 'f', 's'};
    Set<DayOfWeek> expected = Set.of(
        DayOfWeek.MONDAY,
        DayOfWeek.TUESDAY,
        DayOfWeek.FRIDAY,
        DayOfWeek.SATURDAY
    );
    assertEquals(expected, TimeUtils.getDaysOfWeek(days));
  }

  @Test
  public void testGetDaysOfWeekMixedCase() {
    char[] days = new char[]{'M', 't', 'F', 's'};
    Set<DayOfWeek> expected = Set.of(
        DayOfWeek.MONDAY,
        DayOfWeek.TUESDAY,
        DayOfWeek.FRIDAY,
        DayOfWeek.SATURDAY
    );
    assertEquals(expected, TimeUtils.getDaysOfWeek(days));
  }

  @Test
  public void testParseValidDateTimeString() {
    String dateTimeString = "2020-01-01T01:00";
    ZoneId zoneId = ZoneId.of("US/Eastern");
    ChronoZonedDateTime<LocalDate> zonedDateTime =
        TimeUtils.parseDateTimeString(dateTimeString, zoneId);

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
    LocalDateTime dateTime = LocalDateTime.parse(dateTimeString, formatter);
    ChronoZonedDateTime<LocalDate> expected = dateTime.atZone(zoneId);

    assertEquals(expected, zonedDateTime);
  }

  @Test(expected = DateTimeParseException.class)
  public void testParseInvalidDateTimeString() {
    String dateTimeString = "2020-01-01T25:00";
    TimeUtils.parseDateTimeString(dateTimeString, ZoneId.of("Europe/Berlin"));
  }

  @Test
  public void testParseDateTimeStringNullValue() {
    assertNull(TimeUtils.parseDateTimeString(null, ZoneId.systemDefault()));
  }

  @Test
  public void testParseDateTimeStringEmptyValue() {
    ChronoZonedDateTime<LocalDate> dateTime = TimeUtils.parseDateTimeString("",
        ZoneId.systemDefault());
    assertNull(dateTime);
  }

  @Test
  public void testGetAllTimezones() {
    List<String> zones = TimeUtils.getAllTimezones();
    assertNotNull(zones);
    assertFalse(zones.isEmpty());
  }

  @Test
  public void testAsDate() {
    Date date = TimeUtils.asDate(ZonedDateTime.now());
    assertNotNull(date);
  }

  @Test
  public void testFormatNotNull() {
    String dateString = TimeUtils.format(ZonedDateTime.now(), "yyyy-MM-dd'T'HH:mm");
    assertNotNull(dateString);
  }

  @Test
  public void testFormatCorrectValue() {
    String dateTimeString = "2020-01-01T23:00";
    ChronoZonedDateTime<LocalDate> dateTime =
        TimeUtils.parseDateTimeString(dateTimeString, ZoneId.of("UTC"));

    String result = TimeUtils.format(dateTime, "yyyy-MM-dd'T'HH:mm");
    assertEquals(dateTimeString, result);
  }

  @Test
  public void testParseDateTimeStringWithPattern() {
    ChronoZonedDateTime<LocalDate> dateTime = TimeUtils.parseDateTimeStringWithPattern(
        "2020-01-01T23:00", ZoneId.systemDefault(), "yyyy-MM-dd'T'HH:mm");
    assertNotNull(dateTime);
  }

  @Test
  public void testParseDateTimeStringWithPatternMissing() {
    ChronoZonedDateTime<LocalDate> dateTime = TimeUtils.parseDateTimeStringWithPattern(
        "2020-01-01T23:00", ZoneId.systemDefault(), "");
    assertNull(dateTime);
  }
}