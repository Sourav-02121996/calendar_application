package calendar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.BeforeClass;
import org.junit.Test;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.chrono.ChronoZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Set;

import utils.TimeUtils;

/**
 * A JUnit test class for testing the Event class.
 */
public class EventTest {

  private static ZoneId ZONE_ID;

  @BeforeClass
  public static void setUpBeforeClass() {
    ZONE_ID = ZoneId.of("US/Eastern");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEventBuilderWithNoArguments() {
    new Event.EventBuilder().build();
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEventBuilderWithEmptySubject() {
    new Event.EventBuilder()
        .subject("")
        .build();
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEventBuilderWithNullStartDateTime() {
    new Event.EventBuilder()
        .subject("1")
        .startDateTime(null)
        .build();
  }

  @Test
  public void testEventBuilderWithBasicArguments() {
    IEvent event = new Event.EventBuilder()
        .subject("test")
        .startDateTime(ZonedDateTime.now())
        .endDateTime(ZonedDateTime.now().plusDays(1))
        .build();
    assertNotNull(event);
    assertFalse(event.isPrivate());
  }

  @Test
  public void testEventBuilderWithAllArguments() {
    IEvent event = new Event.EventBuilder()
        .subject("test")
        .startDateTime(ZonedDateTime.now())
        .endDateTime(ZonedDateTime.now())
        .description("test description")
        .isPrivate(true)
        .location("location")
        .build();

    assertNotNull(event);
    assertTrue(event.isPrivate());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEventBuilderWithSameStartAndEndDateTime() {
    ZonedDateTime time = ZonedDateTime.now();
    new Event.EventBuilder()
        .subject("test")
        .startDateTime(time)
        .endDateTime(time)
        .description("test description")
        .isPrivate(true)
        .location("location")
        .build();
  }

  @Test
  public void testConflictsWithSameEvents() {
    ZonedDateTime startDateTime = ZonedDateTime.now();
    IEvent event1 = new Event.EventBuilder()
        .subject("event 1")
        .startDateTime(startDateTime)
        .endDateTime(startDateTime.plusHours(2))
        .build();
    IEvent event2 = new Event.EventBuilder()
        .subject("event 2")
        .startDateTime(startDateTime)
        .endDateTime(startDateTime.plusHours(2))
        .build();

    assertTrue(event1.conflictsWith(event2));
    assertTrue(event2.conflictsWith(event1));
  }

  @Test
  public void testConflictsWithContainedEvent() {
    IEvent event1 = new Event.EventBuilder()
        .subject("event 1")
        .startDateTime(ZonedDateTime.now())
        .endDateTime(ZonedDateTime.now().plusHours(5))
        .build();
    IEvent event2 = new Event.EventBuilder()
        .subject("event 2")
        .startDateTime(ZonedDateTime.now().plusHours(1))
        .endDateTime(ZonedDateTime.now().plusHours(2))
        .build();
    assertTrue(event1.conflictsWith(event2));
    assertTrue(event2.conflictsWith(event1));
  }

  @Test
  public void testConflictsWithEventsWithOverlappingStartDateTime() {
    IEvent event1 = new Event.EventBuilder()
        .subject("event 1")
        .startDateTime(ZonedDateTime.now())
        .endDateTime(ZonedDateTime.now().plusHours(3))
        .build();
    IEvent event2 = new Event.EventBuilder()
        .subject("event 2")
        .startDateTime(ZonedDateTime.now().plusHours(2))
        .endDateTime(ZonedDateTime.now().plusHours(5))
        .build();
    assertTrue(event1.conflictsWith(event2));
    assertTrue(event2.conflictsWith(event1));
  }

  @Test
  public void testConflictsWithSameAllDayEvents() {
    IEvent event1 = new Event.EventBuilder()
        .subject("event 1")
        .startDateTime(ZonedDateTime.now())
        .endDateTime(ZonedDateTime.now().plusDays(1))
        .isAllDay(true)
        .build();
    IEvent event2 = new Event.EventBuilder()
        .subject("event 2")
        .startDateTime(ZonedDateTime.now())
        .endDateTime(ZonedDateTime.now().plusDays(1))
        .isAllDay(true)
        .build();
    assertTrue(event1.conflictsWith(event2));
    assertTrue(event2.conflictsWith(event1));
  }

  @Test
  public void testConflictsWithDifferentAllDayEvents() {
    IEvent event1 = new Event.EventBuilder()
        .subject("event 1")
        .startDateTime(ZonedDateTime.now().plusDays(3))
        .endDateTime(ZonedDateTime.now().plusDays(4))
        .isAllDay(true)
        .build();
    IEvent event2 = new Event.EventBuilder()
        .subject("event 2")
        .startDateTime(ZonedDateTime.now())
        .endDateTime(ZonedDateTime.now().plusDays(1))
        .isAllDay(true)
        .build();
    assertFalse(event1.conflictsWith(event2));
    assertFalse(event2.conflictsWith(event1));
  }

  @Test
  public void testConflictsWithAnAllDayEvent() {
    IEvent event1 = new Event.EventBuilder()
        .subject("event 1")
        .startDateTime(ZonedDateTime.now())
        .endDateTime(ZonedDateTime.now().plusDays(1))
        .isAllDay(true)
        .build();
    IEvent event2 = new Event.EventBuilder()
        .subject("event 2")
        .startDateTime(ZonedDateTime.now())
        .endDateTime(ZonedDateTime.now().plusHours(3))
        .build();
    assertTrue(event1.conflictsWith(event2));
    assertTrue(event2.conflictsWith(event1));
  }

  @Test
  public void testConflictsWithAllDayEventOverlappingEndOfSpanningEvent() {
    IEvent event1 = new Event.EventBuilder()
        .subject("event 1")
        .startDateTime(ZonedDateTime.now())
        .endDateTime(ZonedDateTime.now().plusDays(1).plusHours(3))
        .build();
    IEvent event2 = new Event.EventBuilder()
        .subject("event 2")
        .startDateTime(ZonedDateTime.now().plusDays(1))
        .endDateTime(ZonedDateTime.now().plusDays(2))
        .isAllDay(true)
        .build();
    assertTrue(event1.conflictsWith(event2));
    assertTrue(event2.conflictsWith(event1));
  }

  @Test
  public void testConflictsWithAllDayEventOverlappingStartOfSpanningEvent() {
    IEvent event1 = new Event.EventBuilder()
        .subject("event 1")
        .startDateTime(ZonedDateTime.now().plusDays(2))
        .endDateTime(ZonedDateTime.now().plusDays(3).plusHours(3))
        .build();
    IEvent event2 = new Event.EventBuilder()
        .subject("event 2")
        .startDateTime(ZonedDateTime.now().plusDays(2))
        .endDateTime(ZonedDateTime.now().plusDays(3))
        .isAllDay(true)
        .build();
    assertTrue(event1.conflictsWith(event2));
    assertTrue(event2.conflictsWith(event1));
  }

  @Test
  public void testConflictsWithAllDayEventAndSpanningEvent() {
    IEvent event1 = new Event.EventBuilder()
        .subject("event 1")
        .startDateTime(ZonedDateTime.now())
        .endDateTime(ZonedDateTime.now().plusDays(1).plusHours(3))
        .build();
    IEvent event2 = new Event.EventBuilder()
        .subject("event 2")
        .startDateTime(ZonedDateTime.now().plusDays(10))
        .endDateTime(ZonedDateTime.now().plusDays(11))
        .isAllDay(true)
        .build();
    assertFalse(event1.conflictsWith(event2));
    assertFalse(event2.conflictsWith(event1));
  }

  @Test
  public void testConflictsWithEventsOnSameDay() {
    ChronoZonedDateTime<LocalDate> startDateTime =
        TimeUtils.parseDateTimeString("2020-01-01T08:00", ZONE_ID);

    IEvent event1 = new Event.EventBuilder()
        .subject("event 1")
        .startDateTime(startDateTime)
        .endDateTime(startDateTime.plus(1, ChronoUnit.HOURS))
        .build();
    IEvent event2 = new Event.EventBuilder()
        .subject("event 2")
        .startDateTime(startDateTime.plus(2, ChronoUnit.HOURS))
        .endDateTime(startDateTime.plus(3, ChronoUnit.HOURS))
        .build();
    assertFalse(event1.conflictsWith(event2));
    assertFalse(event2.conflictsWith(event1));
  }

  @Test
  public void testConflictsWithBackToBackEvents() {
    ChronoZonedDateTime<LocalDate> startDateTime =
        TimeUtils.parseDateTimeString("2020-01-01T08:00", ZONE_ID);

    IEvent event1 = new Event.EventBuilder()
        .subject("event 1")
        .startDateTime(startDateTime)
        .endDateTime(startDateTime.plus(1, ChronoUnit.HOURS))
        .build();
    IEvent event2 = new Event.EventBuilder()
        .subject("event 2")
        .startDateTime(startDateTime.plus(1, ChronoUnit.HOURS))
        .endDateTime(startDateTime.plus(2, ChronoUnit.HOURS))
        .build();
    assertFalse(event1.conflictsWith(event2));
    assertFalse(event2.conflictsWith(event1));
  }

  @Test
  public void testConflictsWithBackToBackEventsWithOverlap() {
    ChronoZonedDateTime<LocalDate> startDateTime =
        TimeUtils.parseDateTimeString("2020-01-01T08:00", ZONE_ID);

    IEvent event1 = new Event.EventBuilder()
        .subject("event 1")
        .startDateTime(startDateTime)
        .endDateTime(startDateTime.plus(1, ChronoUnit.HOURS)
            .plus(1, ChronoUnit.MINUTES))
        .build();
    IEvent event2 = new Event.EventBuilder()
        .subject("event 2")
        .startDateTime(startDateTime.plus(1, ChronoUnit.HOURS))
        .endDateTime(startDateTime.plus(2, ChronoUnit.HOURS))
        .build();
    assertTrue(event1.conflictsWith(event2));
    assertTrue(event2.conflictsWith(event1));
  }

  @Test
  public void testCompareToWithSameEvent() {
    ChronoZonedDateTime<LocalDate> startDateTime =
        TimeUtils.parseDateTimeString("2020-01-01T08:00", ZONE_ID);
    IEvent event1 = new Event.EventBuilder()
        .subject("event 1")
        .startDateTime(startDateTime)
        .endDateTime(startDateTime.plus(1, ChronoUnit.HOURS))
        .build();
    IEvent event2 = new Event.EventBuilder()
        .subject("event 2")
        .startDateTime(startDateTime)
        .endDateTime(startDateTime.plus(1, ChronoUnit.HOURS))
        .build();
    assertEquals(0, event1.compareTo(event2));
    assertEquals(0, event2.compareTo(event1));
  }

  @Test
  public void testCompareToWithBackToBackEvents() {
    ChronoZonedDateTime<LocalDate> startDateTime =
        TimeUtils.parseDateTimeString("2020-01-01T08:00", ZONE_ID);
    IEvent event1 = new Event.EventBuilder()
        .subject("event 1")
        .startDateTime(startDateTime)
        .endDateTime(startDateTime.plus(1, ChronoUnit.HOURS))
        .build();
    IEvent event2 = new Event.EventBuilder()
        .subject("event 2")
        .startDateTime(startDateTime.plus(1, ChronoUnit.HOURS))
        .endDateTime(startDateTime.plus(2, ChronoUnit.HOURS))
        .build();
    assertEquals(-1, event1.compareTo(event2));
    assertEquals(1, event2.compareTo(event1));
  }

  @Test
  public void testCompareToWithSameAllDayEvents() {
    ChronoZonedDateTime<LocalDate> startDateTime =
        TimeUtils.parseDateTimeString("2020-01-01T08:00", ZONE_ID);
    IEvent event1 = new Event.EventBuilder()
        .subject("event 1")
        .startDateTime(startDateTime)
        .endDateTime(startDateTime.plus(1, ChronoUnit.HOURS))
        .build();
    IEvent event2 = new Event.EventBuilder()
        .subject("event 2")
        .startDateTime(startDateTime)
        .endDateTime(startDateTime.plus(1, ChronoUnit.HOURS))
        .build();
    assertEquals(0, event1.compareTo(event2));
    assertEquals(0, event2.compareTo(event1));
  }

  @Test
  public void testCompareToWithAllDayEvents() {
    ChronoZonedDateTime<LocalDate> startDateTime =
        TimeUtils.parseDateTimeString("2020-01-01T08:00", ZONE_ID);
    IEvent event1 = new Event.EventBuilder()
        .subject("event 1")
        .startDateTime(startDateTime.plus(1, ChronoUnit.DAYS))
        .endDateTime(startDateTime.plus(2, ChronoUnit.DAYS))
        .isAllDay(true)
        .build();
    IEvent event2 = new Event.EventBuilder()
        .subject("event 2")
        .startDateTime(startDateTime)
        .endDateTime(startDateTime.plus(1, ChronoUnit.DAYS))
        .isAllDay(true)
        .build();
    assertEquals(1, event1.compareTo(event2));
    assertEquals(-1, event2.compareTo(event1));
  }

  @Test
  public void testCompareToWithAllDayEventAndSpanningEvent() {
    ChronoZonedDateTime<LocalDate> startDateTime =
        TimeUtils.parseDateTimeString("2020-01-01T08:00", ZONE_ID);
    IEvent event1 = new Event.EventBuilder()
        .subject("event 1")
        .startDateTime(startDateTime.plus(1, ChronoUnit.DAYS))
        .endDateTime(startDateTime.plus(2, ChronoUnit.DAYS))
        .isAllDay(true)
        .build();
    IEvent event2 = new Event.EventBuilder()
        .subject("event 2")
        .startDateTime(startDateTime)
        .endDateTime(startDateTime.plus(1, ChronoUnit.HOURS))
        .build();
    assertEquals(1, event1.compareTo(event2));
    assertEquals(-1, event2.compareTo(event1));
  }

  @Test
  public void testCompareToWithAllDayEventAndSpanningEventOnSameDay() {
    ChronoZonedDateTime<LocalDate> startDateTime =
        TimeUtils.parseDateTimeString("2020-01-01T08:00", ZONE_ID);
    IEvent event1 = new Event.EventBuilder()
        .subject("event 1")
        .startDateTime(startDateTime)
        .endDateTime(startDateTime.plus(1, ChronoUnit.DAYS))
        .isAllDay(true)
        .build();
    IEvent event2 = new Event.EventBuilder()
        .subject("event 2")
        .startDateTime(startDateTime)
        .endDateTime(startDateTime.plus(1, ChronoUnit.HOURS))
        .build();
    assertEquals(1, event1.compareTo(event2));
    assertEquals(-1, event2.compareTo(event1));
  }

  @Test
  public void testCompareToEventsWithSameStartDateTimeButDifferentEndDateTime() {
    ChronoZonedDateTime<LocalDate> startDateTime =
        TimeUtils.parseDateTimeString("2020-01-01T08:00", ZONE_ID);
    IEvent event1 = new Event.EventBuilder()
        .subject("event 1")
        .startDateTime(startDateTime)
        .endDateTime(startDateTime.plus(2, ChronoUnit.HOURS))
        .build();
    IEvent event2 = new Event.EventBuilder()
        .subject("event 2")
        .startDateTime(startDateTime)
        .endDateTime(startDateTime.plus(3, ChronoUnit.HOURS))
        .build();
    assertEquals(-1, event1.compareTo(event2));
    assertEquals(1, event2.compareTo(event1));
  }

  @Test
  public void testGetSubject() {
    String subject = "test";
    IEvent event = new Event.EventBuilder()
        .subject(subject)
        .startDateTime(ZonedDateTime.now())
        .endDateTime(ZonedDateTime.now().plusHours(1))
        .build();

    assertEquals(subject, event.getSubject());
  }

  @Test
  public void testGetStartDateTime() {
    ChronoZonedDateTime<LocalDate> startDateTime =
        TimeUtils.parseDateTimeString("2020-01-01T08:00", ZONE_ID);
    IEvent event = new Event.EventBuilder()
        .subject("test")
        .startDateTime(startDateTime)
        .endDateTime(startDateTime.plus(1, ChronoUnit.HOURS))
        .build();

    assertEquals(startDateTime, event.getStartDateTime());
  }

  @Test
  public void testSetNewPropertyLocation() {
    ChronoZonedDateTime<LocalDate> startDateTime =
        TimeUtils.parseDateTimeString("2025-01-01T08:00", ZONE_ID);
    IEvent event = new Event.EventBuilder()
        .subject("test")
        .startDateTime(startDateTime)
        .endDateTime(startDateTime.plus(1, ChronoUnit.HOURS))
        .build();

    try {
      event.setNewProperty("location", "new location");
    } catch (Exception e) {
      fail(e.getMessage());
    }

    String expected = "subject: test, startDateTime: 2025-01-01T08:00-05:00[US/Eastern], "
        + "endDateTime: 2025-01-01T09:00-05:00[US/Eastern], description: , "
        + "location: new location, isAllDay: false, isPrivate: false";
    assertEquals(expected, event.toString());
  }

  @Test
  public void testSetNewPropertyDescription() {
    ChronoZonedDateTime<LocalDate> startDateTime =
        TimeUtils.parseDateTimeString("2025-01-01T08:00", ZONE_ID);
    IEvent event = new Event.EventBuilder()
        .subject("test")
        .startDateTime(startDateTime)
        .endDateTime(startDateTime.plus(1, ChronoUnit.HOURS))
        .build();

    try {
      event.setNewProperty("description", "new description");
    } catch (Exception e) {
      fail(e.getMessage());
    }

    String expected = "subject: test, startDateTime: 2025-01-01T08:00-05:00[US/Eastern], "
        + "endDateTime: 2025-01-01T09:00-05:00[US/Eastern], description: new description, "
        + "location: , isAllDay: false, isPrivate: false";
    assertEquals(expected, event.toString());
  }

  @Test
  public void testSetNewPropertySubject() {
    ChronoZonedDateTime<LocalDate> startDateTime =
        TimeUtils.parseDateTimeString("2025-01-01T08:00", ZONE_ID);
    IEvent event = new Event.EventBuilder()
        .subject("test")
        .startDateTime(startDateTime)
        .endDateTime(startDateTime.plus(1, ChronoUnit.HOURS))
        .build();

    try {
      event.setNewProperty("subject", "new subject");
    } catch (Exception e) {
      fail(e.getMessage());
    }

    String expected = "subject: new subject, startDateTime: 2025-01-01T08:00-05:00[US/Eastern], "
        + "endDateTime: 2025-01-01T09:00-05:00[US/Eastern], description: , "
        + "location: , isAllDay: false, isPrivate: false";
    assertEquals(expected, event.toString());
  }

  @Test
  public void testSetNewPropertyStartDateTime() {
    ChronoZonedDateTime<LocalDate> startDateTime =
        TimeUtils.parseDateTimeString("2025-01-01T08:00", ZONE_ID);
    IEvent event = new Event.EventBuilder()
        .subject("test")
        .startDateTime(startDateTime)
        .endDateTime(startDateTime.plus(1, ChronoUnit.HOURS))
        .build();

    try {
      event.setNewProperty("startDateTime", "2025-01-01T07:00");
    } catch (Exception e) {
      fail(e.getMessage());
    }

    String expected = "subject: test, startDateTime: 2025-01-01T07:00-05:00[US/Eastern], "
        + "endDateTime: 2025-01-01T09:00-05:00[US/Eastern], description: , location: , "
        + "isAllDay: false, isPrivate: false";
    assertEquals(expected, event.toString());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testSetNewPropertyInvalidProperty() {
    ChronoZonedDateTime<LocalDate> startDateTime =
        TimeUtils.parseDateTimeString("2025-01-01T08:00", ZONE_ID);
    IEvent event = new Event.EventBuilder()
        .subject("test")
        .startDateTime(startDateTime)
        .endDateTime(startDateTime.plus(1, ChronoUnit.DAYS))
        .isAllDay(true)
        .build();

    event.setNewProperty("invalid", "new");
  }

  @Test
  public void testSetNewPropertyEndDateTime() {
    ChronoZonedDateTime<LocalDate> startDateTime =
        TimeUtils.parseDateTimeString("2025-01-01T08:00", ZONE_ID);
    IEvent event = new Event.EventBuilder()
        .subject("test")
        .startDateTime(startDateTime)
        .endDateTime(startDateTime.plus(1, ChronoUnit.DAYS))
        .isAllDay(true)
        .build();

    try {
      event.setNewProperty("endDateTime", "2025-01-01T09:00");
    } catch (Exception e) {
      fail(e.getMessage());
    }

    String expected = "subject: test, startDateTime: 2025-01-01T08:00-05:00[US/Eastern], "
        + "endDateTime: 2025-01-01T09:00-05:00[US/Eastern], description: , "
        + "location: , isAllDay: false, isPrivate: false";
    assertEquals(expected, event.toString());
  }

  @Test
  public void testChangeEventToSpanningEvent() {
    ChronoZonedDateTime<LocalDate> startDateTime =
        TimeUtils.parseDateTimeString("2025-01-01T08:00", ZONE_ID);
    IEvent event = new Event.EventBuilder()
        .subject("test")
        .startDateTime(startDateTime)
        .endDateTime(startDateTime.plus(1, ChronoUnit.HOURS))
        .build();

    try {
      event.setNewProperty("endDateTime", "2025-01-02T14:00");
    } catch (Exception e) {
      fail(e.getMessage());
    }

    String expected = "subject: test, startDateTime: 2025-01-01T08:00-05:00[US/Eastern], "
        + "endDateTime: 2025-01-02T14:00-05:00[US/Eastern], description: , location: , "
        + "isAllDay: false, isPrivate: false";
    assertEquals(expected, event.toString());
  }

  @Test
  public void testChangeEventToAllDayEvent() {
    ChronoZonedDateTime<LocalDate> startDateTime =
        TimeUtils.parseDateTimeString("2025-01-01T08:00", ZONE_ID);
    IEvent event = new Event.EventBuilder()
        .subject("test")
        .startDateTime(startDateTime)
        .endDateTime(startDateTime.plus(1, ChronoUnit.HOURS))
        .build();

    String expected = "subject: test, startDateTime: 2025-01-01T08:00-05:00[US/Eastern], "
        + "endDateTime: 2025-01-01T09:00-05:00[US/Eastern], description: , location: , "
        + "isAllDay: false, isPrivate: false";
    assertEquals(expected, event.toString());

    // set startDateTime to 00:00 time
    try {
      event.setNewProperty("startDateTime", "2025-01-01T00:00");
    } catch (Exception e) {
      fail(e.getMessage());
    }

    expected = "subject: test, startDateTime: 2025-01-01T00:00-05:00[US/Eastern], "
        + "endDateTime: 2025-01-01T09:00-05:00[US/Eastern], description: , location: , "
        + "isAllDay: false, isPrivate: false";
    assertEquals(expected, event.toString());

    // set endDateTime to 00:00 time
    try {
      event.setNewProperty("endDateTime", "2025-01-02T00:00");
    } catch (Exception e) {
      fail(e.getMessage());
    }

    expected = "subject: test, startDateTime: 2025-01-01T00:00-05:00[US/Eastern], "
        + "endDateTime: 2025-01-02T00:00-05:00[US/Eastern], description: , location: , "
        + "isAllDay: true, isPrivate: false";
    assertEquals(expected, event.toString());
  }

  @Test
  public void testSetNewPropertyPrivate() {
    ChronoZonedDateTime<LocalDate> startDateTime =
        TimeUtils.parseDateTimeString("2025-01-01T08:00", ZONE_ID);
    IEvent event = new Event.EventBuilder()
        .subject("test")
        .startDateTime(startDateTime)
        .endDateTime(startDateTime.plus(1, ChronoUnit.HOURS))
        .build();

    String expected = "subject: test, startDateTime: 2025-01-01T08:00-05:00[US/Eastern], "
        + "endDateTime: 2025-01-01T09:00-05:00[US/Eastern], description: , location: , "
        + "isAllDay: false, isPrivate: false";
    assertEquals(expected, event.toString());

    try {
      event.setNewProperty("private", "true");
    } catch (Exception e) {
      fail(e.getMessage());
    }

    expected = "subject: test, startDateTime: 2025-01-01T08:00-05:00[US/Eastern], "
        + "endDateTime: 2025-01-01T09:00-05:00[US/Eastern], description: , location: , "
        + "isAllDay: false, isPrivate: true";
    assertEquals(expected, event.toString());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testSetNewPropertyInvalid() throws IllegalArgumentException {
    ChronoZonedDateTime<LocalDate> startDateTime =
        TimeUtils.parseDateTimeString("2025-01-01T08:00", ZONE_ID);
    IEvent event = new Event.EventBuilder()
        .subject("test")
        .startDateTime(startDateTime)
        .build();
    assertNotNull(event);

    event.setNewProperty("invalid", "value");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testSetSubjectNullValue() throws IllegalArgumentException {
    ChronoZonedDateTime<LocalDate> startDateTime =
        TimeUtils.parseDateTimeString("2025-01-01T08:00", ZONE_ID);
    IEvent event = new Event.EventBuilder()
        .subject("test")
        .startDateTime(startDateTime)
        .build();
    assertNotNull(event);

    event.setNewProperty("subject", null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testSetSubjectEmptyValue() throws IllegalArgumentException {
    ChronoZonedDateTime<LocalDate> startDateTime =
        TimeUtils.parseDateTimeString("2025-01-01T08:00", ZONE_ID);
    IEvent event = new Event.EventBuilder()
        .subject("test")
        .startDateTime(startDateTime)
        .build();
    assertNotNull(event);

    event.setNewProperty("subject", "");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testSetStartDateTimeNullValue() throws IllegalArgumentException {
    ChronoZonedDateTime<LocalDate> startDateTime =
        TimeUtils.parseDateTimeString("2025-01-01T08:00", ZONE_ID);
    IEvent event = new Event.EventBuilder()
        .subject("test")
        .startDateTime(startDateTime)
        .endDateTime(startDateTime.plus(1, ChronoUnit.HOURS))
        .build();
    assertNotNull(event);

    event.setNewProperty("startDateTime", null);
  }

  @Test
  public void testSetStartDateTimeMidnight() {
    ChronoZonedDateTime<LocalDate> startDateTime =
        TimeUtils.parseDateTimeString("2025-01-01T08:00", ZONE_ID);
    IEvent event = new Event.EventBuilder()
        .subject("test")
        .startDateTime(startDateTime)
        .endDateTime(startDateTime.plus(1, ChronoUnit.HOURS))
        .build();
    assertNotNull(event);

    event.setNewProperty("startDateTime", "2025-01-01T00:00");

    String expected = "subject: test, startDateTime: 2025-01-01T00:00-05:00[US/Eastern], "
        + "endDateTime: 2025-01-01T09:00-05:00[US/Eastern], description: , "
        + "location: , isAllDay: false, isPrivate: false";
    assertEquals(expected, event.toString());
  }

  @Test
  public void testSetStartDateTimeMidnight2() {
    ChronoZonedDateTime<LocalDate> startDateTime =
        TimeUtils.parseDateTimeString("2025-01-01T08:00", ZONE_ID);
    ChronoZonedDateTime<LocalDate> endDateTime =
        TimeUtils.parseDateTimeString("2025-01-03T00:00", ZONE_ID);
    IEvent event = new Event.EventBuilder()
        .subject("test")
        .startDateTime(startDateTime)
        .endDateTime(endDateTime)
        .build();
    assertNotNull(event);

    event.setNewProperty("startDateTime", "2025-01-02T00:00");

    String expected = "subject: test, startDateTime: 2025-01-02T00:00-05:00[US/Eastern], "
        + "endDateTime: 2025-01-03T00:00-05:00[US/Eastern], description: , "
        + "location: , isAllDay: true, isPrivate: false";
    assertEquals(expected, event.toString());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testSetIsPrivateInvalid() throws IllegalArgumentException {
    ChronoZonedDateTime<LocalDate> startDateTime =
        TimeUtils.parseDateTimeString("2025-01-01T08:00", ZONE_ID);
    IEvent event = new Event.EventBuilder()
        .subject("test")
        .startDateTime(startDateTime)
        .build();

    event.setNewProperty("private", "null");
  }

  @Test
  public void testSetIsPrivateFalse() {
    ChronoZonedDateTime<LocalDate> startDateTime =
        TimeUtils.parseDateTimeString("2025-01-01T08:00", ZONE_ID);
    IEvent event = new Event.EventBuilder()
        .subject("test")
        .startDateTime(startDateTime)
        .endDateTime(startDateTime.plus(1, ChronoUnit.HOURS))
        .isPrivate(true)
        .build();

    try {
      event.setNewProperty("private", "false");
    } catch (Exception e) {
      fail(e.getMessage());
    }

    String expected = "subject: test, startDateTime: 2025-01-01T08:00-05:00[US/Eastern], "
        + "endDateTime: 2025-01-01T09:00-05:00[US/Eastern], description: , location: , "
        + "isAllDay: false, isPrivate: false";
    assertEquals(expected, event.toString());
  }

  @Test
  public void testCopyEvent() {
    ChronoZonedDateTime<LocalDate> startDateTime =
        TimeUtils.parseDateTimeString("2025-01-01T08:00", ZONE_ID);
    IEvent event = new Event.EventBuilder()
        .subject("test")
        .startDateTime(startDateTime)
        .endDateTime(startDateTime.plus(1, ChronoUnit.HOURS))
        .isPrivate(true)
        .build();

    ChronoZonedDateTime<LocalDate> newStartDateTime =
        TimeUtils.parseDateTimeString("2025-01-10T08:00", ZONE_ID);

    long timeDifference = TimeUtils.getDifferenceInMinutes(event.getStartDateTime(),
        newStartDateTime);

    IEvent copy = event.copy(timeDifference, ZONE_ID);
    assertNotNull(copy);
    assertEquals(startDateTime.plus(9, ChronoUnit.DAYS), copy.getStartDateTime());
  }

  @Test
  public void testCopyAllDayEvent() {
    ChronoZonedDateTime<LocalDate> startDateTime =
        TimeUtils.parseDateTimeString("2025-01-01T08:00", ZONE_ID);
    IEvent event = new Event.EventBuilder()
        .subject("test")
        .startDateTime(startDateTime)
        .endDateTime(startDateTime.plus(1, ChronoUnit.DAYS))
        .isAllDay(true)
        .build();

    ChronoZonedDateTime<LocalDate> newStartDateTime =
        TimeUtils.parseDateTimeString("2025-01-10T08:00", ZONE_ID);

    long timeDifference = TimeUtils.getDifferenceInMinutes(event.getStartDateTime(),
        newStartDateTime);

    IEvent copy = event.copy(timeDifference, ZONE_ID);
    assertNotNull(copy);
    assertEquals(startDateTime.plus(9, ChronoUnit.DAYS), copy.getStartDateTime());
    assertEquals(startDateTime.plus(10, ChronoUnit.DAYS), copy.getEndDateTime());
  }

  @Test
  public void testCopyEventDifferentTimezone() {
    ChronoZonedDateTime<LocalDate> startDateTime =
        TimeUtils.parseDateTimeString("2025-01-01T08:00", ZONE_ID);
    IEvent event = new Event.EventBuilder()
        .subject("test")
        .startDateTime(startDateTime)
        .endDateTime(startDateTime.plus(1, ChronoUnit.HOURS))
        .isPrivate(true)
        .build();

    ChronoZonedDateTime<LocalDate> newStartDateTime =
        TimeUtils.parseDateTimeString("2025-01-01T08:00", ZONE_ID);
    long timeDifference = TimeUtils.getDifferenceInMinutes(event.getStartDateTime(),
        newStartDateTime);

    IEvent copy = event.copy(timeDifference, ZoneId.of("US/Central"));
    assertNotNull(copy);
    assertEquals(startDateTime.toLocalDateTime(), copy.getStartDateTime().toLocalDateTime());
    assertEquals(ZoneId.of("US/Central"), copy.getStartDateTime().getZone());
  }

  @Test
  public void testCopyAllDayEventDifferentTimezone() {
    ChronoZonedDateTime<LocalDate> startDateTime =
        TimeUtils.parseDateTimeString("2025-01-01T08:00", ZONE_ID);
    IEvent event = new Event.EventBuilder()
        .subject("test")
        .startDateTime(startDateTime)
        .endDateTime(startDateTime.plus(1, ChronoUnit.DAYS))
        .isAllDay(true)
        .build();

    ChronoZonedDateTime<LocalDate> newStartDateTime =
        TimeUtils.parseDateTimeString("2025-01-01T08:00", ZONE_ID);
    long timeDifference = TimeUtils.getDifferenceInMinutes(event.getStartDateTime(),
        newStartDateTime);

    IEvent copy = event.copy(timeDifference, ZoneId.of("US/Central"));
    assertNotNull(copy);
    assertEquals(startDateTime.toLocalDateTime(), copy.getStartDateTime().toLocalDateTime());
    assertEquals(ZoneId.of("US/Central"), copy.getStartDateTime().getZone());
    assertTrue(copy.isAllDay());
  }

  @Test
  public void testCopyEventDifferentTimezoneAndDifferentTime() {
    ChronoZonedDateTime<LocalDate> startDateTime =
        TimeUtils.parseDateTimeString("2025-01-01T08:00", ZONE_ID);
    IEvent event = new Event.EventBuilder()
        .subject("test")
        .startDateTime(startDateTime)
        .endDateTime(startDateTime.plus(1, ChronoUnit.HOURS))
        .isPrivate(true)
        .build();

    ChronoZonedDateTime<LocalDate> newStartDateTime =
        TimeUtils.parseDateTimeString("2025-01-01T18:00", ZONE_ID);
    long timeDifference = TimeUtils.getDifferenceInMinutes(event.getStartDateTime(),
        newStartDateTime);

    IEvent copy = event.copy(timeDifference, ZoneId.of("US/Central"));
    assertNotNull(copy);
    assertEquals(startDateTime.plus(10, ChronoUnit.HOURS).toLocalDateTime(),
        copy.getStartDateTime().toLocalDateTime());
    assertEquals(ZoneId.of("US/Central"), copy.getStartDateTime().getZone());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testSetSubjectNull() {
    ChronoZonedDateTime<LocalDate> startDateTime =
        TimeUtils.parseDateTimeString("2025-01-01T08:00", ZONE_ID);
    IEvent event = new Event.EventBuilder()
        .subject("test")
        .startDateTime(startDateTime)
        .endDateTime(startDateTime.plus(1, ChronoUnit.HOURS))
        .build();

    event.setSubject(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testSetSubjectEmpty() {
    ChronoZonedDateTime<LocalDate> startDateTime =
        TimeUtils.parseDateTimeString("2025-01-01T08:00", ZONE_ID);
    IEvent event = new Event.EventBuilder()
        .subject("test")
        .startDateTime(startDateTime)
        .endDateTime(startDateTime.plus(1, ChronoUnit.HOURS))
        .build();

    event.setSubject("");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testSetSubjectWhitespace() {
    ChronoZonedDateTime<LocalDate> startDateTime =
        TimeUtils.parseDateTimeString("2025-01-01T08:00", ZONE_ID);
    IEvent event = new Event.EventBuilder()
        .subject("test")
        .startDateTime(startDateTime)
        .endDateTime(startDateTime.plus(1, ChronoUnit.HOURS))
        .build();

    event.setSubject(" ");
  }

  @Test
  public void testSetSubjectSuccess() {
    ChronoZonedDateTime<LocalDate> startDateTime =
        TimeUtils.parseDateTimeString("2025-01-01T08:00", ZONE_ID);
    IEvent event = new Event.EventBuilder()
        .subject("test")
        .startDateTime(startDateTime)
        .endDateTime(startDateTime.plus(1, ChronoUnit.HOURS))
        .build();

    String subject = "new";
    event.setSubject(subject);
    assertEquals(subject, event.getSubject());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testSetStartDateTimeNull() {
    ChronoZonedDateTime<LocalDate> startDateTime =
        TimeUtils.parseDateTimeString("2025-01-01T08:00", ZONE_ID);
    IEvent event = new Event.EventBuilder()
        .subject("test")
        .startDateTime(startDateTime)
        .endDateTime(startDateTime.plus(1, ChronoUnit.HOURS))
        .build();

    event.setStartDateTime(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testSetStartDateTimeAfterEndDateTime() {
    ChronoZonedDateTime<LocalDate> startDateTime =
        TimeUtils.parseDateTimeString("2025-01-01T08:00", ZONE_ID);
    IEvent event = new Event.EventBuilder()
        .subject("test")
        .startDateTime(startDateTime)
        .endDateTime(startDateTime.plus(1, ChronoUnit.HOURS))
        .build();

    event.setStartDateTime("2025-02-01T08:00");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testSetEndDateTimeNull() {
    ChronoZonedDateTime<LocalDate> startDateTime =
        TimeUtils.parseDateTimeString("2025-01-01T08:00", ZONE_ID);
    IEvent event = new Event.EventBuilder()
        .subject("test")
        .startDateTime(startDateTime)
        .endDateTime(startDateTime.plus(1, ChronoUnit.HOURS))
        .build();

    event.setEndDateTime(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testSetEndDateTimeAfterEndDateTime() {
    ChronoZonedDateTime<LocalDate> startDateTime =
        TimeUtils.parseDateTimeString("2025-02-01T08:00", ZONE_ID);
    IEvent event = new Event.EventBuilder()
        .subject("test")
        .startDateTime(startDateTime)
        .endDateTime(startDateTime.plus(1, ChronoUnit.HOURS))
        .build();

    event.setEndDateTime("2025-01-01T08:00");
  }

  @Test
  public void testUpdateTimezone() {
    ChronoZonedDateTime<LocalDate> startDateTime =
        TimeUtils.parseDateTimeString("2025-02-01T08:00", ZONE_ID);
    IEvent event = new Event.EventBuilder()
        .subject("test")
        .startDateTime(startDateTime)
        .endDateTime(startDateTime.plus(1, ChronoUnit.HOURS))
        .build();
    ZoneId zoneId = ZoneId.of("Europe/Berlin");
    event.updateTimezone(zoneId);

    String expected = "subject: test, startDateTime: 2025-02-01T14:00+01:00[Europe/Berlin], "
        + "endDateTime: 2025-02-01T15:00+01:00[Europe/Berlin], description: , location: , "
        + "isAllDay: false, isPrivate: false";
    assertEquals(expected, event.toString());
  }

  @Test
  public void testGetExcludedProperties() {
    Set<String> properties = Event.getExcludedProperties();
    assertEquals(2, properties.size());
  }
}