package controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.chrono.ChronoZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import calendar.Calendar;
import calendar.Event;
import calendar.ICalendar;
import calendar.IEvent;
import calendar.IRepeatingEvent;
import calendar.RepeatingEvent;
import model.IModel;
import model.Model;
import utils.TimeUtils;
import view.View;
import view.Viewer;

/**
 * A JUnit class for testing the Controller class. Mainly integration tests.
 */
public class ControllerTest {

  private static ZoneId ZONE_ID;

  private IModel model;
  private ICalendar calendar;
  private Appendable out;
  private Viewer view;

  @BeforeClass
  public static void setUpBeforeClass() {
    ZONE_ID = ZoneId.of("US/Eastern");
  }

  @Before
  public void setUp() throws Exception {
    model = new Model();
    calendar = new Calendar("test", "US/Eastern");
    model.addCalendar(calendar);
    model.useCalendar("test");

    out = new StringBuilder();
    view = new View(out);
  }

  @Test
  public void testInteractiveModeValidCommands() throws IOException {
    String commands = "create event test on 2025-03-06T02:30";
    InputStream in = new ByteArrayInputStream(commands.getBytes(StandardCharsets.UTF_8));

    Listener controller = new Controller(new InputStreamReader(in), model, view);
    controller.listen();

    String expected = "Created all day event\n";
    assertEquals(expected, out.toString());
  }

  @Test
  public void testInteractiveModeInvalidCommands() throws IOException {
    String commands = "create evet test on 2025-03-06T02:30";
    InputStream in = new ByteArrayInputStream(commands.getBytes(StandardCharsets.UTF_8));

    Listener controller = new Controller(new InputStreamReader(in), model, view);
    controller.listen();

    String expected = "Unknown command";
    assertTrue(out.toString().startsWith(expected));
  }

  @Test
  public void testListenHandlesExceptions() throws IOException {
    String commands = "edit event location test from 2025-01-01T10:00 "
        + "to 2025-01-01T16:00 with Boston";
    InputStream in = new ByteArrayInputStream(commands.getBytes(StandardCharsets.UTF_8));

    Listener controller = new Controller(new InputStreamReader(in), model, view);
    controller.listen();

    String expected = "Error: Event not found: test\n";
    assertEquals(expected, out.toString());
  }

  @Test
  public void testListenHandlesExit() throws IOException {
    String commands = "exit";
    InputStream in = new ByteArrayInputStream(commands.getBytes(StandardCharsets.UTF_8));

    Listener controller = new Controller(new InputStreamReader(in), model, view);
    controller.listen();

    String expected = "";
    assertEquals(expected, out.toString());
  }

  @Test
  public void testListenUnknownCommand() throws IOException {
    String commands = "unknown command";
    InputStream in = new ByteArrayInputStream(commands.getBytes(StandardCharsets.UTF_8));

    Listener controller = new Controller(new InputStreamReader(in), model, view);
    controller.listen();

    // read in expected commands from file
    String path = "src/main/resources/availableCommands.txt";
    String expected = new String(Files.readAllBytes(Paths.get(path)));
    assertEquals("Unknown command: unknown command\n" + expected, out.toString().trim());
  }

  @Test(expected = IOException.class)
  public void testListenClosesScanner() throws IOException {
    String commands = "exit";
    InputStream in = new ByteArrayInputStream(commands.getBytes(StandardCharsets.UTF_8));
    InputStreamReader reader = new InputStreamReader(in);

    Listener controller = new Controller(reader, model, view);
    controller.listen();

    String expected = "";
    assertEquals(expected, out.toString());

    // throws an exception because the reader has been closed by the scanner
    boolean result = reader.ready();
    assertFalse(result);
  }

