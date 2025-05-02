package utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import calendar.Calendar;
import calendar.Event;
import calendar.ICalendar;
import calendar.IEvent;
import calendar.RepeatingEvent;
import model.IModel;
import model.Model;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.chrono.ChronoZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * A JUnit test class for testing the ImportExportUtils class.
 */
public class ImportExportUtilsTest {

  private static ZoneId ZONE_ID;
  private IModel model;

  @BeforeClass
  public static void setUpBeforeClass() {
    ZONE_ID = ZoneId.systemDefault();
  }

  @Before
  public void setUp() {
    model = new Model();
    ICalendar calendar = new Calendar("test", "US/Eastern");
    model.addCalendar(calendar);
    model.useCalendar("test");
  }

  @Test
  public void testExportCalendar() throws IOException {
    ChronoZonedDateTime<LocalDate> dateTime =
        TimeUtils.parseDateTimeString("2025-03-04T02:30", ZONE_ID);

    IEvent event = new Event.EventBuilder()
        .subject("Assignment5")
        .startDateTime(dateTime)
        .endDateTime(dateTime.plus(1, ChronoUnit.HOURS))
        .location("place1")
        .description("desc")
        .build();

    try {
      model.addEvents(List.of(event));
    } catch (Exception e) {
      fail(e.getMessage());
    }
    String exportPath = ImportExportUtils.exportCalendar("export_1.csv",
        model.getEventsOnDate(dateTime));

    String expected = "subject,startDate,startTime,endDate,endTime,allDayEvent,description,location"
        + ",private\nAssignment5,03/04/2025,02:30 AM,03/04/2025,03:30 AM,False,desc,place1,False\n";

    String result = Files.readString(Path.of(exportPath));
    assertEquals(expected, result);
  }

  @Test
  public void testExportCalendarWithAbsoluteFilePath() throws IOException {
    ChronoZonedDateTime<LocalDate> dateTime =
        TimeUtils.parseDateTimeString("2025-03-02T02:30", ZONE_ID);

    IEvent event = new Event.EventBuilder()
        .subject("1")
        .startDateTime(dateTime)
        .endDateTime(dateTime.plus(1, ChronoUnit.HOURS))
        .location("place1")
        .description("desc")
        .build();

    try {
      model.addEvents(List.of(event));
    } catch (Exception e) {
      fail(e.getMessage());
    }

    String filePath = Path.of(System.getProperty("user.home"), "export_1.csv")
        .toAbsolutePath().toString();
    String exportPath = ImportExportUtils.exportCalendar(filePath, model.getEventsOnDate(dateTime));

    String expected = "subject,startDate,startTime,endDate,endTime,allDayEvent,description,location"
        + ",private\n1,03/02/2025,02:30 AM,03/02/2025,03:30 AM,False,desc,place1,False\n";
    String result = Files.readString(Path.of(exportPath));
    assertEquals(expected, result);
  }

  @Test
  public void testExportCalendarWithFilePath() throws IOException {
    ChronoZonedDateTime<LocalDate> dateTime =
        TimeUtils.parseDateTimeString("2025-03-02T02:30", ZONE_ID);

    IEvent event = new Event.EventBuilder()
        .subject("1")
        .startDateTime(dateTime)
        .endDateTime(dateTime.plus(1, ChronoUnit.HOURS))
        .location("place1")
        .description("desc")
        .build();

    try {
      model.addEvents(List.of(event));
    } catch (Exception e) {
      fail(e.getMessage());
    }

    String filePath = Path.of(System.getProperty("user.home"), "export_1.csv").toString();
    String exportPath = ImportExportUtils.exportCalendar(filePath, model.getEventsOnDate(dateTime));

    String expected = "subject,startDate,startTime,endDate,endTime,allDayEvent,description,location"
        + ",private\n1,03/02/2025,02:30 AM,03/02/2025,03:30 AM,False,desc,place1,False\n";
    String result = Files.readString(Path.of(exportPath));
    assertEquals(expected, result);
  }

  @Test
  public void testExportCalForRepeatingEvents() throws IOException {
    ChronoZonedDateTime<LocalDate> dateTime =
        TimeUtils.parseDateTimeString("2025-01-01T08:00", ZONE_ID);
    ChronoZonedDateTime<LocalDate> repeatEndDateTime =
        TimeUtils.parseDateTimeString("2025-01-15T08:00", ZONE_ID);
    RepeatingEvent repeatingEvent = new RepeatingEvent.RepeatingEventBuilder()
        .subject("repeat")
        .startDateTime(dateTime)
        .endDateTime(dateTime.plus(2, ChronoUnit.HOURS))
        .location("Boston")
        .description("Workshop")
        .repeatDays(TimeUtils.getDaysOfWeek(new char[]{'W'}))
        .repeatEndDateTime(repeatEndDateTime)
        .build();

    List<IEvent> events = repeatingEvent.repeatUntilEndDate();
    model.addEvents(events);

    String filepath = ImportExportUtils.exportCalendar("repeatingTest.csv",
        model.getEventsInRange(dateTime, repeatEndDateTime));

    String expected = "subject,startDate,startTime,endDate,endTime,allDayEvent,description,"
        + "location,private\n"
        + "repeat,01/01/2025,08:00 AM,01/01/2025,10:00 AM,False,Workshop,Boston,False\n"
        + "repeat,01/08/2025,08:00 AM,01/08/2025,10:00 AM,False,Workshop,Boston,False\n"
        + "repeat,01/15/2025,08:00 AM,01/15/2025,10:00 AM,False,Workshop,Boston,False\n";

    String result = Files.readString(Path.of(filepath));
    assertEquals(expected, result);
  }

  @Test(expected = IOException.class)
  public void testExportCalendarBlankFilepath() throws IOException {
    ImportExportUtils.exportCalendar("", new ArrayList<>());
  }

  @Test(expected = FileNotFoundException.class)
  public void testImportCalendarBlankFilepath() throws IOException {
    ImportExportUtils.importCalendar("", ZoneId.systemDefault());
  }

  @Test
  public void testImportCalendar() throws IOException {
    String path = Path.of("src", "test", "resources", "export.csv").toAbsolutePath().toString();
    List<IEvent> imports = ImportExportUtils.importCalendar(path, ZoneId.systemDefault());
    assertNotNull(imports);
    assertEquals(1, imports.size());
    IEvent event = imports.get(0);

    assertEquals("event", event.getSubject());
    assertFalse(event.isAllDay());
    assertFalse(event.isPrivate());
  }
}