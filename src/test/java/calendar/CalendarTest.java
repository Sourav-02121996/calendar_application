package calendar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.chrono.ChronoZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import model.ConflictException;
import utils.TimeUtils;

/**
 * A JUnit test class for testing the Calendar class.
 */
public class CalendarTest {

  ICalendar calendar;

  @Before
  public void setUp() {
    calendar = new Calendar("test", "US/Eastern");
  }

  @Test
  public void testConstructor() {
    String name = "main";
    String timeZone = "US/Eastern";
    calendar = new Calendar(name, timeZone);
    assertEquals(name, calendar.getName());
  }

  @Test
  public void testConstructorNullParams() {
    try {
      new Calendar(null, null);
    } catch (IllegalArgumentException e) {
      assertEquals("Name and time zone cannot be null or empty", e.getMessage());
    }
  }

  @Test
  public void testConstructorEmptyParams() {
    try {
      new Calendar("", "");
    } catch (IllegalArgumentException e) {
      assertEquals("Name and time zone cannot be null or empty", e.getMessage());
    }
  }

  @Test
  public void testConstructorInvalidTimeZone() {
    String name = "main";
    String timeZone = "invalid";

    try {
      new Calendar(name, timeZone);
    } catch (IllegalArgumentException e) {
      assertEquals("Invalid time zone: " + timeZone, e.getMessage());
    }
  }

  @Test
  public void testAddEventsConflict() {
    ChronoZonedDateTime<LocalDate> dateTime =
        TimeUtils.parseDateTimeString("2025-03-02T02:30", calendar.getTimezone());
    // add existing events to the calendar
    IEvent event1 = new Event.EventBuilder()
        .subject("1")
        .startDateTime(dateTime.plus(1, ChronoUnit.DAYS))
        .endDateTime(dateTime.plus(2, ChronoUnit.DAYS))
        .isAllDay(true)
        .build();

    try {
      calendar.addEvents(List.of(event1));
    } catch (Exception e) {
      fail(e.getMessage());
    }

    IEvent event2 = new Event.EventBuilder()
        .subject("1")
        .startDateTime(dateTime)
        .endDateTime(dateTime.plus(2, ChronoUnit.DAYS))
        .isAllDay(true)
        .build();

    try {
      calendar.addEvents(List.of(event2));
    } catch (ConflictException e) {
      assertEquals("Conflicting events", e.getMessage());
    }
  }

  @Test
  public void testAddEventsRollback() {
    ChronoZonedDateTime<LocalDate> dateTime =
        TimeUtils.parseDateTimeString("2025-03-02T02:30", calendar.getTimezone());
    // add existing events to the calendar
    IEvent event1 = new Event.EventBuilder()
        .subject("1")
        .startDateTime(dateTime.plus(1, ChronoUnit.DAYS))
        .endDateTime(dateTime.plus(2, ChronoUnit.DAYS))
        .isAllDay(true)
        .build();
    IEvent event2 = new Event.EventBuilder()
        .subject("2")
        .startDateTime(dateTime.plus(2, ChronoUnit.DAYS))
        .endDateTime(dateTime.plus(3, ChronoUnit.DAYS))
        .isAllDay(true)
        .build();
    IEvent event3 = new Event.EventBuilder()
        .subject("3")
        .startDateTime(dateTime.plus(3, ChronoUnit.DAYS))
        .endDateTime(dateTime.plus(4, ChronoUnit.DAYS))
        .isAllDay(true)
        .build();
    IEvent event4 = new Event.EventBuilder()
        .subject("conflict")
        .startDateTime(dateTime.plus(4, ChronoUnit.DAYS))
        .endDateTime(dateTime.plus(5, ChronoUnit.DAYS))
        .isAllDay(true)
        .build();

    try {
      calendar.addEvents(List.of(event1, event2, event3, event4));
    } catch (Exception e) {
      fail(e.getMessage());
    }

    // new events
    IEvent repeatEvent1 = new Event.EventBuilder()
        .subject("1")
        .startDateTime(dateTime.minus(3, ChronoUnit.DAYS))
        .endDateTime(dateTime.minus(2, ChronoUnit.DAYS))
        .isAllDay(true)
        .build();
    IEvent repeatEvent2 = new Event.EventBuilder()
        .subject("2")
        .startDateTime(dateTime.minus(2, ChronoUnit.DAYS))
        .endDateTime(dateTime.minus(1, ChronoUnit.DAYS))
        .isAllDay(true)
        .build();
    IEvent repeatEvent3 = new Event.EventBuilder()
        .subject("3")
        .startDateTime(dateTime.plus(1, ChronoUnit.DAYS))
        .endDateTime(dateTime.plus(2, ChronoUnit.DAYS))
        .isAllDay(true)
        .build();
    IEvent repeatEvent4 = new Event.EventBuilder()
        .subject("4")
        .startDateTime(dateTime.plus(3, ChronoUnit.DAYS))
        .endDateTime(dateTime.plus(4, ChronoUnit.DAYS))
        .isAllDay(true)
        .build();

    try {
      calendar.addEvents(List.of(repeatEvent1, repeatEvent2, repeatEvent3, repeatEvent4));
    } catch (ConflictException e) {
      assertEquals(4, calendar.getEvents().size());
    }
  }