  @Test
  public void testEditRepeatingEvents() throws IOException {
    ChronoZonedDateTime<LocalDate> startDateTime =
        TimeUtils.parseDateTimeString("2025-01-01T10:00", ZONE_ID);
    IRepeatingEvent event = new RepeatingEvent.RepeatingEventBuilder()
        .subject("test")
        .startDateTime(startDateTime)
        .endDateTime(startDateTime.plus(6, ChronoUnit.HOURS))
        .location("A")
        .repeatNumber(3)
        .repeatDays(TimeUtils.getDaysOfWeek(new char[]{'M', 'W', 'F'}))
        .build();

    List<IEvent> events = event.repeatNTimes();
    try {
      model.addEvents(events);
    } catch (Exception e) {
      fail(e.getMessage());
    }

    String commands = "edit events location test from 2025-01-03T08:00 with B\n"
        + "print events from 2025-01-01T08:00 to 2025-01-09T08:00";
    InputStream in = new ByteArrayInputStream(commands.getBytes(StandardCharsets.UTF_8));
    InputStreamReader reader = new InputStreamReader(in);

    Listener controller = new Controller(reader, model, view);
    controller.listen();

    String expected = "Edited all matching events\n* subject: test, "
        + "startDateTime: 2025-01-01T10:00-05:00[US/Eastern], "
        + "endDateTime: 2025-01-01T16:00-05:00[US/Eastern], location: A\n"
        + "* subject: test, startDateTime: 2025-01-03T10:00-05:00[US/Eastern], "
        + "endDateTime: 2025-01-03T16:00-05:00[US/Eastern], location: B\n* subject: test, "
        + "startDateTime: 2025-01-06T10:00-05:00[US/Eastern], "
        + "endDateTime: 2025-01-06T16:00-05:00[US/Eastern], location: B\n"
        + "* subject: test, startDateTime: 2025-01-08T10:00-05:00[US/Eastern], "
        + "endDateTime: 2025-01-08T16:00-05:00[US/Eastern], location: B\n";
    assertEquals(expected, out.toString());
  }

  @Test
  public void testEditSingleInstanceOfRepeatingEvent() throws IOException {
    ChronoZonedDateTime<LocalDate> startDateTime =
        TimeUtils.parseDateTimeString("2025-01-01T10:00", ZONE_ID);
    IRepeatingEvent event = new RepeatingEvent.RepeatingEventBuilder()
        .subject("test")
        .startDateTime(startDateTime)
        .endDateTime(startDateTime.plus(1, ChronoUnit.HOURS))
        .location("A")
        .repeatNumber(3)
        .repeatDays(TimeUtils.getDaysOfWeek(new char[]{'M', 'W', 'F'}))
        .build();

    List<IEvent> events = event.repeatNTimes();
    try {
      model.addEvents(events);
    } catch (Exception e) {
      fail(e.getMessage());
    }

    String commands = "edit event location test from 2025-01-03T10:00 to 2025-01-03T11:00 with B\n"
        + "print events from 2025-01-01T08:00 to 2025-01-09T08:00";
    InputStream in = new ByteArrayInputStream(commands.getBytes(StandardCharsets.UTF_8));
    InputStreamReader reader = new InputStreamReader(in);

    Listener controller = new Controller(reader, model, view);
    controller.listen();

    String expected = "Edited event\n* subject: test, "
        + "startDateTime: 2025-01-01T10:00-05:00[US/Eastern], "
        + "endDateTime: 2025-01-01T11:00-05:00[US/Eastern], location: A\n"
        + "* subject: test, startDateTime: 2025-01-03T10:00-05:00[US/Eastern], "
        + "endDateTime: 2025-01-03T11:00-05:00[US/Eastern], location: B\n* "
        + "subject: test, startDateTime: 2025-01-06T10:00-05:00[US/Eastern], "
        + "endDateTime: 2025-01-06T11:00-05:00[US/Eastern], location: A\n* "
        + "subject: test, startDateTime: 2025-01-08T10:00-05:00[US/Eastern], "
        + "endDateTime: 2025-01-08T11:00-05:00[US/Eastern], location: A\n";
    assertEquals(expected, out.toString());
  }

