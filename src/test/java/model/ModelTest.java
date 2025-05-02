package model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import calendar.IRepeatingEvent;
import calendar.RepeatingEvent;
import java.time.DayOfWeek;
import java.util.Set;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.chrono.ChronoZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

import calendar.Calendar;
import calendar.Event;
import calendar.ICalendar;
import calendar.IEvent;
import utils.TimeUtils;

/**
 * A JUnit test class for testing the Model class.
 */
public class ModelTest {

  private IModel model;
  private static ZoneId ZONE_ID;

  @BeforeClass
  public static void setUpBeforeClass() {
    ZONE_ID = ZoneId.of("US/Eastern");
  }

  @Before
  public void setUp() {
    model = new Model();
    ICalendar calendar = new Calendar("test", "US/Eastern");
    model.addCalendar(calendar);
    model.useCalendar("test");
  }

  @Test
  public void testGetCurrentCalendar() {
    IModel test = new Model();
    try {
      test.getCurrentCalendar();
    } catch (IllegalStateException e) {
      assertEquals("No calendar in use", e.getMessage());
    }
  }

  @Test
  public void testAddCalendar() {
    model.addCalendar(new Calendar("new", "US/Eastern"));
    model.useCalendar("new");
    assertEquals("new", model.getCurrentCalendar().getName());
  }

  @Test
  public void testAddCalendarDuplicate() {
    model.addCalendar(new Calendar("new", "US/Eastern"));
    try {
      model.addCalendar(new Calendar("new", "US/Eastern"));
    } catch (IllegalArgumentException e) {
      assertEquals("Calendar already exists", e.getMessage());
    }
  }

  @Test
  public void testUseCalendarInvalid() {
    try {
      model.useCalendar("invalid");
    } catch (IllegalArgumentException e) {
      assertEquals("Calendar not found", e.getMessage());
    }
  }

  @Test
  public void testEditCalendarName() {
    try {
      model.editCalendar("test", "name", "new");
      assertEquals("new", model.getCurrentCalendar().getName());
    } catch (Exception e) {
      System.err.println(e.getMessage());
      fail();
    }
  }

  @Test
  public void testEditCalendarTimezone() {
    try {
      assertEquals(ZoneId.of("US/Eastern"), model.getCurrentCalendar().getTimezone());
      String zone = "US/Central";

      model.editCalendar("test", "timezone", zone);
      assertEquals(ZoneId.of("US/Central"), model.getCurrentCalendar().getTimezone());
    } catch (Exception e) {
      System.err.println(e.getMessage());
      fail();
    }
  }