  @Test
  public void testUpdateTimezone() {
    ChronoZonedDateTime<LocalDate> dateTime =
        TimeUtils.parseDateTimeString("2025-03-02T02:30", calendar.getTimezone());
    // add existing events to the calendar
    IEvent event1 = new Event.EventBuilder()
        .subject("1")
        .startDateTime(dateTime.plus(1, ChronoUnit.DAYS))
        .endDateTime(dateTime.plus(2, ChronoUnit.DAYS))
        .isAllDay(true)
        .build();
    IEvent event2 = new Event.EventBuilder()
        .subject("2")
        .startDateTime(dateTime.plus(2, ChronoUnit.DAYS))
        .endDateTime(dateTime.plus(3, ChronoUnit.DAYS))
        .isAllDay(true)
        .build();
    IEvent event3 = new Event.EventBuilder()
        .subject("3")
        .startDateTime(dateTime.plus(3, ChronoUnit.DAYS))
        .endDateTime(dateTime.plus(4, ChronoUnit.DAYS))
        .isAllDay(true)
        .build();
    IEvent event4 = new Event.EventBuilder()
        .subject("4")
        .startDateTime(dateTime.plus(4, ChronoUnit.DAYS))
        .endDateTime(dateTime.plus(5, ChronoUnit.DAYS))
        .isAllDay(true)
        .build();

    try {
      calendar.addEvents(List.of(event1, event2, event3, event4));
      calendar.edit("timezone", "US/Central");
    } catch (Exception e) {
      fail(e.getMessage());
    }

    ZoneId central = ZoneId.of("US/Central");
    assertEquals(central, calendar.getTimezone());
    for (IEvent event : calendar.getEvents()) {
      assertEquals(central, event.getStartDateTime().getZone());
      assertEquals(central, event.getEndDateTime().getZone());
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidEdit() {
    calendar.edit("invalid", "invalid");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testSetNameNull() {
    calendar.setName(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testSetNameEmpty() {
    calendar.setName("");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testSetNameWhitespace() {
    calendar.setName(" ");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testSetTimezoneNull() {
    calendar.setTimezone(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testSetTimezoneEmpty() {
    calendar.setTimezone("");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testSetTimezoneWhitespace() {
    calendar.setTimezone(" ");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testSetTimezoneInvalid() {
    calendar.setTimezone("invalid");
  }

  @Test
  public void testAddEvents() {
    ChronoZonedDateTime<LocalDate> dateTime =
        TimeUtils.parseDateTimeString("2025-03-02T02:30", calendar.getTimezone());
    // add existing events to the calendar
    IEvent event1 = new Event.EventBuilder()
        .subject("1")
        .startDateTime(dateTime.plus(1, ChronoUnit.DAYS))
        .endDateTime(dateTime.plus(2, ChronoUnit.DAYS))
        .isAllDay(true)
        .build();
    IEvent event2 = new Event.EventBuilder()
        .subject("2")
        .startDateTime(dateTime.plus(2, ChronoUnit.DAYS))
        .endDateTime(dateTime.plus(3, ChronoUnit.DAYS))
        .isAllDay(true)
        .build();
    IEvent event3 = new Event.EventBuilder()
        .subject("3")
        .startDateTime(dateTime.plus(3, ChronoUnit.DAYS))
        .endDateTime(dateTime.plus(4, ChronoUnit.DAYS))
        .isAllDay(true)
        .build();
    IEvent event4 = new Event.EventBuilder()
        .subject("conflict")
        .startDateTime(dateTime.plus(4, ChronoUnit.DAYS))
        .endDateTime(dateTime.plus(5, ChronoUnit.DAYS))
        .isAllDay(true)
        .build();

    try {
      calendar.addEvents(List.of(event1, event2, event3, event4));
    } catch (Exception e) {
      fail(e.getMessage());
    }

    // new events
    IEvent event5 = new Event.EventBuilder()
        .subject("5")
        .startDateTime(dateTime.minus(5, ChronoUnit.DAYS))
        .endDateTime(dateTime.minus(2, ChronoUnit.DAYS))
        .isAllDay(true)
        .build();
    IEvent event6 = new Event.EventBuilder()
        .subject("6")
        .startDateTime(dateTime.minus(2, ChronoUnit.DAYS))
        .endDateTime(dateTime.minus(1, ChronoUnit.DAYS))
        .isAllDay(true)
        .build();
    IEvent event7 = new Event.EventBuilder()
        .subject("7")
        .startDateTime(dateTime.plus(5, ChronoUnit.DAYS))
        .endDateTime(dateTime.plus(6, ChronoUnit.DAYS))
        .isAllDay(true)
        .build();
    IEvent event8 = new Event.EventBuilder()
        .subject("8")
        .startDateTime(dateTime.plus(6, ChronoUnit.DAYS))
        .endDateTime(dateTime.plus(7, ChronoUnit.DAYS))
        .isAllDay(true)
        .build();

    try {
      calendar.addEvents(List.of(event5, event6, event7, event8));
    } catch (Exception e) {
      fail(e.getMessage());
    }

    assertEquals(8, calendar.getEvents().size());
    assertEquals(List.of(event5, event6, event1, event2, event3, event4, event7, event8),
        calendar.getEvents());
  }

  @Test(expected = ConflictException.class)
  public void testAddDuplicateEvents() {
    ChronoZonedDateTime<LocalDate> dateTime =
        TimeUtils.parseDateTimeString("2025-03-02T02:30", calendar.getTimezone());

    IEvent event1 = new Event.EventBuilder()
        .subject("1")
        .startDateTime(dateTime.plus(1, ChronoUnit.DAYS))
        .endDateTime(dateTime.plus(2, ChronoUnit.DAYS))
        .isAllDay(true)
        .build();

    calendar.addEvents(List.of(event1, event1));
  }

  @Test(expected = ConflictException.class)
  public void testEditEventsBySubjectStartDateTime() throws ConflictException {
    calendar.editEventsBySubject("startDateTime", "a",
        "2025-03-02T02:30");
  }

  @Test(expected = ConflictException.class)
  public void testEditEventsBySubjectEndDateTime() throws ConflictException {
    calendar.editEventsBySubject("endDateTime", "a", "2025-03-02T02:30");
  }

  @Test
  public void testEditSingleEventStartDateTime() {
    ChronoZonedDateTime<LocalDate> dateTime =
        TimeUtils.parseDateTimeString("2025-03-02T02:30", calendar.getTimezone());

    IEvent event1 = new Event.EventBuilder()
        .subject("1")
        .startDateTime(dateTime.plus(1, ChronoUnit.HOURS))
        .endDateTime(dateTime.plus(2, ChronoUnit.HOURS))
        .build();

    IEvent event2 = new Event.EventBuilder()
        .subject("1")
        .startDateTime(dateTime.plus(2, ChronoUnit.HOURS))
        .endDateTime(dateTime.plus(3, ChronoUnit.HOURS))
        .build();

    calendar.addEvents(List.of(event1, event2));

    calendar.editSingleEvent("startDateTime", "1", "2025-03-02T03:30",
        "2025-03-02T04:30", "2025-03-02T01:30");

    String expected = "[subject: 1, startDateTime: 2025-03-02T01:30-05:00[US/Eastern], "
        + "endDateTime: 2025-03-02T04:30-05:00[US/Eastern], description: , location: , "
        + "isAllDay: false, isPrivate: false, "
        + "subject: 1, startDateTime: 2025-03-02T04:30-05:00[US/Eastern], "
        + "endDateTime: 2025-03-02T05:30-05:00[US/Eastern], description: , location: , "
        + "isAllDay: false, isPrivate: false]";
    assertEquals(expected, calendar.getEvents().toString());
  }

  @Test
  public void testEditSingleEventEndDateTime() {
    ChronoZonedDateTime<LocalDate> dateTime =
        TimeUtils.parseDateTimeString("2025-03-02T02:30", calendar.getTimezone());

    IEvent event1 = new Event.EventBuilder()
        .subject("1")
        .startDateTime(dateTime.plus(1, ChronoUnit.HOURS))
        .endDateTime(dateTime.plus(2, ChronoUnit.HOURS))
        .build();

    IEvent event2 = new Event.EventBuilder()
        .subject("1")
        .startDateTime(dateTime.plus(2, ChronoUnit.HOURS))
        .endDateTime(dateTime.plus(3, ChronoUnit.HOURS))
        .build();

    calendar.addEvents(List.of(event1, event2));

    calendar.editSingleEvent("endDateTime", "1", "2025-03-02T03:30",
        "2025-03-02T04:30", "2025-03-02T04:00");

    String expected = "[subject: 1, startDateTime: 2025-03-02T03:30-05:00[US/Eastern], "
        + "endDateTime: 2025-03-02T04:00-05:00[US/Eastern], description: , location: , "
        + "isAllDay: false, isPrivate: false, "
        + "subject: 1, startDateTime: 2025-03-02T04:30-05:00[US/Eastern], "
        + "endDateTime: 2025-03-02T05:30-05:00[US/Eastern], description: , location: , "
        + "isAllDay: false, isPrivate: false]";
    assertEquals(expected, calendar.getEvents().toString());
  }

  @Test
  public void testEditSingleEventEndDateTimeConflict() {
    ChronoZonedDateTime<LocalDate> dateTime =
        TimeUtils.parseDateTimeString("2025-03-02T02:30", calendar.getTimezone());

    IEvent event1 = new Event.EventBuilder()
        .subject("1")
        .startDateTime(dateTime.plus(1, ChronoUnit.HOURS))
        .endDateTime(dateTime.plus(2, ChronoUnit.HOURS))
        .build();

    IEvent event2 = new Event.EventBuilder()
        .subject("1")
        .startDateTime(dateTime.plus(2, ChronoUnit.HOURS))
        .endDateTime(dateTime.plus(3, ChronoUnit.HOURS))
        .build();

    calendar.addEvents(List.of(event1, event2));

    try {
      calendar.editSingleEvent("endDateTime", "1", "2025-03-02T03:30",
          "2025-03-02T04:30", "2025-03-02T05:00");
    } catch (ConflictException e) {
      assertEquals("Event conflicts with an existing event", e.getMessage());
    }

    String expected = "[subject: 1, startDateTime: 2025-03-02T03:30-05:00[US/Eastern], "
        + "endDateTime: 2025-03-02T04:30-05:00[US/Eastern], description: , location: , "
        + "isAllDay: false, isPrivate: false, "
        + "subject: 1, startDateTime: 2025-03-02T04:30-05:00[US/Eastern], "
        + "endDateTime: 2025-03-02T05:30-05:00[US/Eastern], description: , location: , "
        + "isAllDay: false, isPrivate: false]";
    assertEquals(expected, calendar.getEvents().toString());
  }

  @Test
  public void testEditSingleEventStartDateTimeConflict() {
    ChronoZonedDateTime<LocalDate> dateTime =
        TimeUtils.parseDateTimeString("2025-03-02T02:30", calendar.getTimezone());

    IEvent event1 = new Event.EventBuilder()
        .subject("1")
        .startDateTime(dateTime.plus(1, ChronoUnit.HOURS))
        .endDateTime(dateTime.plus(2, ChronoUnit.HOURS))
        .build();

    IEvent event2 = new Event.EventBuilder()
        .subject("1")
        .startDateTime(dateTime.plus(2, ChronoUnit.HOURS))
        .endDateTime(dateTime.plus(3, ChronoUnit.HOURS))
        .build();

    calendar.addEvents(List.of(event1, event2));

    try {
      calendar.editSingleEvent("startDateTime", "1", "2025-03-02T04:30",
          "2025-03-02T05:30", "2025-03-02T04:00");
    } catch (ConflictException e) {
      assertEquals("Event conflicts with an existing event", e.getMessage());
    }

    String expected = "[subject: 1, startDateTime: 2025-03-02T03:30-05:00[US/Eastern], "
        + "endDateTime: 2025-03-02T04:30-05:00[US/Eastern], description: , location: , "
        + "isAllDay: false, isPrivate: false, "
        + "subject: 1, startDateTime: 2025-03-02T04:30-05:00[US/Eastern], "
        + "endDateTime: 2025-03-02T05:30-05:00[US/Eastern], description: , location: , "
        + "isAllDay: false, isPrivate: false]";
    assertEquals(expected, calendar.getEvents().toString());
  }

  @Test
  public void testEditEventsFromStartDateTimeConflict() {
    ChronoZonedDateTime<LocalDate> dateTime =
        TimeUtils.parseDateTimeString("2025-03-02T02:30", calendar.getTimezone());

    IRepeatingEvent repeatingEvent = new RepeatingEvent.RepeatingEventBuilder()
        .subject("1")
        .startDateTime(dateTime)
        .endDateTime(dateTime.plus(1, ChronoUnit.HOURS))
        .repeatDays(TimeUtils.getDaysOfWeek(new char[]{'M', 'W', 'F'}))
        .repeatNumber(3)
        .build();
    List<IEvent> events = repeatingEvent.repeatNTimes();

    IEvent event = new Event.EventBuilder()
        .subject("2")
        .startDateTime(dateTime.plus(9, ChronoUnit.DAYS))
        .endDateTime(dateTime.plus(10, ChronoUnit.DAYS))
        .isAllDay(true)
        .build();

    calendar.addEvents(events);
    calendar.addEvents(List.of(event));

    assertEquals(5, calendar.getEvents().size());
    try {
      calendar.editEventsFromStartDateTime("repeatNumber", "1",
          "2025-03-05T02:30", "7");
    } catch (ConflictException e) {
      assertEquals("Conflicting events", e.getMessage());
      assertEquals(5, calendar.getEvents().size());
    }
  }

  @Test
  public void testEditBySubject() {
    ChronoZonedDateTime<LocalDate> dateTime =
        TimeUtils.parseDateTimeString("2025-03-02T02:30", calendar.getTimezone());
    // add existing events to the calendar
    IEvent event1 = new Event.EventBuilder()
        .subject("1")
        .startDateTime(dateTime.plus(1, ChronoUnit.DAYS))
        .endDateTime(dateTime.plus(2, ChronoUnit.DAYS))
        .isAllDay(true)
        .build();
    IEvent event2 = new Event.EventBuilder()
        .subject("1")
        .startDateTime(dateTime.plus(2, ChronoUnit.DAYS))
        .endDateTime(dateTime.plus(3, ChronoUnit.DAYS))
        .isAllDay(true)
        .build();
    IEvent event3 = new Event.EventBuilder()
        .subject("2")
        .startDateTime(dateTime.plus(3, ChronoUnit.DAYS))
        .endDateTime(dateTime.plus(4, ChronoUnit.DAYS))
        .isAllDay(true)
        .build();
    IEvent event4 = new Event.EventBuilder()
        .subject("1")
        .startDateTime(dateTime.plus(4, ChronoUnit.DAYS))
        .endDateTime(dateTime.plus(5, ChronoUnit.DAYS))
        .isAllDay(true)
        .build();

    try {
      calendar.addEvents(List.of(event1, event2, event3, event4));
      calendar.editEventsBySubject("location", "1", "loc");
    } catch (Exception e) {
      fail(e.getMessage());
    }

    String expected = "[subject: 1, startDateTime: 2025-03-03T02:30-05:00[US/Eastern], "
        + "endDateTime: 2025-03-04T02:30-05:00[US/Eastern], description: , location: loc, "
        + "isAllDay: true, isPrivate: false, "
        + "subject: 1, startDateTime: 2025-03-04T02:30-05:00[US/Eastern], "
        + "endDateTime: 2025-03-05T02:30-05:00[US/Eastern], description: , location: loc, "
        + "isAllDay: true, isPrivate: false, "
        + "subject: 2, startDateTime: 2025-03-05T02:30-05:00[US/Eastern], "
        + "endDateTime: 2025-03-06T02:30-05:00[US/Eastern], description: , location: , "
        + "isAllDay: true, isPrivate: false, "
        + "subject: 1, startDateTime: 2025-03-06T02:30-05:00[US/Eastern], "
        + "endDateTime: 2025-03-07T02:30-05:00[US/Eastern], description: , location: loc, "
        + "isAllDay: true, isPrivate: false]";
    assertEquals(expected, calendar.getEvents().toString());
  }

  @Test
  public void testEditEventsBySubjectConflict() {
    ChronoZonedDateTime<LocalDate> dateTime =
        TimeUtils.parseDateTimeString("2025-03-02T02:30", calendar.getTimezone());

    IRepeatingEvent repeatingEvent = new RepeatingEvent.RepeatingEventBuilder()
        .subject("1")
        .startDateTime(dateTime)
        .endDateTime(dateTime.plus(1, ChronoUnit.HOURS))
        .repeatDays(TimeUtils.getDaysOfWeek(new char[]{'M', 'W', 'F'}))
        .repeatNumber(3)
        .build();
    List<IEvent> events = repeatingEvent.repeatNTimes();

    IEvent event = new Event.EventBuilder()
        .subject("2")
        .startDateTime(dateTime.plus(9, ChronoUnit.DAYS))
        .endDateTime(dateTime.plus(10, ChronoUnit.DAYS))
        .isAllDay(true)
        .build();

    calendar.addEvents(events);
    calendar.addEvents(List.of(event));

    assertEquals(5, calendar.getEvents().size());
    try {
      calendar.editEventsBySubject("repeatNumber", "1", "7");
    } catch (ConflictException e) {
      assertEquals("Conflicting events", e.getMessage());
      assertEquals(5, calendar.getEvents().size());
    }
  }

  @Test
  public void testEditRepeatingEventsBySubject() {
    ChronoZonedDateTime<LocalDate> dateTime =
        TimeUtils.parseDateTimeString("2025-03-02T02:30", calendar.getTimezone());

    IRepeatingEvent repeatingEvent = new RepeatingEvent.RepeatingEventBuilder()
        .subject("1")
        .startDateTime(dateTime)
        .endDateTime(dateTime.plus(1, ChronoUnit.HOURS))
        .repeatDays(TimeUtils.getDaysOfWeek(new char[]{'M', 'W', 'F'}))
        .repeatNumber(3)
        .build();
    List<IEvent> events = repeatingEvent.repeatNTimes();
    calendar.addEvents(events);

    assertEquals(4, calendar.getEvents().size());
    try {
      calendar.editEventsBySubject("repeatNumber", "1", "7");
    } catch (Exception e) {
      fail(e.getMessage());
    }

    assertEquals(8, calendar.getEvents().size());
  }
}