  @Test
  public void testEditRepeatingEventRepeatEndDate() throws IOException {
    ChronoZonedDateTime<LocalDate> startDateTime =
        TimeUtils.parseDateTimeString("2025-01-01T10:00", ZONE_ID);
    IRepeatingEvent event = new RepeatingEvent.RepeatingEventBuilder()
        .subject("test")
        .startDateTime(startDateTime)
        .endDateTime(startDateTime.plus(1, ChronoUnit.HOURS))
        .location("A")
        .repeatEndDateTime(startDateTime.plus(1, ChronoUnit.WEEKS))
        .repeatDays(TimeUtils.getDaysOfWeek(new char[]{'M', 'W', 'F'}))
        .build();

    List<IEvent> events = event.repeatUntilEndDate();
    try {
      model.addEvents(events);
    } catch (Exception e) {
      fail(e.getMessage());
    }

    String commands = "edit event repeatEndDateTime test from 2025-01-03T10:00 to 2025-01-03T11:00"
        + " with 2025-01-19T10:00\nprint events from 2025-01-01T08:00 to 2025-01-15T08:00";
    InputStream in = new ByteArrayInputStream(commands.getBytes(StandardCharsets.UTF_8));
    InputStreamReader reader = new InputStreamReader(in);

    Listener controller = new Controller(reader, model, view);
    controller.listen();

    String expected = "Error: Repeat rules cannot be changed for a single event\n";
    assertEquals(expected, out.toString());
  }

  @Test
  public void testEditRepeatingEventRepeatNumber() throws IOException {
    ChronoZonedDateTime<LocalDate> startDateTime =
        TimeUtils.parseDateTimeString("2025-01-01T10:00", ZONE_ID);
    IRepeatingEvent event = new RepeatingEvent.RepeatingEventBuilder()
        .subject("test")
        .startDateTime(startDateTime)
        .endDateTime(startDateTime.plus(1, ChronoUnit.HOURS))
        .location("A")
        .repeatNumber(3)
        .repeatDays(TimeUtils.getDaysOfWeek(new char[]{'M', 'W', 'F'}))
        .build();

    List<IEvent> events = event.repeatNTimes();
    assertEquals(4, events.size());
    try {
      model.addEvents(events);
    } catch (Exception e) {
      fail(e.getMessage());
    }

    String commands = "edit events repeatNumber test from 2025-01-03T10:00 with 5\n"
        + "print events from 2025-01-01T08:00 to 2025-01-15T08:00";
    InputStream in = new ByteArrayInputStream(commands.getBytes(StandardCharsets.UTF_8));
    InputStreamReader reader = new InputStreamReader(in);

    Listener controller = new Controller(reader, model, view);
    controller.listen();

    String expected = "Edited all matching events\n* subject: test, "
        + "startDateTime: 2025-01-01T10:00-05:00[US/Eastern], "
        + "endDateTime: 2025-01-01T11:00-05:00[US/Eastern], location: A\n"
        + "* subject: test, startDateTime: 2025-01-03T10:00-05:00[US/Eastern], "
        + "endDateTime: 2025-01-03T11:00-05:00[US/Eastern], location: A\n* "
        + "subject: test, startDateTime: 2025-01-06T10:00-05:00[US/Eastern], "
        + "endDateTime: 2025-01-06T11:00-05:00[US/Eastern], location: A\n"
        + "* subject: test, startDateTime: 2025-01-08T10:00-05:00[US/Eastern], "
        + "endDateTime: 2025-01-08T11:00-05:00[US/Eastern], location: A\n"
        + "* subject: test, startDateTime: 2025-01-10T10:00-05:00[US/Eastern], "
        + "endDateTime: 2025-01-10T11:00-05:00[US/Eastern], location: A\n"
        + "* subject: test, startDateTime: 2025-01-13T10:00-05:00[US/Eastern], "
        + "endDateTime: 2025-01-13T11:00-05:00[US/Eastern], location: A\n";
    assertEquals(expected, out.toString());
  }