  @Test
  public void testAddAllDayEventToEmptyList() {
    ChronoZonedDateTime<LocalDate> dateTime =
        TimeUtils.parseDateTimeString("2025-01-01T10:00", ZONE_ID);

    IEvent event = new Event.EventBuilder()
        .subject("1")
        .startDateTime(dateTime)
        .endDateTime(dateTime.plus(1, ChronoUnit.DAYS))
        .isAllDay(true)
        .build();
    try {
      model.addEvents(List.of(event));

      List<IEvent> events = model.getEventsOnDate(dateTime);
      assertEquals(1, events.size());
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  @Test
  public void testAddMultipleAllDayEventsInOrder() {
    ChronoZonedDateTime<LocalDate> dateTime =
        TimeUtils.parseDateTimeString("2025-01-01T10:00", ZONE_ID);

    IEvent event1 = new Event.EventBuilder()
        .subject("1")
        .startDateTime(dateTime)
        .endDateTime(dateTime.plus(1, ChronoUnit.DAYS))
        .isAllDay(true)
        .build();
    IEvent event2 = new Event.EventBuilder()
        .subject("2")
        .startDateTime(dateTime.plus(1, ChronoUnit.DAYS))
        .endDateTime(dateTime.plus(2, ChronoUnit.DAYS))
        .isAllDay(true)
        .build();
    IEvent event3 = new Event.EventBuilder()
        .subject("3")
        .startDateTime(dateTime.plus(2, ChronoUnit.DAYS))
        .endDateTime(dateTime.plus(3, ChronoUnit.DAYS))
        .isAllDay(true)
        .build();

    try {
      model.addEvents(List.of(event1, event2, event3));

      List<IEvent> expectedEvents = Arrays.asList(event1, event2, event3);
      List<IEvent> events = model.getEventsInRange(dateTime,
          dateTime.plus(2, ChronoUnit.DAYS));
      assertEquals(expectedEvents, events);
    } catch (Exception e) {
      System.err.println(e.getMessage());
      fail(e.getMessage());
    }
  }

  @Test
  public void testAddMultipleAllDayEventsInReverseOrder() {
    ChronoZonedDateTime<LocalDate> dateTime =
        TimeUtils.parseDateTimeString("2025-01-01T10:00", ZONE_ID);

    IEvent event1 = new Event.EventBuilder()
        .subject("1")
        .startDateTime(dateTime)
        .endDateTime(dateTime.plus(1, ChronoUnit.DAYS))
        .isAllDay(true)
        .build();
    IEvent event2 = new Event.EventBuilder()
        .subject("2")
        .startDateTime(dateTime.plus(1, ChronoUnit.DAYS))
        .endDateTime(dateTime.plus(2, ChronoUnit.DAYS))
        .isAllDay(true)
        .build();
    IEvent event3 = new Event.EventBuilder()
        .subject("3")
        .startDateTime(dateTime.plus(2, ChronoUnit.DAYS))
        .endDateTime(dateTime.plus(3, ChronoUnit.DAYS))
        .isAllDay(true)
        .build();

    try {
      model.addEvents(List.of(event3, event2, event1));

      List<IEvent> expectedEvents = Arrays.asList(event1, event2, event3);
      List<IEvent> events = model.getEventsInRange(dateTime,
          dateTime.plus(3, ChronoUnit.DAYS));
      assertEquals(expectedEvents, events);
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  @Test
  public void testAddConflictingEvents() {
    ChronoZonedDateTime<LocalDate> dateTime =
        TimeUtils.parseDateTimeString("2025-01-01T10:00", ZONE_ID);

    IEvent event1 = new Event.EventBuilder()
        .subject("1")
        .startDateTime(dateTime)
        .endDateTime(dateTime.plus(3, ChronoUnit.HOURS))
        .build();
    IEvent event2 = new Event.EventBuilder()
        .subject("2")
        .startDateTime(dateTime.plus(1, ChronoUnit.HOURS))
        .endDateTime(dateTime.plus(4, ChronoUnit.HOURS))
        .build();

    try {
      model.addEvents(List.of(event1, event2));
    } catch (Exception e) {
      assertEquals("Event conflicts with an existing event", e.getMessage());
    }
  }

  @Test
  public void testAddConflictingBackToBackEvents() {
    ChronoZonedDateTime<LocalDate> dateTime =
        TimeUtils.parseDateTimeString("2025-01-01T10:00", ZONE_ID);

    IEvent event1 = new Event.EventBuilder()
        .subject("1")
        .startDateTime(dateTime)
        .endDateTime(dateTime.plus(3, ChronoUnit.HOURS))
        .build();
    IEvent event2 = new Event.EventBuilder()
        .subject("2")
        .startDateTime(dateTime.plus(2, ChronoUnit.HOURS))
        .endDateTime(dateTime.plus(4, ChronoUnit.HOURS))
        .build();

    try {
      model.addEvents(List.of(event1, event2));
    } catch (Exception e) {
      assertEquals("Event conflicts with an existing event", e.getMessage());
    }
  }

  @Test
  public void testAddConflictingAllDayEvents() {
    ChronoZonedDateTime<LocalDate> dateTime =
        TimeUtils.parseDateTimeString("2025-01-01T10:00", ZONE_ID);

    IEvent event1 = new Event.EventBuilder()
        .subject("1")
        .startDateTime(dateTime)
        .endDateTime(dateTime.plus(1, ChronoUnit.DAYS))
        .isAllDay(true)
        .build();
    IEvent event2 = new Event.EventBuilder()
        .subject("2")
        .startDateTime(dateTime.plus(1, ChronoUnit.HOURS))
        .endDateTime(dateTime.plus(1, ChronoUnit.DAYS))
        .isAllDay(true)
        .build();

    try {
      model.addEvents(List.of(event1, event2));
    } catch (Exception e) {
      assertEquals("Event conflicts with an existing event", e.getMessage());
    }
  }

  @Test
  public void testAddMultipleConflictingAllDayEvents() {
    ChronoZonedDateTime<LocalDate> dateTime =
        TimeUtils.parseDateTimeString("2025-01-01T10:00", ZONE_ID);
    IEvent event1 = new Event.EventBuilder()
        .subject("1")
        .startDateTime(dateTime)
        .endDateTime(dateTime.plus(1, ChronoUnit.DAYS))
        .isAllDay(true)
        .build();
    IEvent event2 = new Event.EventBuilder()
        .subject("2")
        .startDateTime(dateTime.plus(1, ChronoUnit.HOURS))
        .endDateTime(dateTime.plus(1, ChronoUnit.DAYS))
        .isAllDay(true)
        .build();
    IEvent event3 = new Event.EventBuilder()
        .subject("2")
        .startDateTime(dateTime.plus(1, ChronoUnit.HOURS))
        .endDateTime(dateTime.plus(1, ChronoUnit.DAYS))
        .isAllDay(true)
        .build();

    try {
      model.addEvents(List.of(event1, event2, event3));
    } catch (Exception e) {
      assertEquals("Event conflicts with an existing event", e.getMessage());
    }
  }

  @Test
  public void testAddDuplicateEvents() {
    ChronoZonedDateTime<LocalDate> dateTime =
        TimeUtils.parseDateTimeString("2025-01-01T10:00", ZONE_ID);
    IEvent event1 = new Event.EventBuilder()
        .subject("1")
        .startDateTime(dateTime)
        .endDateTime(dateTime.plus(1, ChronoUnit.HOURS))
        .build();
    IEvent event2 = new Event.EventBuilder()
        .subject("1")
        .startDateTime(dateTime)
        .endDateTime(dateTime.plus(1, ChronoUnit.HOURS))
        .build();

    try {
      model.addEvents(List.of(event1, event2));
    } catch (Exception e) {
      assertEquals("Event conflicts with an existing event", e.getMessage());
    }
  }

  @Test
  public void testAddContainedEvent() {
    ChronoZonedDateTime<LocalDate> dateTime =
        TimeUtils.parseDateTimeString("2025-01-01T10:00", ZONE_ID);

    IEvent event1 = new Event.EventBuilder()
        .subject("1")
        .startDateTime(dateTime)
        .endDateTime(dateTime.plus(5, ChronoUnit.HOURS))
        .build();
    IEvent event2 = new Event.EventBuilder()
        .subject("1")
        .startDateTime(dateTime.plus(1, ChronoUnit.HOURS))
        .endDateTime(dateTime.plus(2, ChronoUnit.HOURS))
        .build();

    try {
      model.addEvents(List.of(event1, event2));
    } catch (Exception e) {
      assertEquals("Event conflicts with an existing event", e.getMessage());
    }
  }

  @Test
  public void testGetStatusBeforeStartDateTimeBusy() {
    ChronoZonedDateTime<LocalDate> dateTime =
        TimeUtils.parseDateTimeString("2025-01-01T10:00", ZONE_ID);

    IEvent event = new Event.EventBuilder()
        .subject("Meeting")
        .startDateTime(dateTime)
        .endDateTime(dateTime.plus(3, ChronoUnit.HOURS))
        .location("Location")
        .build();

    try {
      model.addEvents(List.of(event));
      assertEquals(Status.BUSY.getValue(), model.getStatus("2025-01-01T11:00"));
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  @Test
  public void testGetStatusOnStartDateTimeAvailable() {
    ChronoZonedDateTime<LocalDate> dateTime =
        TimeUtils.parseDateTimeString("2025-01-01T10:00", ZONE_ID);

    IEvent event = new Event.EventBuilder()
        .subject("Meeting")
        .startDateTime(dateTime)
        .endDateTime(dateTime.plus(2, ChronoUnit.HOURS))
        .location("Location")
        .build();

    try {
      model.addEvents(List.of(event));
      assertEquals(Status.AVAILABLE.getValue(), model.getStatus("2025-01-03T10:00"));
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  @Test
  public void testGetStatusOnEndDateTimeAvailable() {
    ChronoZonedDateTime<LocalDate> dateTime =
        TimeUtils.parseDateTimeString("2025-01-01T10:00", ZONE_ID);

    IEvent event = new Event.EventBuilder()
        .subject("Meeting")
        .startDateTime(dateTime)
        .endDateTime(dateTime.plus(2, ChronoUnit.HOURS))
        .location("Location")
        .build();

    try {
      model.addEvents(List.of(event));
      assertEquals(Status.AVAILABLE.getValue(), model.getStatus("2025-01-01T12:00"));
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  @Test
  public void testGetStatusBetweenEvents() {
    ChronoZonedDateTime<LocalDate> dateTime =
        TimeUtils.parseDateTimeString("2025-01-01T10:00", ZONE_ID);

    IEvent event1 = new Event.EventBuilder()
        .subject("Meeting")
        .startDateTime(dateTime)
        .endDateTime(dateTime.plus(2, ChronoUnit.HOURS))
        .location("Location")
        .build();
    IEvent event2 = new Event.EventBuilder()
        .subject("Meeting")
        .startDateTime(dateTime.plus(2, ChronoUnit.HOURS))
        .endDateTime(dateTime.plus(3, ChronoUnit.HOURS))
        .location("Location")
        .build();

    try {
      model.addEvents(List.of(event1, event2));

      assertEquals(Status.BUSY.getValue(), model.getStatus("2025-01-01T12:00"));
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  @Test
  public void testGetStatusOnExactStartDateTime() {
    ChronoZonedDateTime<LocalDate> dateTime =
        TimeUtils.parseDateTimeString("2025-01-01T10:00", ZONE_ID);

    IEvent event = new Event.EventBuilder()
        .subject("Team Meeting")
        .startDateTime(dateTime)
        .endDateTime(dateTime.plus(5, ChronoUnit.HOURS))
        .location("Boston")
        .build();

    try {
      model.addEvents(List.of(event));
      assertEquals(Status.AVAILABLE.getValue(), model.getStatus("2025-01-01T15:00"));
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  @Test
  public void testGetStatusWithNoEvents() {
    String expected = "Available";

    assertEquals(expected, model.getStatus("2025-01-01T10:00"));
  }

  @Test
  public void testEditEventLocationForAllEventsWithSameEventName() {
    ChronoZonedDateTime<LocalDate> dateTime =
        TimeUtils.parseDateTimeString("2025-01-01T10:00", ZONE_ID);
    String subject = "1";

    IEvent event1 = new Event.EventBuilder()
        .subject(subject)
        .startDateTime(dateTime)
        .endDateTime(dateTime.plus(1, ChronoUnit.HOURS))
        .location("Location 1")
        .build();

    IEvent event2 = new Event.EventBuilder()
        .subject(subject)
        .startDateTime(dateTime.plus(1, ChronoUnit.DAYS))
        .endDateTime(dateTime.plus(1, ChronoUnit.DAYS)
            .plus(1, ChronoUnit.HOURS))
        .location("Location 2")
        .build();

    try {
      model.addEvents(List.of(event1, event2));
      model.editEventsBySubject("location", subject, "Location 3");

      List<IEvent> events = model.getEventsInRange(dateTime,
          dateTime.plus(2, ChronoUnit.DAYS));

      String expected = "[subject: 1, startDateTime: 2025-01-01T10:00-05:00[US/Eastern], "
          + "endDateTime: 2025-01-01T11:00-05:00[US/Eastern], description: , "
          + "location: Location 3, isAllDay: false, isPrivate: false, "
          + "subject: 1, startDateTime: 2025-01-02T10:00-05:00[US/Eastern], "
          + "endDateTime: 2025-01-02T11:00-05:00[US/Eastern], description: , "
          + "location: Location 3, isAllDay: false, isPrivate: false]";
      assertEquals(expected, events.toString());
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  @Test
  public void testEditEventNameForAllEventsWithSameEventName() {
    ChronoZonedDateTime<LocalDate> dateTime =
        TimeUtils.parseDateTimeString("2025-01-01T10:00", ZONE_ID);
    String subject = "1";

    IEvent event1 = new Event.EventBuilder()
        .subject(subject)
        .startDateTime(dateTime)
        .endDateTime(dateTime.plus(1, ChronoUnit.HOURS))
        .location("Location 1")
        .build();

    IEvent event2 = new Event.EventBuilder()
        .subject(subject)
        .startDateTime(dateTime.plus(1, ChronoUnit.DAYS))
        .endDateTime(dateTime.plus(1, ChronoUnit.DAYS)
            .plus(1, ChronoUnit.HOURS))
        .location("Location 2")
        .build();

    IEvent event3 = new Event.EventBuilder()
        .subject("3")
        .startDateTime(dateTime.plus(2, ChronoUnit.DAYS))
        .endDateTime(dateTime.plus(2, ChronoUnit.DAYS)
            .plus(1, ChronoUnit.HOURS))
        .location("Location 2")
        .build();

    try {
      model.addEvents(List.of(event1, event2, event3));

      String expected = "new";
      model.editEventsBySubject("subject", subject, expected);

      assertEquals(expected, event1.getSubject());
      assertEquals(expected, event2.getSubject());
      assertEquals("3", event3.getSubject());
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  @Test
  public void testEditEventsWithGivenStartDateTimeAndEndDateTime() {
    ChronoZonedDateTime<LocalDate> dateTime =
        TimeUtils.parseDateTimeString("2025-01-01T10:00", ZONE_ID);
    String subject = "1";

    IEvent event1 = new Event.EventBuilder()
        .subject(subject)
        .startDateTime(dateTime)
        .endDateTime(dateTime.plus(1, ChronoUnit.HOURS))
        .location("Location 1")
        .build();

    IEvent event2 = new Event.EventBuilder()
        .subject(subject)
        .startDateTime(dateTime.plus(1, ChronoUnit.DAYS))
        .endDateTime(dateTime.plus(1, ChronoUnit.DAYS)
            .plus(1, ChronoUnit.HOURS))
        .location("Location 2")
        .build();

    IEvent event3 = new Event.EventBuilder()
        .subject("3")
        .startDateTime(dateTime.plus(2, ChronoUnit.DAYS))
        .endDateTime(dateTime.plus(2, ChronoUnit.DAYS)
            .plus(1, ChronoUnit.HOURS))
        .location("Location 3")
        .build();

    try {
      model.addEvents(List.of(event1, event2, event3));

      model.editSingleEvent("location", subject, "2025-01-02T10:00",
          "2025-01-02T11:00", "new location");

      String expected = "[subject: 1, startDateTime: 2025-01-01T10:00-05:00[US/Eastern], "
          + "endDateTime: 2025-01-01T11:00-05:00[US/Eastern], description: , "
          + "location: Location 1, isAllDay: false, isPrivate: false, "
          + "subject: 1, startDateTime: 2025-01-02T10:00-05:00[US/Eastern], "
          + "endDateTime: 2025-01-02T11:00-05:00[US/Eastern], description: , "
          + "location: new location, isAllDay: false, isPrivate: false, "
          + "subject: 3, startDateTime: 2025-01-03T10:00-05:00[US/Eastern], "
          + "endDateTime: 2025-01-03T11:00-05:00[US/Eastern], description: , "
          + "location: Location 3, isAllDay: false, isPrivate: false]";
      assertEquals(expected, model.getEventsInRange(dateTime,
          dateTime.plus(3, ChronoUnit.DAYS)).toString());
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  @Test
  public void testEditEventsWithGivenStartDateTime() {
    ChronoZonedDateTime<LocalDate> dateTime =
        TimeUtils.parseDateTimeString("2025-01-01T10:00", ZONE_ID);
    String subject = "1";

    IEvent event1 = new Event.EventBuilder()
        .subject(subject)
        .startDateTime(dateTime)
        .endDateTime(dateTime.plus(1, ChronoUnit.HOURS))
        .location("Location 1")
        .build();

    IEvent event2 = new Event.EventBuilder()
        .subject(subject)
        .startDateTime(dateTime.plus(1, ChronoUnit.DAYS))
        .endDateTime(dateTime.plus(1, ChronoUnit.DAYS)
            .plus(1, ChronoUnit.HOURS))
        .location("Location 2")
        .build();

    IEvent event3 = new Event.EventBuilder()
        .subject("3")
        .startDateTime(dateTime.plus(2, ChronoUnit.DAYS))
        .endDateTime(dateTime.plus(2, ChronoUnit.DAYS)
            .plus(1, ChronoUnit.HOURS))
        .location("Location 3")
        .build();

    try {
      model.addEvents(List.of(event1, event2, event3));

      model.editEventsFromStartDateTime("location", subject,
          "2025-01-02T10:00", "new location");

      String expected = "[subject: 1, startDateTime: 2025-01-01T10:00-05:00[US/Eastern], "
          + "endDateTime: 2025-01-01T11:00-05:00[US/Eastern], description: , "
          + "location: Location 1, isAllDay: false, isPrivate: false, "
          + "subject: 1, startDateTime: 2025-01-02T10:00-05:00[US/Eastern], "
          + "endDateTime: 2025-01-02T11:00-05:00[US/Eastern], description: , "
          + "location: new location, isAllDay: false, isPrivate: false, "
          + "subject: 3, startDateTime: 2025-01-03T10:00-05:00[US/Eastern], "
          + "endDateTime: 2025-01-03T11:00-05:00[US/Eastern], description: , "
          + "location: Location 3, isAllDay: false, isPrivate: false]";
      assertEquals(expected, model.getEventsInRange(dateTime,
          dateTime.plus(3, ChronoUnit.DAYS)).toString());
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  @Test
  public void testGetEventsOnDate() {
    ChronoZonedDateTime<LocalDate> dateTime1 =
        TimeUtils.parseDateTimeString("2025-01-03T23:00", ZONE_ID);
    IEvent event1 = new Event.EventBuilder()
        .subject("1")
        .startDateTime(dateTime1)
        .endDateTime(dateTime1.plus(3, ChronoUnit.HOURS))
        .build();

    ChronoZonedDateTime<LocalDate> dateTime2 =
        TimeUtils.parseDateTimeString("2025-01-04T23:00", ZONE_ID);
    IEvent event2 = new Event.EventBuilder()
        .subject("2")
        .startDateTime(dateTime2)
        .endDateTime(dateTime2.plus(2, ChronoUnit.HOURS))
        .build();

    ChronoZonedDateTime<LocalDate> dateTime3 =
        TimeUtils.parseDateTimeString("2025-01-06T00:00", ZONE_ID);
    IEvent event3 = new Event.EventBuilder()
        .subject("3")
        .startDateTime(dateTime3)
        .endDateTime(dateTime3.plus(1, ChronoUnit.HOURS))
        .build();

    ChronoZonedDateTime<LocalDate> dateTime4 =
        TimeUtils.parseDateTimeString("2025-01-04T08:00", ZONE_ID);
    IEvent event4 = new Event.EventBuilder()
        .subject("4")
        .startDateTime(dateTime4)
        .endDateTime(dateTime4.plus(1, ChronoUnit.HOURS))
        .build();

    try {
      model.addEvents(List.of(event1, event2, event3, event4));

      List<IEvent> events = model.getEventsOnDate(dateTime4);
      String expected = "[subject: 1, startDateTime: 2025-01-03T23:00-05:00[US/Eastern], "
          + "endDateTime: 2025-01-04T02:00-05:00[US/Eastern], description: , location: , "
          + "isAllDay: false, isPrivate: false, subject: 4, "
          + "startDateTime: 2025-01-04T08:00-05:00[US/Eastern], "
          + "endDateTime: 2025-01-04T09:00-05:00[US/Eastern], description: , location: , "
          + "isAllDay: false, isPrivate: false, subject: 2, "
          + "startDateTime: 2025-01-04T23:00-05:00[US/Eastern], "
          + "endDateTime: 2025-01-05T01:00-05:00[US/Eastern], description: , location: , "
          + "isAllDay: false, isPrivate: false]";
      assertEquals(expected, events.toString());
    } catch (Exception e) {
      System.err.println(e.getMessage());
      fail();
    }
  }

  @Test
  public void testGetEventsOnDateSpanningEvent() {
    ChronoZonedDateTime<LocalDate> dateTime1 =
        TimeUtils.parseDateTimeString("2025-01-03T23:00", ZONE_ID);
    IEvent event = new Event.EventBuilder()
        .subject("1")
        .startDateTime(dateTime1)
        .endDateTime(dateTime1.plus(2, ChronoUnit.DAYS)
            .plus(3, ChronoUnit.HOURS))
        .build();

    try {
      model.addEvents(List.of(event));
      ChronoZonedDateTime<LocalDate> dateTime =
          TimeUtils.parseDateTimeString("2025-01-04T08:00", ZONE_ID);
      List<IEvent> events = model.getEventsOnDate(dateTime);

      String expected = "[subject: 1, startDateTime: 2025-01-03T23:00-05:00[US/Eastern], "
          + "endDateTime: 2025-01-06T02:00-05:00[US/Eastern], description: , location: , "
          + "isAllDay: false, isPrivate: false]";
      assertEquals(expected, events.toString());
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  @Test
  public void testGetEventsByStartDateNotAllEventsFetched() {
    ChronoZonedDateTime<LocalDate> dateTime =
        TimeUtils.parseDateTimeString("2025-01-01T10:00", ZONE_ID);

    IEvent event1 = new Event.EventBuilder()
        .subject("test")
        .startDateTime(dateTime)
        .endDateTime(dateTime.plus(6, ChronoUnit.HOURS))
        .location("Boston")
        .description("Workshop")
        .build();

    IEvent event2 = new Event.EventBuilder()
        .subject("test")
        .startDateTime(dateTime.plus(1, ChronoUnit.DAYS)
            .plus(2, ChronoUnit.HOURS))
        .endDateTime(dateTime.plus(1, ChronoUnit.DAYS)
            .plus(3, ChronoUnit.HOURS))
        .location("New York")
        .description("Workshop")
        .build();

    IEvent event3 = new Event.EventBuilder()
        .subject("test")
        .startDateTime(dateTime.plus(1, ChronoUnit.DAYS)
            .plus(3, ChronoUnit.HOURS))
        .endDateTime(dateTime.plus(1, ChronoUnit.DAYS)
            .plus(5, ChronoUnit.HOURS))
        .location("Chicago")
        .description("Workshop")
        .build();

    try {
      model.addEvents(List.of(event1, event2, event3));

      String expected = "[subject: test, startDateTime: 2025-01-02T12:00-05:00[US/Eastern], "
          + "endDateTime: 2025-01-02T13:00-05:00[US/Eastern], description: Workshop, "
          + "location: New York, isAllDay: false, isPrivate: false, "
          + "subject: test, startDateTime: 2025-01-02T13:00-05:00[US/Eastern], "
          + "endDateTime: 2025-01-02T15:00-05:00[US/Eastern], description: Workshop, "
          + "location: Chicago, isAllDay: false, isPrivate: false]";
      assertEquals(expected,
          model.getEventsOnDate(dateTime.plus(1, ChronoUnit.DAYS)).toString());
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  @Test
  public void testGetEventsInRange() {
    ChronoZonedDateTime<LocalDate> startDateTime =
        TimeUtils.parseDateTimeString("2025-01-01T10:00", ZONE_ID);
    IEvent event1 = new Event.EventBuilder()
        .subject("test")
        .startDateTime(startDateTime)
        .endDateTime(startDateTime.plus(6, ChronoUnit.HOURS))
        .location("New York")
        .build();

    ChronoZonedDateTime<LocalDate> startDateTime2 =
        TimeUtils.parseDateTimeString("2025-01-02T10:00", ZONE_ID);
    IEvent event2 = new Event.EventBuilder()
        .subject("test")
        .startDateTime(startDateTime2)
        .endDateTime(startDateTime2.plus(6, ChronoUnit.HOURS))
        .location("Cincinnati")
        .build();

    ChronoZonedDateTime<LocalDate> start =
        TimeUtils.parseDateTimeString("2025-01-01T10:00", ZONE_ID);
    ChronoZonedDateTime<LocalDate> end =
        TimeUtils.parseDateTimeString("2025-01-03T12:00", ZONE_ID);
    try {
      model.addEvents(List.of(event1, event2));

      String expected = "[subject: test, startDateTime: 2025-01-01T10:00-05:00[US/Eastern], "
          + "endDateTime: 2025-01-01T16:00-05:00[US/Eastern], description: , "
          + "location: New York, isAllDay: false, isPrivate: false, subject: test, "
          + "startDateTime: 2025-01-02T10:00-05:00[US/Eastern], "
          + "endDateTime: 2025-01-02T16:00-05:00[US/Eastern], description: , "
          + "location: Cincinnati, isAllDay: false, isPrivate: false]";
      assertEquals(expected, model.getEventsInRange(start, end).toString());
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void testGetEventsInRangeException() {
    Model model = new Model();

    ChronoZonedDateTime<LocalDate> startDateTime =
        TimeUtils.parseDateTimeString("2025-01-01T10:00", ZONE_ID);
    ChronoZonedDateTime<LocalDate> endDateTime =
        TimeUtils.parseDateTimeString("2025-01-03T12:00", ZONE_ID);

    model.getEventsInRange(endDateTime, startDateTime);
  }

  @Test
  public void testGetEventsInRangeZeroEventsFetched() {
    ChronoZonedDateTime<LocalDate> startDateTime =
        TimeUtils.parseDateTimeString("2025-01-01T10:00", ZONE_ID);
    IEvent event1 = new Event.EventBuilder()
        .subject("test")
        .startDateTime(startDateTime)
        .endDateTime(startDateTime.plus(6, ChronoUnit.HOURS))
        .location("New-york")
        .build();

    ChronoZonedDateTime<LocalDate> startDateTime2 =
        TimeUtils.parseDateTimeString("2025-01-06T10:00", ZONE_ID);
    IEvent event2 = new Event.EventBuilder()
        .subject("test")
        .startDateTime(startDateTime2)
        .endDateTime(startDateTime2.plus(6, ChronoUnit.HOURS))
        .location("Cincinnati")
        .build();

    try {
      model.addEvents(List.of(event1, event2));

      ChronoZonedDateTime<LocalDate> time1 =
          TimeUtils.parseDateTimeString("2025-01-02T10:00", ZONE_ID);
      ChronoZonedDateTime<LocalDate> endTime1 =
          TimeUtils.parseDateTimeString("2025-01-03T12:00", ZONE_ID);

      model.getEventsInRange(time1, endTime1);
    } catch (IllegalArgumentException e) {
      assertEquals("No events found in given date range", e.getMessage());
    }
  }

  @Test
  public void testGetEventsInRangeExactEvents() {
    ChronoZonedDateTime<LocalDate> startDateTime =
        TimeUtils.parseDateTimeString("2025-01-01T10:00", ZONE_ID);
    IEvent event1 = new Event.EventBuilder()
        .subject("test")
        .startDateTime(startDateTime)
        .endDateTime(startDateTime.plus(6, ChronoUnit.HOURS))
        .location("New-york")
        .build();

    ChronoZonedDateTime<LocalDate> startDateTime2 =
        TimeUtils.parseDateTimeString("2025-01-06T10:00", ZONE_ID);
    IEvent event2 = new Event.EventBuilder()
        .subject("test")
        .startDateTime(startDateTime2)
        .endDateTime(startDateTime2.plus(6, ChronoUnit.HOURS))
        .location("Cincinnati")
        .build();

    try {
      model.addEvents(List.of(event1, event2));

      ChronoZonedDateTime<LocalDate> time1 =
          TimeUtils.parseDateTimeString("2025-01-01T10:00", ZONE_ID);
      ChronoZonedDateTime<LocalDate> endTime1 =
          TimeUtils.parseDateTimeString("2025-01-06T10:00", ZONE_ID);

      assertEquals(2, model.getEventsInRange(time1, endTime1).size());
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  @Test
  public void testGetEventsInRangeOverlapEvents() {
    ChronoZonedDateTime<LocalDate> startDateTime =
        TimeUtils.parseDateTimeString("2025-01-01T10:00", ZONE_ID);
    IEvent event1 = new Event.EventBuilder()
        .subject("test")
        .startDateTime(startDateTime)
        .endDateTime(startDateTime.plus(6, ChronoUnit.HOURS))
        .location("New-york")
        .build();

    ChronoZonedDateTime<LocalDate> startDateTime2 =
        TimeUtils.parseDateTimeString("2025-01-06T10:00", ZONE_ID);
    IEvent event2 = new Event.EventBuilder()
        .subject("test")
        .startDateTime(startDateTime2)
        .endDateTime(startDateTime2.plus(6, ChronoUnit.HOURS))
        .location("Cincinnati")
        .build();

    try {
      model.addEvents(List.of(event1, event2));

      ChronoZonedDateTime<LocalDate> time1 =
          TimeUtils.parseDateTimeString("2025-01-03T10:00", ZONE_ID);
      ChronoZonedDateTime<LocalDate> endTime1 =
          TimeUtils.parseDateTimeString("2025-01-07T10:00", ZONE_ID);

      assertEquals(1, model.getEventsInRange(time1, endTime1).size());
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEditEventsBySubjectEventNotFound() throws IllegalArgumentException {
    model.editEventsBySubject("location", "invalid", "new");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEditEventsFromStartDateTimeEventNotFound() throws IllegalArgumentException {
    model.editEventsFromStartDateTime("location", "invalid",
        "2025-01-03T10:00", "new");
  }

  @Test
  public void testEditSingleEvent() {
    String subject = "test";
    ChronoZonedDateTime<LocalDate> startDateTime =
        TimeUtils.parseDateTimeString("2025-01-01T10:00", ZONE_ID);
    IEvent event = new Event.EventBuilder()
        .subject(subject)
        .startDateTime(startDateTime)
        .endDateTime(startDateTime.plus(6, ChronoUnit.HOURS))
        .build();

    try {
      model.addEvents(List.of(event));
      model.editSingleEvent("location", subject, "2025-01-01T10:00",
          "2025-01-01T16:00", "Location");

      String expected = "[subject: test, startDateTime: 2025-01-01T10:00-05:00[US/Eastern], "
          + "endDateTime: 2025-01-01T16:00-05:00[US/Eastern], description: , "
          + "location: Location, isAllDay: false, isPrivate: false]";
      assertEquals(expected, model.getEventsOnDate(startDateTime).toString());
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  @Test(expected = ConflictException.class)
  public void testEditSingleEventConflict() throws ConflictException {
    String subject = "test";
    ChronoZonedDateTime<LocalDate> startDateTime =
        TimeUtils.parseDateTimeString("2025-01-01T10:00", ZONE_ID);
    IEvent event1 = new Event.EventBuilder()
        .subject(subject)
        .startDateTime(startDateTime)
        .endDateTime(startDateTime.plus(1, ChronoUnit.HOURS))
        .build();
    IEvent event2 = new Event.EventBuilder()
        .subject(subject)
        .startDateTime(startDateTime.plus(1, ChronoUnit.HOURS))
        .endDateTime(startDateTime.plus(2, ChronoUnit.HOURS))
        .build();
    try {
      model.addEvents(List.of(event1, event2));
    } catch (Exception e) {
      fail(e.getMessage());
    }

    model.editSingleEvent("endDateTime", subject, "2025-01-01T10:00",
        "2025-01-01T11:00", "2025-01-01T12:00");
  }

  @Test
  public void testEditSingleEventFindsCorrectEvent() {
    String subject = "test";
    ChronoZonedDateTime<LocalDate> startDateTime =
        TimeUtils.parseDateTimeString("2025-01-01T10:00", ZONE_ID);
    IEvent event1 = new Event.EventBuilder()
        .subject(subject)
        .startDateTime(startDateTime)
        .endDateTime(startDateTime.plus(1, ChronoUnit.HOURS))
        .build();
    IEvent event2 = new Event.EventBuilder()
        .subject(subject)
        .startDateTime(startDateTime.plus(1, ChronoUnit.HOURS))
        .endDateTime(startDateTime.plus(2, ChronoUnit.HOURS))
        .build();

    try {
      model.addEvents(List.of(event1, event2));
      model.editSingleEvent("location", subject, "2025-01-01T11:00",
          "2025-01-01T12:00", "Location");

      String expected = "[subject: test, startDateTime: 2025-01-01T10:00-05:00[US/Eastern], "
          + "endDateTime: 2025-01-01T11:00-05:00[US/Eastern], description: , location: , "
          + "isAllDay: false, isPrivate: false, "
          + "subject: test, startDateTime: 2025-01-01T11:00-05:00[US/Eastern], "
          + "endDateTime: 2025-01-01T12:00-05:00[US/Eastern], description: ,"
          + " location: Location, isAllDay: false, isPrivate: false]";
      assertEquals(expected, model.getEventsOnDate(startDateTime).toString());
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  @Test
  public void testCopyEventsInRangeDate() {
    ICalendar target = new Calendar("target", "US/Pacific");
    model.addCalendar(target);

    ChronoZonedDateTime<LocalDate> dateTime =
        TimeUtils.parseDateTimeString("2025-01-01T10:00", ZONE_ID);
    IEvent event1 = new Event.EventBuilder()
        .subject("1")
        .startDateTime(dateTime)
        .endDateTime(dateTime.plus(1, ChronoUnit.DAYS))
        .isAllDay(true)
        .build();

    IEvent event2 = new Event.EventBuilder()
        .subject("2")
        .startDateTime(dateTime.plus(3, ChronoUnit.DAYS))
        .endDateTime(dateTime.plus(4, ChronoUnit.DAYS))
        .isAllDay(true)
        .build();

    model.addEvents(List.of(event1, event2));
    model.copyEventsInRange("2025-01-01", "2025-01-06",
        "target", "2025-01-10");

    List<IEvent> events = target.getEvents();
    assertEquals(2, events.size());

    String expected = "[subject: 1, startDateTime: 2025-01-09T10:00-08:00[US/Pacific], "
        + "endDateTime: 2025-01-10T10:00-08:00[US/Pacific], description: , location: , "
        + "isAllDay: true, isPrivate: false, "
        + "subject: 2, startDateTime: 2025-01-12T10:00-08:00[US/Pacific], "
        + "endDateTime: 2025-01-13T10:00-08:00[US/Pacific], description: , location: , "
        + "isAllDay: true, isPrivate: false]";
    assertEquals(expected, events.toString());
  }

  @Test
  public void testGetEventsOnDateEmpty() {
    ChronoZonedDateTime<LocalDate> dateTime =
        TimeUtils.parseDateTimeString("2025-01-01T10:00", ZONE_ID);
    try {
      model.getEventsOnDate(dateTime);
    } catch (Exception e) {
      assertEquals("No events found on the given date", e.getMessage());
    }
  }

  @Test
  public void copyEventsOnDateNoTargetCalendar() {
    try {
      IModel model = new Model();
      model.copyEventsOnDate("2025-01-01", "2025-01-02", "target");
    } catch (Exception e) {
      assertEquals("Calendar not found", e.getMessage());
    }
  }

  @Test
  public void copyEventsInRangeNoTargetCalendar() {
    try {
      IModel model = new Model();
      model.copyEventsInRange("2025-01-01", "2025-01-02", "target",
          "2025-01-10");
    } catch (Exception e) {
      assertEquals("Calendar not found", e.getMessage());
    }
  }

  @Test
  public void testCopyEventNoEventFound() {
    try {
      model.copyEvent("1", "target", "2025-01-01T10:00",
          "2025-01-01T10:00");
    } catch (Exception e) {
      assertEquals("Event not found", e.getMessage());
    }
  }

  @Test
  public void testCopyEventNoTargetCalendar() {
    ChronoZonedDateTime<LocalDate> dateTime =
        TimeUtils.parseDateTimeString("2025-01-01T10:00", ZONE_ID);
    IEvent event = new Event.EventBuilder()
        .subject("1")
        .startDateTime(dateTime)
        .endDateTime(dateTime.plus(1, ChronoUnit.HOURS))
        .build();

    try {
      model.addEvents(List.of(event));
      model.copyEvent("1", "target", "2025-01-01T10:00",
          "2025-01-01T10:00");
    } catch (Exception e) {
      assertEquals("Calendar not found", e.getMessage());
    }
  }

  @Test
  public void testCopyEventSuccess() {
    ICalendar target = new Calendar("target", "US/Eastern");
    model.addCalendar(target);

    ChronoZonedDateTime<LocalDate> dateTime =
        TimeUtils.parseDateTimeString("2025-01-01T10:00", ZONE_ID);
    IEvent event = new Event.EventBuilder()
        .subject("1")
        .startDateTime(dateTime)
        .endDateTime(dateTime.plus(1, ChronoUnit.HOURS))
        .build();

    try {
      model.addEvents(List.of(event));
      model.copyEvent("1", "target", "2025-01-01T10:00",
          "2025-01-01T10:00");
    } catch (Exception e) {
      System.err.println(e.getMessage());
    }

    assertEquals(1, target.getEvents().size());

    String expected = "subject: 1, startDateTime: 2025-01-01T10:00-05:00[US/Eastern], "
        + "endDateTime: 2025-01-01T11:00-05:00[US/Eastern], description: , location: , "
        + "isAllDay: false, isPrivate: false";
    assertEquals(expected, target.getEvents().get(0).toString());
  }

  @Test
  public void testCopyEventCorrectEvent() {
    ICalendar target = new Calendar("target", "US/Eastern");
    model.addCalendar(target);

    ChronoZonedDateTime<LocalDate> dateTime =
        TimeUtils.parseDateTimeString("2025-01-01T10:00", ZONE_ID);
    IEvent event1 = new Event.EventBuilder()
        .subject("1")
        .startDateTime(dateTime)
        .endDateTime(dateTime.plus(1, ChronoUnit.HOURS))
        .description("description")
        .build();
    IEvent event2 = new Event.EventBuilder()
        .subject("1")
        .startDateTime(dateTime.plus(3, ChronoUnit.HOURS))
        .endDateTime(dateTime.plus(5, ChronoUnit.HOURS))
        .build();
    IEvent event3 = new Event.EventBuilder()
        .subject("1")
        .startDateTime(dateTime.minus(5, ChronoUnit.HOURS))
        .endDateTime(dateTime.minus(4, ChronoUnit.HOURS))
        .build();

    try {
      model.addEvents(List.of(event1, event2, event3));
      model.copyEvent("1", "target", "2025-01-01T10:00",
          "2025-01-01T10:00");
    } catch (Exception e) {
      System.err.println(e.getMessage());
      fail();
    }

    assertEquals(1, target.getEvents().size());

    String expected = "subject: 1, startDateTime: 2025-01-01T10:00-05:00[US/Eastern], "
        + "endDateTime: 2025-01-01T11:00-05:00[US/Eastern], description: description, "
        + "location: , isAllDay: false, isPrivate: false";
    assertEquals(expected, target.getEvents().get(0).toString());
  }

  @Test
  public void testCopyEventSkipConflicts() {
    ICalendar target = new Calendar("target", "US/Eastern");
    model.addCalendar(target);

    ChronoZonedDateTime<LocalDate> dateTime =
        TimeUtils.parseDateTimeString("2025-01-01T06:00", ZONE_ID);
    IEvent event1 = new Event.EventBuilder()
        .subject("1")
        .startDateTime(dateTime)
        .endDateTime(dateTime.plus(1, ChronoUnit.HOURS))
        .description("description")
        .build();
    IEvent event2 = new Event.EventBuilder()
        .subject("1")
        .startDateTime(dateTime.plus(2, ChronoUnit.HOURS))
        .endDateTime(dateTime.plus(3, ChronoUnit.HOURS))
        .build();
    IEvent event3 = new Event.EventBuilder()
        .subject("1")
        .startDateTime(dateTime.plus(4, ChronoUnit.HOURS))
        .endDateTime(dateTime.plus(5, ChronoUnit.HOURS))
        .build();

    try {
      model.addEvents(List.of(event1, event2, event3));
      model.copyEvent("1", "target", "2025-01-01T06:00",
          "2025-01-01T06:00");
    } catch (Exception e) {
      System.err.println(e.getMessage());
      fail();
    }

    try {
      model.copyEventsOnDate("2025-01-01", "2025-01-01",
          "target");
    } catch (ConflictException e) {
      assertEquals("Skipped 1 conflicting event", e.getMessage());
    }
    assertEquals(3, target.getEvents().size());
  }

  @Test
  public void testCopyEventAllConflicts() {
    ICalendar target = new Calendar("target", "US/Eastern");
    model.addCalendar(target);

    ChronoZonedDateTime<LocalDate> dateTime =
        TimeUtils.parseDateTimeString("2025-01-01T06:00", ZONE_ID);
    IEvent event1 = new Event.EventBuilder()
        .subject("1")
        .startDateTime(dateTime)
        .endDateTime(dateTime.plus(1, ChronoUnit.HOURS))
        .description("description")
        .build();
    IEvent event2 = new Event.EventBuilder()
        .subject("1")
        .startDateTime(dateTime.plus(2, ChronoUnit.HOURS))
        .endDateTime(dateTime.plus(3, ChronoUnit.HOURS))
        .build();
    IEvent event3 = new Event.EventBuilder()
        .subject("1")
        .startDateTime(dateTime.plus(4, ChronoUnit.HOURS))
        .endDateTime(dateTime.plus(5, ChronoUnit.HOURS))
        .build();

    try {
      model.addEvents(List.of(event1, event2, event3));
      model.copyEventsOnDate("2025-01-01", "2025-01-01",
          "target");
    } catch (Exception e) {
      System.err.println(e.getMessage());
      fail();
    }

    try {
      model.copyEventsOnDate("2025-01-01", "2025-01-01",
          "target");
    } catch (ConflictException e) {
      assertEquals("Skipped 3 conflicting events", e.getMessage());
    }

    assertEquals(3, target.getEvents().size());
  }

  @Test
  public void testGetAllCalendars() {
    List<ICalendar> calendars = model.getAllCalendars();
    assertEquals(2, calendars.size());
  }

  @Test
  public void testRepeatEndDateTime() {
    ChronoZonedDateTime<LocalDate> startDateTime =
        TimeUtils.parseDateTimeString("2025-04-10T18:00", ZONE_ID);
    ChronoZonedDateTime<LocalDate> endDateTime =
        TimeUtils.parseDateTimeString("2025-04-10T19:00", ZONE_ID);
    ChronoZonedDateTime<LocalDate> repeatEndDateTime =
        TimeUtils.parseDateTimeString("9999-12-30T00:00", ZONE_ID);

    IRepeatingEvent baseEvent = new RepeatingEvent.RepeatingEventBuilder()
        .subject("OfficeHours")
        .startDateTime(startDateTime)
        .endDateTime(endDateTime)
        .description("Weekly Meeting")
        .location("Conference Room")
        .isPrivate(false)
        .isAllDay(false)
        .repeatNumber(3)
        .repeatDays(Set.of(DayOfWeek.SATURDAY))
        .repeatEndDateTime(repeatEndDateTime)
        .build();

    List<IEvent> repeatingEvents = baseEvent.repeatNTimes();
    model.addEvents(repeatingEvents);

    model.editEventsBySubject("repeatEndDateTime",
        "OfficeHours", "2025-04-26T11:00");

    ChronoZonedDateTime<LocalDate> startDateTime2 =
        TimeUtils.parseDateTimeString("2025-04-12T18:00", ZONE_ID);
    ChronoZonedDateTime<LocalDate> endDateTime2 =
        TimeUtils.parseDateTimeString("2025-05-04T19:00", ZONE_ID);

    List<IEvent> repeatedEvents = model.getEventsInRange(startDateTime2, endDateTime2);
    System.out.println(repeatedEvents.size());
    assertEquals(3, repeatedEvents.size());
  }
}