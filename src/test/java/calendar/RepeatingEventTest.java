package calendar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertFalse;

import org.junit.BeforeClass;
import org.junit.Test;

import java.time.DayOfWeek;
import java.util.Set;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.chrono.ChronoZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import utils.TimeUtils;

/**
 * A JUnit test class for testing the RepeatingEvent class.
 */
public class RepeatingEventTest {

  private static ZoneId ZONE_ID;

  @BeforeClass
  public static void setUpBeforeClass() {
    ZONE_ID = ZoneId.of("US/Eastern");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEventBuilderWithNoArguments() {
    new RepeatingEvent.RepeatingEventBuilder().build();
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEventBuilderWithEmptySubject() {
    new RepeatingEvent.RepeatingEventBuilder()
        .subject("")
        .build();
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEventBuilderWithNullStartDateTime() {
    new RepeatingEvent.RepeatingEventBuilder()
        .subject("1")
        .startDateTime(null)
        .build();
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEventBuilderWithNullRepeatEndDateTimeAndNoRepeatNumber() {
    ChronoZonedDateTime<LocalDate> startDateTime =
        TimeUtils.parseDateTimeString("2020-01-01T08:00", ZONE_ID);
    new RepeatingEvent.RepeatingEventBuilder()
        .subject("1")
        .startDateTime(startDateTime)
        .endDateTime(startDateTime.plus(1, ChronoUnit.HOURS))
        .repeatDays(TimeUtils.getDaysOfWeek(new char[]{'M', 'W', 'F'}))
        .build();
  }

  @Test
  public void testEventBuilderWithBasicArguments() {
    ChronoZonedDateTime<LocalDate> startDateTime =
        TimeUtils.parseDateTimeString("2020-01-01T08:00", ZONE_ID);

    IRepeatingEvent event = new RepeatingEvent.RepeatingEventBuilder()
        .subject("test")
        .startDateTime(startDateTime)
        .endDateTime(startDateTime.plus(1, ChronoUnit.HOURS))
        .repeatDays(TimeUtils.getDaysOfWeek(new char[]{'M', 'W', 'F'}))
        .repeatEndDateTime(startDateTime.plus(1, ChronoUnit.WEEKS))
        .build();
    assertNotNull(event);
    assertEquals("test", event.getSubject());
    assertEquals(startDateTime, event.getStartDateTime());
    assertEquals(Set.of(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY),
        event.getRepeatDays());
    assertEquals(startDateTime.plus(1, ChronoUnit.HOURS), event.getEndDateTime());
    assertEquals(startDateTime.plus(1, ChronoUnit.WEEKS),
        event.getRepeatEndDateTime());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEventBuilderWithMultipleDayEvent() {
    ChronoZonedDateTime<LocalDate> startDateTime =
        TimeUtils.parseDateTimeString("2020-01-01T08:00", ZONE_ID);
    new RepeatingEvent.RepeatingEventBuilder()
        .subject("test")
        .startDateTime(startDateTime)
        .endDateTime(startDateTime.plus(1, ChronoUnit.DAYS))
        .repeatNumber(2)
        .repeatDays(TimeUtils.getDaysOfWeek(new char[]{'M', 'W', 'F'}))
        .build();
  }

  @Test
  public void testEventBuilderWithAllArguments() {
    ChronoZonedDateTime<LocalDate> startDateTime =
        TimeUtils.parseDateTimeString("2020-01-01T08:00", ZONE_ID);
    RepeatingEvent event = new RepeatingEvent.RepeatingEventBuilder()
        .subject("test")
        .startDateTime(startDateTime)
        .endDateTime(startDateTime.plus(1, ChronoUnit.HOURS))
        .description("test description")
        .isPrivate(true)
        .location("location")
        .repeatNumber(2)
        .repeatDays(TimeUtils.getDaysOfWeek(new char[]{'M', 'W', 'F'}))
        .build();

    assertNotNull(event);
    assertEquals("test", event.getSubject());

  }

  @Test(expected = IllegalArgumentException.class)
  public void testEventBuilderWithInvalidRepeatEndDateTime() throws IllegalArgumentException {
    ChronoZonedDateTime<LocalDate> startDateTime =
        TimeUtils.parseDateTimeString("2020-01-01T08:00", ZONE_ID);
    new RepeatingEvent.RepeatingEventBuilder()
        .subject("test")
        .startDateTime(startDateTime)
        .endDateTime(startDateTime.plus(1, ChronoUnit.HOURS))
        .description("test description")
        .isPrivate(true)
        .location("location")
        .repeatEndDateTime(startDateTime.minus(1, ChronoUnit.DAYS))
        .repeatDays(TimeUtils.getDaysOfWeek(new char[]{'M', 'W', 'F'}))
        .build();
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEventBuilderWithSameStartAndEndDateTime() {
    ChronoZonedDateTime<LocalDate> startDateTime =
        TimeUtils.parseDateTimeString("2020-01-01T08:00", ZONE_ID);
    new RepeatingEvent.RepeatingEventBuilder()
        .subject("test")
        .startDateTime(startDateTime)
        .endDateTime(startDateTime)
        .description("test description")
        .isPrivate(true)
        .location("location")
        .repeatNumber(2)
        .repeatDays(TimeUtils.getDaysOfWeek(new char[]{'M', 'W', 'F'}))
        .build();
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEventBuilderWithMissingRepeatNumber() {
    ChronoZonedDateTime<LocalDate> startDateTime =
        TimeUtils.parseDateTimeString("2020-01-01T08:00", ZONE_ID);
    new RepeatingEvent.RepeatingEventBuilder()
        .subject("test")
        .startDateTime(startDateTime)
        .endDateTime(startDateTime.plus(1, ChronoUnit.HOURS))
        .description("test description")
        .isPrivate(true)
        .location("location")
        .repeatDays(TimeUtils.getDaysOfWeek(new char[]{'M', 'W', 'F'}))
        .build();
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEventBuilderWithMissingRepeatEndDate() {
    ChronoZonedDateTime<LocalDate> startDateTime =
        TimeUtils.parseDateTimeString("2020-01-01T08:00", ZONE_ID);
    new RepeatingEvent.RepeatingEventBuilder()
        .subject("test")
        .startDateTime(startDateTime)
        .endDateTime(startDateTime.plus(1, ChronoUnit.HOURS))
        .description("test description")
        .isPrivate(true)
        .location("location")
        .repeatDays(TimeUtils.getDaysOfWeek(new char[]{'M', 'W', 'F'}))
        .build();
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEventBuilderWithInvalidRepeatNumber() {
    ChronoZonedDateTime<LocalDate> startDateTime =
        TimeUtils.parseDateTimeString("2020-01-01T08:00", ZONE_ID);
    new RepeatingEvent.RepeatingEventBuilder()
        .subject("test")
        .startDateTime(startDateTime)
        .endDateTime(startDateTime.plus(1, ChronoUnit.HOURS))
        .description("test description")
        .isPrivate(true)
        .location("location")
        .repeatNumber(-1)
        .repeatDays(TimeUtils.getDaysOfWeek(new char[]{'M', 'W', 'F'}))
        .build();
  }

  @Test(expected = IllegalArgumentException.class)
  public void testSetNewPropertyInvalidRepeatNumber() throws IllegalArgumentException {
    ChronoZonedDateTime<LocalDate> startDateTime =
        TimeUtils.parseDateTimeString("2020-01-01T08:00", ZONE_ID);
    RepeatingEvent event = new RepeatingEvent.RepeatingEventBuilder()
        .subject("test")
        .startDateTime(startDateTime)
        .endDateTime(startDateTime.plus(1, ChronoUnit.HOURS))
        .description("test description")
        .isPrivate(true)
        .location("location")
        .repeatNumber(2)
        .repeatDays(TimeUtils.getDaysOfWeek(new char[]{'M', 'W', 'F'}))
        .build();

    event.setNewProperty("repeatNumber", "-1");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testSetNewPropertyZeroRepeatNumber() throws IllegalArgumentException {
    ChronoZonedDateTime<LocalDate> startDateTime =
        TimeUtils.parseDateTimeString("2020-01-01T08:00", ZONE_ID);
    RepeatingEvent event = new RepeatingEvent.RepeatingEventBuilder()
        .subject("test")
        .startDateTime(startDateTime)
        .endDateTime(startDateTime.plus(1, ChronoUnit.HOURS))
        .description("test description")
        .isPrivate(true)
        .location("location")
        .repeatNumber(2)
        .repeatDays(TimeUtils.getDaysOfWeek(new char[]{'M', 'W', 'F'}))
        .build();

    event.setNewProperty("repeatNumber", "0");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testSetNewPropertyNonNumericalRepeatNumber() throws IllegalArgumentException {
    ChronoZonedDateTime<LocalDate> startDateTime =
        TimeUtils.parseDateTimeString("2020-01-01T08:00", ZONE_ID);
    IRepeatingEvent event = new RepeatingEvent.RepeatingEventBuilder()
        .subject("test")
        .startDateTime(startDateTime)
        .endDateTime(startDateTime.plus(1, ChronoUnit.HOURS))
        .description("test description")
        .isPrivate(true)
        .location("location")
        .repeatNumber(2)
        .repeatDays(TimeUtils.getDaysOfWeek(new char[]{'M', 'W', 'F'}))
        .build();

    event.setNewProperty("repeatNumber", "word");
  }

  @Test
  public void testSetNewPropertyRepeatNumber() {
    ChronoZonedDateTime<LocalDate> startDateTime =
        TimeUtils.parseDateTimeString("2020-01-01T08:00", ZONE_ID);
    RepeatingEvent event = new RepeatingEvent.RepeatingEventBuilder()
        .subject("test")
        .startDateTime(startDateTime)
        .endDateTime(startDateTime.plus(1, ChronoUnit.HOURS))
        .description("test description")
        .isPrivate(true)
        .location("location")
        .repeatNumber(2)
        .repeatDays(TimeUtils.getDaysOfWeek(new char[]{'M', 'W', 'F'}))
        .build();

    event.setNewProperty("repeatNumber", "3");

    String expected = "subject: test, startDateTime: 2020-01-01T08:00-05:00[US/Eastern],"
        + " endDateTime: 2020-01-01T09:00-05:00[US/Eastern], description: test description, "
        + "location: location, isAllDay: false, isPrivate: true, repeatNumber: 3, "
        + "repeatEndDateTime: 9999-12-29T23:00-05:00[America/New_York]";
    assertEquals(expected, event.toString());
  }

  @Test
  public void testSetNewPropertyRepeatDays() {
    ChronoZonedDateTime<LocalDate> startDateTime =
        TimeUtils.parseDateTimeString("2020-01-01T08:00", ZONE_ID);
    RepeatingEvent event = new RepeatingEvent.RepeatingEventBuilder()
        .subject("test")
        .startDateTime(startDateTime)
        .endDateTime(startDateTime.plus(1, ChronoUnit.HOURS))
        .description("test description")
        .isPrivate(true)
        .location("location")
        .repeatNumber(2)
        .repeatDays(TimeUtils.getDaysOfWeek(new char[]{'M', 'W', 'F'}))
        .build();

    event.setNewProperty("repeatDays", "MW");

    String expected = "subject: test, startDateTime: 2020-01-01T08:00-05:00[US/Eastern],"
        + " endDateTime: 2020-01-01T09:00-05:00[US/Eastern], description: test description, "
        + "location: location, isAllDay: false, isPrivate: true, repeatNumber: 2,"
        + " repeatEndDateTime: 9999-12-29T23:00-05:00[America/New_York]";
    assertEquals(expected, event.toString());
  }

  @Test
  public void testSetNewPropertyRepeatDate() {
    ChronoZonedDateTime<LocalDate> startDateTime =
        TimeUtils.parseDateTimeString("2020-01-01T08:00", ZONE_ID);
    RepeatingEvent event = new RepeatingEvent.RepeatingEventBuilder()
        .subject("test")
        .startDateTime(startDateTime)
        .endDateTime(startDateTime.plus(1, ChronoUnit.HOURS))
        .description("test description")
        .isPrivate(true)
        .location("location")
        .repeatEndDateTime(startDateTime.plus(1, ChronoUnit.WEEKS))
        .repeatDays(TimeUtils.getDaysOfWeek(new char[]{'M', 'W', 'F'}))
        .build();

    event.setNewProperty("repeatEndDateTime", "2020-02-01T08:00");

    String expected = "subject: test, startDateTime: 2020-01-01T08:00-05:00[US/Eastern], "
        + "endDateTime: 2020-01-01T09:00-05:00[US/Eastern], description: test description, "
        + "location: location, isAllDay: false, isPrivate: true, repeatNumber: 0, "
        + "repeatEndDateTime: 2020-02-01T08:00-05:00[US/Eastern]";
    assertEquals(expected, event.toString());
  }

  @Test
  public void testEditRepeatRepeatDays() {
    ChronoZonedDateTime<LocalDate> startDateTime =
        TimeUtils.parseDateTimeString("2020-01-01T08:00", ZONE_ID);
    IRepeatingEvent event = new RepeatingEvent.RepeatingEventBuilder()
        .subject("test")
        .startDateTime(startDateTime)
        .endDateTime(startDateTime.plus(1, ChronoUnit.HOURS))
        .description("test description")
        .isPrivate(true)
        .location("location")
        .repeatEndDateTime(startDateTime.plus(1, ChronoUnit.WEEKS))
        .repeatDays(TimeUtils.getDaysOfWeek(new char[]{'M', 'W', 'F'}))
        .build();

    List<IEvent> events = event.editRepeat("repeatDays");
    assertEquals(4, events.size());
  }

  @Test(expected = IllegalStateException.class)
  public void testEditRepeatInvalidProperty() throws IllegalStateException {
    ChronoZonedDateTime<LocalDate> startDateTime =
        TimeUtils.parseDateTimeString("2020-01-01T08:00", ZONE_ID);
    RepeatingEvent event = new RepeatingEvent.RepeatingEventBuilder()
        .subject("test")
        .startDateTime(startDateTime)
        .endDateTime(startDateTime.plus(1, ChronoUnit.HOURS))
        .description("test description")
        .isPrivate(true)
        .location("location")
        .repeatEndDateTime(startDateTime.plus(1, ChronoUnit.WEEKS))
        .repeatDays(TimeUtils.getDaysOfWeek(new char[]{'M', 'W', 'F'}))
        .build();

    event.editRepeat("invalid");
  }

  @Test
  public void testEditRepeatRepeatEndDate() {
    ChronoZonedDateTime<LocalDate> startDateTime =
        TimeUtils.parseDateTimeString("2020-01-01T08:00", ZONE_ID);
    IRepeatingEvent event = new RepeatingEvent.RepeatingEventBuilder()
        .subject("test")
        .startDateTime(startDateTime)
        .endDateTime(startDateTime.plus(1, ChronoUnit.HOURS))
        .description("test description")
        .isPrivate(true)
        .location("location")
        .repeatEndDateTime(startDateTime.plus(1, ChronoUnit.WEEKS))
        .repeatDays(TimeUtils.getDaysOfWeek(new char[]{'M', 'W', 'F'}))
        .build();

    List<IEvent> events = event.editRepeat("repeatEndDateTime");
    assertEquals(4, events.size());
  }

  @Test
  public void testCreateNRepeatingEvents() {
    ChronoZonedDateTime<LocalDate> date =
        TimeUtils.parseDateTimeString("2025-01-01T08:00", ZONE_ID);
    IRepeatingEvent event = new RepeatingEvent.RepeatingEventBuilder()
        .subject("test")
        .startDateTime(date)
        .endDateTime(date.plus(1, ChronoUnit.HOURS))
        .description("test description")
        .isPrivate(true)
        .location("location")
        .repeatNumber(3)
        .repeatDays(TimeUtils.getDaysOfWeek(new char[]{'W'}))
        .build();

    List<IEvent> events = event.repeatNTimes();
    assertEquals(4, events.size());
    assertEquals(3, event.getRepeatNumber());
  }

  @Test
  public void testCreateRepeatingEventUntilEndDateTime() {
    ChronoZonedDateTime<LocalDate> date =
        TimeUtils.parseDateTimeString("2025-01-01T08:00", ZONE_ID);
    IRepeatingEvent event = new RepeatingEvent.RepeatingEventBuilder()
        .subject("test")
        .startDateTime(date)
        .endDateTime(date.plus(1, ChronoUnit.HOURS))
        .description("test description")
        .isPrivate(true)
        .location("location")
        .repeatEndDateTime(date.plus(2, ChronoUnit.WEEKS))
        .repeatDays(TimeUtils.getDaysOfWeek(new char[]{'W', 'F'}))
        .build();

    List<IEvent> events = event.repeatUntilEndDate();
    assertEquals(5, events.size());
  }

  @Test
  public void testRepeatUntilEndDate() {
    ChronoZonedDateTime<LocalDate> startDateTime =
        TimeUtils.parseDateTimeString("2025-04-01T10:00", ZONE_ID);
    ChronoZonedDateTime<LocalDate> endDateTime =
        TimeUtils.parseDateTimeString("2025-04-01T11:00", ZONE_ID);
    ChronoZonedDateTime<LocalDate> repeatEndDateTime =
        TimeUtils.parseDateTimeString("2025-04-30T11:00", ZONE_ID);

    IRepeatingEvent event = new RepeatingEvent.RepeatingEventBuilder()
        .subject("Meeting")
        .startDateTime(startDateTime)
        .endDateTime(endDateTime)
        .description("Weekly Meeting")
        .location("Conference Room")
        .isPrivate(false)
        .isAllDay(false)
        .repeatNumber(4)
        .repeatDays(Set.of(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY))
        .repeatEndDateTime(repeatEndDateTime)
        .previous(null)
        .build();

    List<IEvent> repeatedEvents = event.repeatUntilEndDate();
    assertFalse(repeatedEvents.isEmpty());
  }

  @Test
  public void testRepeatNTimes() {
    ChronoZonedDateTime<LocalDate> startDateTime =
        TimeUtils.parseDateTimeString("2025-04-01T10:00", ZONE_ID);
    ChronoZonedDateTime<LocalDate> endDateTime =
        TimeUtils.parseDateTimeString("2025-04-01T11:00", ZONE_ID);
    ChronoZonedDateTime<LocalDate> repeatEndDateTime =
        TimeUtils.parseDateTimeString("2025-04-30T11:00", ZONE_ID);

    IRepeatingEvent event = new RepeatingEvent.RepeatingEventBuilder()
        .subject("Meeting")
        .startDateTime(startDateTime)
        .endDateTime(endDateTime)
        .description("Weekly Meeting")
        .location("Conference Room")
        .isPrivate(false)
        .isAllDay(false)
        .repeatNumber(4)
        .repeatDays(Set.of(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY))
        .repeatEndDateTime(repeatEndDateTime)
        .previous(null)
        .build();

    List<IEvent> repeatedEvents = event.repeatNTimes();
    assertEquals(5, repeatedEvents.size());
  }

  @Test
  public void testEditRepeat() {
    ChronoZonedDateTime<LocalDate> startDateTime =
        TimeUtils.parseDateTimeString("2025-04-01T10:00", ZONE_ID);
    ChronoZonedDateTime<LocalDate> endDateTime =
        TimeUtils.parseDateTimeString("2025-04-01T11:00", ZONE_ID);

    IRepeatingEvent event = new RepeatingEvent.RepeatingEventBuilder()
        .subject("Meeting")
        .startDateTime(startDateTime)
        .endDateTime(endDateTime)
        .description("Weekly Meeting")
        .location("Conference Room")
        .isPrivate(false)
        .isAllDay(false)
        .repeatNumber(4)
        .repeatDays(Set.of(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY))
        .previous(null)
        .build();

    List<IEvent> updatedEvents = event.editRepeat("repeatNumber");
    assertFalse(updatedEvents.isEmpty());
  }

  @Test
  public void testCopyNoDifferenceSameTimeZone() {
    ChronoZonedDateTime<LocalDate> startDateTime =
        TimeUtils.parseDateTimeString("2025-04-01T10:00", ZONE_ID);
    IRepeatingEvent event = new RepeatingEvent.RepeatingEventBuilder()
        .subject("Meeting")
        .startDateTime(startDateTime)
        .endDateTime(startDateTime.plus(1, ChronoUnit.HOURS))
        .repeatNumber(4)
        .repeatDays(Set.of(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY))
        .build();

    IEvent copy = event.copy(0, ZONE_ID);

    String expected = "subject: Meeting, startDateTime: 2025-04-01T10:00-04:00[US/Eastern],"
        + " endDateTime: 2025-04-01T11:00-04:00[US/Eastern], description: , location: ,"
        + " isAllDay: false, isPrivate: false, repeatNumber: 4,"
        + " repeatEndDateTime: 9999-12-29T23:00-05:00[US/Eastern]";
    assertEquals(expected, copy.toString());
  }

  @Test
  public void testCopySameTimeZone() {
    ChronoZonedDateTime<LocalDate> startDateTime =
        TimeUtils.parseDateTimeString("2025-04-01T10:00", ZONE_ID);
    IRepeatingEvent event = new RepeatingEvent.RepeatingEventBuilder()
        .subject("Meeting")
        .startDateTime(startDateTime)
        .endDateTime(startDateTime.plus(1, ChronoUnit.HOURS))
        .repeatNumber(4)
        .repeatDays(Set.of(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY))
        .build();

    ChronoZonedDateTime<LocalDate> newStartDateTime =
        TimeUtils.parseDateTimeString("2025-04-02T18:00", ZONE_ID);
    long timeDifference = TimeUtils.getDifferenceInMinutes(startDateTime, newStartDateTime);

    IEvent copy = event.copy(timeDifference, ZONE_ID);

    String expected = "subject: Meeting, startDateTime: 2025-04-02T18:00-04:00[US/Eastern], "
        + "endDateTime: 2025-04-02T19:00-04:00[US/Eastern], description: , location: , "
        + "isAllDay: false, isPrivate: false, repeatNumber: 4, "
        + "repeatEndDateTime: 9999-12-31T07:00-05:00[US/Eastern]";
    assertEquals(expected, copy.toString());
  }

  @Test
  public void testCopyNewTimeZone() {
    ChronoZonedDateTime<LocalDate> startDateTime =
        TimeUtils.parseDateTimeString("2025-04-01T10:00", ZONE_ID);
    IRepeatingEvent event = new RepeatingEvent.RepeatingEventBuilder()
        .subject("Meeting")
        .startDateTime(startDateTime)
        .endDateTime(startDateTime.plus(1, ChronoUnit.HOURS))
        .repeatNumber(4)
        .repeatDays(Set.of(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY))
        .build();

    ZoneId newZone = ZoneId.of("Europe/Berlin");
    ChronoZonedDateTime<LocalDate> newStartDateTime =
        TimeUtils.parseDateTimeString("2025-04-02T18:00", newZone);
    long timeDifference = TimeUtils.getDifferenceInMinutes(startDateTime, newStartDateTime);

    IEvent copy = event.copy(timeDifference, newZone);

    String expected = "subject: Meeting, startDateTime: 2025-04-02T12:00+02:00[Europe/Berlin],"
        + " endDateTime: 2025-04-02T13:00+02:00[Europe/Berlin], description: , "
        + "location: , isAllDay: false, isPrivate: false, repeatNumber: 4,"
        + " repeatEndDateTime: 9999-12-31T01:00+01:00[Europe/Berlin]";
    assertEquals(expected, copy.toString());
  }

  @Test
  public void testCopyNewTimeZoneRepeatEndDateTime() {
    ChronoZonedDateTime<LocalDate> startDateTime =
        TimeUtils.parseDateTimeString("2025-04-01T10:00", ZONE_ID);
    IRepeatingEvent event = new RepeatingEvent.RepeatingEventBuilder()
        .subject("Meeting")
        .startDateTime(startDateTime)
        .endDateTime(startDateTime.plus(1, ChronoUnit.HOURS))
        .repeatDays(Set.of(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY))
        .repeatEndDateTime(startDateTime.plus(1, ChronoUnit.WEEKS))
        .build();

    ZoneId newZone = ZoneId.of("Europe/Berlin");
    ChronoZonedDateTime<LocalDate> newStartDateTime =
        TimeUtils.parseDateTimeString("2025-04-02T18:00", newZone);
    long timeDifference = TimeUtils.getDifferenceInMinutes(startDateTime, newStartDateTime);

    IEvent copy = event.copy(timeDifference, newZone);

    String expected = "subject: Meeting, startDateTime: 2025-04-02T12:00+02:00[Europe/Berlin], "
        + "endDateTime: 2025-04-02T13:00+02:00[Europe/Berlin], description: , location: , "
        + "isAllDay: false, isPrivate: false, repeatNumber: 0, "
        + "repeatEndDateTime: 2025-04-09T12:00+02:00[Europe/Berlin]";
    assertEquals(expected, copy.toString());
  }

  @Test
  public void testCopyPacificTimeZoneRepeatEndDateTime() {
    ChronoZonedDateTime<LocalDate> startDateTime =
        TimeUtils.parseDateTimeString("2025-04-01T10:00", ZONE_ID);
    IRepeatingEvent event = new RepeatingEvent.RepeatingEventBuilder()
        .subject("Meeting")
        .startDateTime(startDateTime)
        .endDateTime(startDateTime.plus(1, ChronoUnit.HOURS))
        .repeatDays(Set.of(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY))
        .repeatEndDateTime(startDateTime.plus(1, ChronoUnit.WEEKS))
        .build();

    ZoneId newZone = ZoneId.of("US/Pacific");
    ChronoZonedDateTime<LocalDate> newStartDateTime =
        TimeUtils.parseDateTimeString("2025-04-01T19:00", newZone);
    long timeDifference = TimeUtils.getDifferenceInMinutes(startDateTime, newStartDateTime);

    IEvent copy = event.copy(timeDifference, newZone);

    String expected = "subject: Meeting, startDateTime: 2025-04-01T22:00-07:00[US/Pacific], "
        + "endDateTime: 2025-04-01T23:00-07:00[US/Pacific], description: , location: , "
        + "isAllDay: false, isPrivate: false, repeatNumber: 0, "
        + "repeatEndDateTime: 2025-04-08T22:00-07:00[US/Pacific]";
    assertEquals(expected, copy.toString());
  }

  @Test
  public void testCopyPacificTimeZoneRepeatEndDateTimeSmallTimeDifference() {
    ChronoZonedDateTime<LocalDate> startDateTime =
        TimeUtils.parseDateTimeString("2025-04-01T10:00", ZONE_ID);
    IRepeatingEvent event = new RepeatingEvent.RepeatingEventBuilder()
        .subject("Meeting")
        .startDateTime(startDateTime)
        .endDateTime(startDateTime.plus(1, ChronoUnit.HOURS))
        .repeatDays(Set.of(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY))
        .repeatEndDateTime(startDateTime.plus(1, ChronoUnit.WEEKS))
        .build();

    ZoneId newZone = ZoneId.of("US/Pacific");
    ChronoZonedDateTime<LocalDate> newStartDateTime =
        TimeUtils.parseDateTimeString("2025-04-01T09:00", newZone);
    long timeDifference = TimeUtils.getDifferenceInMinutes(startDateTime, newStartDateTime);

    IEvent copy = event.copy(timeDifference, newZone);

    String expected = "subject: Meeting, startDateTime: 2025-04-01T12:00-07:00[US/Pacific], "
        + "endDateTime: 2025-04-01T13:00-07:00[US/Pacific], description: , location: , "
        + "isAllDay: false, isPrivate: false, repeatNumber: 0, "
        + "repeatEndDateTime: 2025-04-08T09:00-07:00[US/Pacific]";
    assertEquals(expected, copy.toString());
  }

  @Test
  public void testCopyPacificTimeZoneRepeatEndDateTimeLargeDifference() {
    ChronoZonedDateTime<LocalDate> startDateTime =
        TimeUtils.parseDateTimeString("2025-04-01T10:00", ZONE_ID);
    IRepeatingEvent event = new RepeatingEvent.RepeatingEventBuilder()
        .subject("Meeting")
        .startDateTime(startDateTime)
        .endDateTime(startDateTime.plus(1, ChronoUnit.HOURS))
        .repeatDays(Set.of(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY))
        .repeatEndDateTime(startDateTime.plus(1, ChronoUnit.WEEKS))
        .build();

    ZoneId newZone = ZoneId.of("US/Pacific");
    ChronoZonedDateTime<LocalDate> newStartDateTime =
        TimeUtils.parseDateTimeString("2025-04-01T11:00", newZone);
    long timeDifference = TimeUtils.getDifferenceInMinutes(startDateTime, newStartDateTime);

    IEvent copy = event.copy(timeDifference, newZone);

    String expected = "subject: Meeting, startDateTime: 2025-04-01T14:00-07:00[US/Pacific], "
        + "endDateTime: 2025-04-01T15:00-07:00[US/Pacific], description: , location: , "
        + "isAllDay: false, isPrivate: false, repeatNumber: 0, "
        + "repeatEndDateTime: 2025-04-08T14:00-07:00[US/Pacific]";
    assertEquals(expected, copy.toString());
  }

  @Test
  public void testCopyPacificTimeZoneRepeatEndDateTimeEqualDifference() {
    ChronoZonedDateTime<LocalDate> startDateTime =
        TimeUtils.parseDateTimeString("2025-04-01T10:00", ZONE_ID);
    IRepeatingEvent event = new RepeatingEvent.RepeatingEventBuilder()
        .subject("Meeting")
        .startDateTime(startDateTime)
        .endDateTime(startDateTime.plus(1, ChronoUnit.HOURS))
        .repeatDays(Set.of(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY))
        .repeatEndDateTime(startDateTime.plus(1, ChronoUnit.WEEKS))
        .build();

    ZoneId newZone = ZoneId.of("US/Pacific");
    ChronoZonedDateTime<LocalDate> newStartDateTime =
        TimeUtils.parseDateTimeString("2025-04-01T10:00", newZone);
    long timeDifference = TimeUtils.getDifferenceInMinutes(startDateTime, newStartDateTime);

    IEvent copy = event.copy(timeDifference, newZone);

    String expected = "subject: Meeting, startDateTime: 2025-04-01T13:00-07:00[US/Pacific], "
        + "endDateTime: 2025-04-01T14:00-07:00[US/Pacific], description: , location: , "
        + "isAllDay: false, isPrivate: false, repeatNumber: 0, "
        + "repeatEndDateTime: 2025-04-08T10:00-07:00[US/Pacific]";
    assertEquals(expected, copy.toString());
  }

  @Test
  public void testUpdateTimezone() {
    ChronoZonedDateTime<LocalDate> startDateTime =
        TimeUtils.parseDateTimeString("2025-04-01T10:00", ZONE_ID);
    IRepeatingEvent event = new RepeatingEvent.RepeatingEventBuilder()
        .subject("Meeting")
        .startDateTime(startDateTime)
        .endDateTime(startDateTime.plus(1, ChronoUnit.HOURS))
        .repeatDays(Set.of(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY))
        .repeatEndDateTime(startDateTime.plus(1, ChronoUnit.WEEKS))
        .build();

    ZoneId newZone = ZoneId.of("US/Pacific");
    event.updateTimezone(newZone);

    String expected = "subject: Meeting, startDateTime: 2025-04-01T07:00-07:00[US/Pacific], "
        + "endDateTime: 2025-04-01T08:00-07:00[US/Pacific], description: , location: , "
        + "isAllDay: false, isPrivate: false, repeatNumber: 0, "
        + "repeatEndDateTime: 2025-04-08T07:00-07:00[US/Pacific]";
    assertEquals(expected, event.toString());
  }

  @Test
  public void testGetEditableProperties() {
    Set<String> properties = RepeatingEvent.getEditableProperties();
    assertEquals(3, properties.size());
  }
}