  @Test
  public void testEditRepeatingEventRepeatDaysWithRepeatNumber() throws IOException {
    ChronoZonedDateTime<LocalDate> startDateTime =
        TimeUtils.parseDateTimeString("2025-01-01T10:00", ZONE_ID);
    IRepeatingEvent event = new RepeatingEvent.RepeatingEventBuilder()
        .subject("test")
        .startDateTime(startDateTime)
        .endDateTime(startDateTime.plus(1, ChronoUnit.HOURS))
        .location("A")
        .repeatNumber(4)
        .repeatDays(TimeUtils.getDaysOfWeek(new char[]{'M', 'W', 'F'}))
        .build();

    List<IEvent> events = event.repeatNTimes();
    try {
      model.addEvents(events);
    } catch (Exception e) {
      fail(e.getMessage());
    }

    String commands = "edit events repeatDays test from 2025-01-03T10:00 with MWTF\n"
        + "print events from 2025-01-01T08:00 to 2025-01-15T08:00";
    InputStream in = new ByteArrayInputStream(commands.getBytes(StandardCharsets.UTF_8));
    InputStreamReader reader = new InputStreamReader(in);

    Listener controller = new Controller(reader, model, view);
    controller.listen();

    String expected = "Edited all matching events\n* subject: test, "
        + "startDateTime: 2025-01-01T10:00-05:00[US/Eastern], "
        + "endDateTime: 2025-01-01T11:00-05:00[US/Eastern], location: A\n"
        + "* subject: test, startDateTime: 2025-01-03T10:00-05:00[US/Eastern], "
        + "endDateTime: 2025-01-03T11:00-05:00[US/Eastern], location: A\n"
        + "* subject: test, startDateTime: 2025-01-06T10:00-05:00[US/Eastern], "
        + "endDateTime: 2025-01-06T11:00-05:00[US/Eastern], location: A\n* "
        + "subject: test, startDateTime: 2025-01-07T10:00-05:00[US/Eastern], "
        + "endDateTime: 2025-01-07T11:00-05:00[US/Eastern], location: A\n"
        + "* subject: test, startDateTime: 2025-01-08T10:00-05:00[US/Eastern], "
        + "endDateTime: 2025-01-08T11:00-05:00[US/Eastern], location: A\n"
        + "* subject: test, startDateTime: 2025-01-10T10:00-05:00[US/Eastern], "
        + "endDateTime: 2025-01-10T11:00-05:00[US/Eastern], location: A\n";
    assertEquals(expected, out.toString());
  }

  @Test
  public void testEditRepeatingEventLessRepeatDaysWithRepeatNumber() throws IOException {
    ChronoZonedDateTime<LocalDate> startDateTime =
        TimeUtils.parseDateTimeString("2025-01-01T10:00", ZONE_ID);
    IRepeatingEvent event = new RepeatingEvent.RepeatingEventBuilder()
        .subject("test")
        .startDateTime(startDateTime)
        .endDateTime(startDateTime.plus(1, ChronoUnit.HOURS))
        .location("A")
        .repeatNumber(3)
        .repeatDays(TimeUtils.getDaysOfWeek(new char[]{'M', 'W', 'F'}))
        .build();

    List<IEvent> events = event.repeatNTimes();
    try {
      model.addEvents(events);
    } catch (Exception e) {
      fail(e.getMessage());
    }

    String commands = "edit events repeatDays test from 2025-01-03T10:00 with MW\n"
        + "print events from 2025-01-01T08:00 to 2025-01-15T08:00";
    InputStream in = new ByteArrayInputStream(commands.getBytes(StandardCharsets.UTF_8));
    InputStreamReader reader = new InputStreamReader(in);

    Listener controller = new Controller(reader, model, view);
    controller.listen();

    String expected = "Edited all matching events\n* subject: test, "
        + "startDateTime: 2025-01-01T10:00-05:00[US/Eastern], "
        + "endDateTime: 2025-01-01T11:00-05:00[US/Eastern], location: A\n"
        + "* subject: test, startDateTime: 2025-01-06T10:00-05:00[US/Eastern], "
        + "endDateTime: 2025-01-06T11:00-05:00[US/Eastern], location: A\n"
        + "* subject: test, startDateTime: 2025-01-08T10:00-05:00[US/Eastern], "
        + "endDateTime: 2025-01-08T11:00-05:00[US/Eastern], location: A\n"
        + "* subject: test, startDateTime: 2025-01-13T10:00-05:00[US/Eastern], "
        + "endDateTime: 2025-01-13T11:00-05:00[US/Eastern], location: A\n";
    assertEquals(expected, out.toString());
  }

  @Test
  public void testEventCreateAndEdit() throws IOException {
    String commands = "create event test from 2025-03-06T02:30 to 2025-03-06T03:30\n"
        + "print events from 2025-03-05T10:00 to 2025-03-15T16:00\n"
        + "show status on 2025-03-06T02:30\n"
        + "edit events location test from 2025-03-06T02:30 with new\n";

    InputStream in = new ByteArrayInputStream(commands.getBytes(StandardCharsets.UTF_8));
    Listener controller = new Controller(new InputStreamReader(in), model, view);
    controller.listen();

    String expected = "Created event\n"
        + "* subject: test, startDateTime: 2025-03-06T02:30-05:00[US/Eastern], "
        + "endDateTime: 2025-03-06T03:30-05:00[US/Eastern]\n"
        + "Busy\n"
        + "Edited all matching events\n";
    assertEquals(expected, out.toString());
  }

  @Test
  public void testEventCreatePrintAndShowStatus() throws IOException {
    String commands = "create event test on 2025-01-06 repeats MWF for 4 times\n"
        + "print events from 2025-01-05T00:00 to 2025-01-15T16:00\n"
        + "show status on 2025-01-06T01:00\n"
        + "edit events startDateTime test from 2025-01-10T00:00 with 2025-01-07T09:00\n";

    InputStream in = new ByteArrayInputStream(commands.getBytes(StandardCharsets.UTF_8));
    Listener controller = new Controller(new InputStreamReader(in), model, view);

    controller.listen();

    String expected = "Created repeating event\n"
        + "* subject: test, startDateTime: 2025-01-06T00:00-05:00[US/Eastern],"
        + " endDateTime: 2025-01-07T00:00-05:00[US/Eastern]\n"
        + "* subject: test, startDateTime: 2025-01-08T00:00-05:00[US/Eastern], "
        + "endDateTime: 2025-01-09T00:00-05:00[US/Eastern]\n"
        + "* subject: test, startDateTime: 2025-01-10T00:00-05:00[US/Eastern], "
        + "endDateTime: 2025-01-11T00:00-05:00[US/Eastern]\n"
        + "* subject: test, startDateTime: 2025-01-13T00:00-05:00[US/Eastern], "
        + "endDateTime: 2025-01-14T00:00-05:00[US/Eastern]\n"
        + "* subject: test, startDateTime: 2025-01-15T00:00-05:00[US/Eastern], "
        + "endDateTime: 2025-01-16T00:00-05:00[US/Eastern]\n"
        + "Busy\n"
        + "Error: Editing the start or end times of multiple events at once will create "
        + "a conflict.\n";
    assertEquals(expected, out.toString());
  }

  @Test
  public void testEndToEnd() throws IOException {
    String commands = "create event test on 2025-01-06 repeats MWF for 4 times\n"
        + "print events from 2025-01-05T00:00 to 2025-01-15T16:00\n"
        + "create event test on 2025-01-16T01:00\n"
        + "show status on 2025-01-06T01:00\n"
        + "edit events location test from 2025-01-06T00:00 with updated\n"
        + "print events from 2025-01-05T00:00 to 2025-01-15T16:00\n"
        + "export cal testexport.csv";

    InputStream in = new ByteArrayInputStream(commands.getBytes(StandardCharsets.UTF_8));
    Listener controller = new Controller(new InputStreamReader(in), model, view);
    controller.listen();

    String userHome = System.getProperty("user.home");
    String expected = "Created repeating event\n"
        + "* subject: test, startDateTime: 2025-01-06T00:00-05:00[US/Eastern],"
        + " endDateTime: 2025-01-07T00:00-05:00[US/Eastern]\n"
        + "* subject: test, startDateTime: 2025-01-08T00:00-05:00[US/Eastern],"
        + " endDateTime: 2025-01-09T00:00-05:00[US/Eastern]\n"
        + "* subject: test, startDateTime: 2025-01-10T00:00-05:00[US/Eastern],"
        + " endDateTime: 2025-01-11T00:00-05:00[US/Eastern]\n"
        + "* subject: test, startDateTime: 2025-01-13T00:00-05:00[US/Eastern],"
        + " endDateTime: 2025-01-14T00:00-05:00[US/Eastern]\n"
        + "* subject: test, startDateTime: 2025-01-15T00:00-05:00[US/Eastern],"
        + " endDateTime: 2025-01-16T00:00-05:00[US/Eastern]\n"
        + "Created all day event\n"
        + "Busy\n"
        + "Edited all matching events\n"
        + "* subject: test, startDateTime: 2025-01-06T00:00-05:00[US/Eastern], "
        + "endDateTime: 2025-01-07T00:00-05:00[US/Eastern], location: updated\n"
        + "* subject: test, startDateTime: 2025-01-08T00:00-05:00[US/Eastern], "
        + "endDateTime: 2025-01-09T00:00-05:00[US/Eastern], location: updated\n"
        + "* subject: test, startDateTime: 2025-01-10T00:00-05:00[US/Eastern], "
        + "endDateTime: 2025-01-11T00:00-05:00[US/Eastern], location: updated\n"
        + "* subject: test, startDateTime: 2025-01-13T00:00-05:00[US/Eastern], "
        + "endDateTime: 2025-01-14T00:00-05:00[US/Eastern], location: updated\n"
        + "* subject: test, startDateTime: 2025-01-15T00:00-05:00[US/Eastern], "
        + "endDateTime: 2025-01-16T00:00-05:00[US/Eastern], location: updated\n"
        + "Exported calendar to " + userHome + "/Downloads/testexport.csv\n";
    assertEquals(expected, out.toString());
  }

  @Test
  public void testEventCreateAndEdit2() throws IOException {
    String commands = "create event test on 2025-03-06T02:30\n"
        + "print events from 2025-03-06T10:00 to 2025-03-15T16:00\n";

    InputStream in = new ByteArrayInputStream(commands.getBytes(StandardCharsets.UTF_8));
    Listener controller = new Controller(new InputStreamReader(in), model, view);
    controller.listen();

    String expected = "Created all day event\n"
        + "* subject: test, startDateTime: 2025-03-06T00:00-05:00[US/Eastern], "
        + "endDateTime: 2025-03-07T00:00-05:00[US/Eastern]\n";
    assertEquals(expected, out.toString());
  }

  @Test
  public void testAllDayEventConflict() throws IOException {
    ChronoZonedDateTime<LocalDate> dateTime =
        TimeUtils.parseDateTimeString("2025-03-02T02:30", ZONE_ID);
    IEvent event = new Event.EventBuilder()
        .subject("conflict")
        .startDateTime(dateTime)
        .endDateTime(dateTime.plus(1, ChronoUnit.DAYS))
        .build();

    model.addEvents(List.of(event));

    String command = "create event test on 2025-03-02T02:30";
    InputStream in = new ByteArrayInputStream(command.getBytes(StandardCharsets.UTF_8));
    InputStreamReader reader = new InputStreamReader(in);

    Listener controller = new Controller(reader, model, view);
    controller.listen();

    String expected = "Error: Event conflicts with an existing event\n";
    assertEquals(expected, out.toString());
  }

  @Test
  public void testAllDayRepeatingEventsUntilEndDateConflict() throws Exception {
    ChronoZonedDateTime<LocalDate> dateTime =
        TimeUtils.parseDateTimeString("2025-03-02T02:30", ZONE_ID);
    IEvent event = new Event.EventBuilder()
        .subject("conflict")
        .startDateTime(dateTime)
        .endDateTime(dateTime.plus(1, ChronoUnit.DAYS))
        .build();
    model.addEvents(List.of(event));

    String command = "create event \"test name\" on 2025-03-02 repeats MTU until 2025-09-02";
    InputStream in = new ByteArrayInputStream(command.getBytes(StandardCharsets.UTF_8));
    InputStreamReader reader = new InputStreamReader(in);

    Listener controller = new Controller(reader, model, view);
    controller.listen();

    String expected = "Error: Event conflicts with an existing event\n";
    assertEquals(expected, out.toString());
  }

  @Test
  public void testSingleEventConflict() throws Exception {
    ChronoZonedDateTime<LocalDate> dateTime =
        TimeUtils.parseDateTimeString("2025-03-02T02:30", ZONE_ID);
    IEvent event = new Event.EventBuilder()
        .subject("conflict")
        .startDateTime(dateTime)
        .endDateTime(dateTime.plus(1, ChronoUnit.DAYS))
        .build();
    model.addEvents(List.of(event));

    String command = "create event test from 2025-03-02T02:30 to 2025-03-02T03:30";
    InputStream in = new ByteArrayInputStream(command.getBytes(StandardCharsets.UTF_8));
    InputStreamReader reader = new InputStreamReader(in);

    Listener controller = new Controller(reader, model, view);
    controller.listen();

    String expected = "Error: Event conflicts with an existing event\n";
    assertEquals(expected, out.toString());
  }

  @Test
  public void testDefaultCalendar() throws IOException {
    IModel model = new Model();

    String command = "create event test from 2025-03-02T02:30 to 2025-03-02T03:30";
    InputStream in = new ByteArrayInputStream(command.getBytes(StandardCharsets.UTF_8));
    InputStreamReader reader = new InputStreamReader(in);

    Listener controller = new Controller(reader, model, view);
    controller.listen();

    String expected = "Created event\n";
    assertEquals(expected, out.toString());
  }

  @Test
  public void testCopyEventConflict() throws Exception {
    ChronoZonedDateTime<LocalDate> dateTime =
        TimeUtils.parseDateTimeString("2025-03-02T02:30", ZONE_ID);
    IEvent event = new Event.EventBuilder()
        .subject("conflict")
        .startDateTime(dateTime)
        .endDateTime(dateTime.plus(1, ChronoUnit.DAYS))
        .build();
    model.addEvents(List.of(event));

    String commands = "create event test from 2025-03-02T02:30 to 2025-03-02T03:30\n"
        + "create calendar --name target --timezone US/Eastern\n"
        + "copy event test ";
    InputStream in = new ByteArrayInputStream(commands.getBytes(StandardCharsets.UTF_8));
    InputStreamReader reader = new InputStreamReader(in);

    Listener controller = new Controller(reader, model, view);
    controller.listen();

    String expected = "Error: Event conflicts with an existing event\n";
    assertEquals(expected, out.toString());
  }

  @Test
  public void testEditRepeatingEventRepeatNumberConflict() throws IOException {
    ChronoZonedDateTime<LocalDate> startDateTime =
        TimeUtils.parseDateTimeString("2025-01-01T10:00", ZONE_ID);
    IRepeatingEvent event1 = new RepeatingEvent.RepeatingEventBuilder()
        .subject("test")
        .startDateTime(startDateTime)
        .endDateTime(startDateTime.plus(1, ChronoUnit.HOURS))
        .location("A")
        .repeatNumber(3)
        .repeatDays(TimeUtils.getDaysOfWeek(new char[]{'M', 'W', 'F'}))
        .build();

    IEvent event2 = new Event.EventBuilder()
        .subject("conflict")
        .startDateTime(startDateTime.plus(12, ChronoUnit.DAYS))
        .endDateTime(startDateTime.plus(13, ChronoUnit.DAYS))
        .isAllDay(true)
        .build();

    List<IEvent> events = event1.repeatNTimes();
    assertEquals(4, events.size());
    try {
      model.addEvents(events);
      model.addEvents(List.of(event2));
    } catch (Exception e) {
      fail(e.getMessage());
    }

    String commands = "edit events repeatNumber test from 2025-01-03T10:00 with 5";
    InputStream in = new ByteArrayInputStream(commands.getBytes(StandardCharsets.UTF_8));
    InputStreamReader reader = new InputStreamReader(in);

    Listener controller = new Controller(reader, model, view);
    controller.listen();

    String expected = "Error: Event conflicts with an existing event\n";
    assertEquals(expected, out.toString());
    assertEquals(5, calendar.getEvents().size());
  }

  @Test
  public void testEditRepeatingEventBySubjectRepeatNumberConflict() throws IOException {
    ChronoZonedDateTime<LocalDate> startDateTime =
        TimeUtils.parseDateTimeString("2025-01-01T10:00", ZONE_ID);
    IRepeatingEvent event1 = new RepeatingEvent.RepeatingEventBuilder()
        .subject("test")
        .startDateTime(startDateTime)
        .endDateTime(startDateTime.plus(1, ChronoUnit.HOURS))
        .location("A")
        .repeatNumber(3)
        .repeatDays(TimeUtils.getDaysOfWeek(new char[]{'M', 'W', 'F'}))
        .build();

    IEvent event2 = new Event.EventBuilder()
        .subject("conflict")
        .startDateTime(startDateTime.plus(12, ChronoUnit.DAYS))
        .endDateTime(startDateTime.plus(13, ChronoUnit.DAYS))
        .isAllDay(true)
        .build();

    List<IEvent> events = event1.repeatNTimes();
    assertEquals(4, events.size());
    try {
      model.addEvents(events);
      model.addEvents(List.of(event2));
    } catch (Exception e) {
      fail(e.getMessage());
    }

    String commands = "edit events repeatNumber test 5";
    InputStream in = new ByteArrayInputStream(commands.getBytes(StandardCharsets.UTF_8));
    InputStreamReader reader = new InputStreamReader(in);

    Listener controller = new Controller(reader, model, view);
    controller.listen();

    String expected = "Error: Event conflicts with an existing event\n";
    assertEquals(expected, out.toString());
    assertEquals(5, calendar.getEvents().size());
  }

  @Test
  public void testCopyEventSkipConflicts() throws IOException {
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

    } catch (Exception e) {
      System.err.println(e.getMessage());
      fail();
    }

    String commands = "copy event 1 on 2025-01-01T06:00 --target target to 2025-01-01T06:00\n"
        + "copy events on 2025-01-01 --target target to 2025-01-01";
    InputStream in = new ByteArrayInputStream(commands.getBytes(StandardCharsets.UTF_8));
    InputStreamReader reader = new InputStreamReader(in);

    Listener controller = new Controller(reader, model, view);
    controller.listen();

    String expected = "Copied event to calendar target\n"
        + "Error: Skipped 1 conflicting event\n";
    assertEquals(expected, out.toString());
    assertEquals(3, target.getEvents().size());
  }

}