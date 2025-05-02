package controller;

import org.junit.Before;
import org.junit.Test;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import mock.MockModel;
import mock.MockView;
import model.IModel;
import view.Viewer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * A JUnit test class for testing the ViewController.
 */
public class ViewControllerTest {

  private ViewController controller;
  private StringBuilder out;

  @Before
  public void setUp() {
    StringBuilder log = new StringBuilder();
    IModel mockModel = new MockModel(log, 123);
    controller = new ViewController(mockModel);
    out = new StringBuilder();
    Viewer view = new MockView(out);
    controller.setView(view);
  }

  @Test
  public void testAddCalendar() {
    String name = "new";
    String timezone = "America/New_York";

    boolean result = controller.addCalendar(name, timezone);
    assertTrue(result);

    String expected = "Created calendar\n";
    assertEquals(expected, out.toString());
  }

  @Test
  public void testAddCalendarMissingName() {
    String name = "";
    String timezone = "America/New_York";

    boolean result = controller.addCalendar(name, timezone);
    assertFalse(result);

    String expected = "Please enter a calendar name.\n";
    assertEquals(expected, out.toString());
  }

  @Test
  public void testEditCalendar() {
    String name = "default";
    String property = "name";
    String newValue = "new name";

    boolean result = controller.editCalendar(name, property, newValue);
    assertTrue(result);

    String expected = "Edited calendar name\n";
    assertEquals(expected, out.toString());
  }

  @Test
  public void testEditCalendarNoName() {
    String name = "name";
    String property = "name";
    String newValue = "";

    boolean result = controller.editCalendar(name, property, newValue);
    assertFalse(result);

    String expected = "Please enter a new name.\n";
    assertEquals(expected, out.toString());
  }

  @Test
  public void testEditCalendarTimezone() {
    String name = "default";
    String property = "timezone";
    String newValue = "US/Central";

    boolean result = controller.editCalendar(name, property, newValue);
    assertTrue(result);

    String expected = "Edited calendar timezone\n";
    assertEquals(expected, out.toString());
  }

  @Test
  public void testUseCalendar() {
    String name = "default";
    boolean result = controller.useCalendar(name);
    assertTrue(result);

    String expected = "Current calendar: default\n";
    assertEquals(expected, out.toString());
  }

  @Test
  public void testAddEvent() {
    Map<String, String> inputs = new HashMap<>();
    inputs.put("subject", "name");
    inputs.put("startDate", "2025-02-03");
    inputs.put("startTime", "02:30");
    inputs.put("endDate", "2025-02-03");
    inputs.put("endTime", "03:30");
    inputs.put("description", "description");
    inputs.put("location", "location");
    inputs.put("isAllDay", "false");
    inputs.put("isRepeating", "false");
    inputs.put("isPrivate", "false");

    boolean result = controller.addEvent(inputs);
    assertTrue(result);

    String expected = "Created event\n";
    assertEquals(expected, out.toString());
  }

  @Test
  public void testAddRepeatingEvent() {
    Map<String, String> inputs = new HashMap<>();
    inputs.put("subject", "name");
    inputs.put("startDate", "2025-02-03");
    inputs.put("startTime", "02:30");
    inputs.put("endDate", "2025-02-03");
    inputs.put("endTime", "03:30");
    inputs.put("description", "description");
    inputs.put("location", "location");
    inputs.put("isAllDay", "false");
    inputs.put("isRepeating", "true");
    inputs.put("isPrivate", "false");
    inputs.put("repeatNumber", "3");
    inputs.put("repeatNumberType", "true");
    inputs.put("repeatUntilType", "false");
    inputs.put("repeatMonday", "true");
    inputs.put("repeatTuesday", "true");
    inputs.put("repeatWednesday", "true");
    inputs.put("repeatThursday", "true");
    inputs.put("repeatFriday", "true");
    inputs.put("repeatSaturday", "true");
    inputs.put("repeatSunday", "true");

    boolean result = controller.addEvent(inputs);
    assertTrue(result);

    String expected = "Created repeating event\n";
    assertEquals(expected, out.toString());
  }

  @Test
  public void testEditEvent() {
    Map<String, String> inputs = new HashMap<>();
    inputs.put("subject", "name");
    inputs.put("startDate", "2025-02-03");
    inputs.put("startTime", "02:30");
    inputs.put("endDate", "2025-02-03");
    inputs.put("endTime", "03:30");
    inputs.put("property", "name");
    inputs.put("description", "private");
    inputs.put("isPrivate", "true");

    boolean result = controller.editEvent(inputs);
    assertTrue(result);

    String expected = "Edited event\n";
    assertEquals(expected, out.toString());
  }

  @Test
  public void testEditEventEditStartDateTime() {
    Map<String, String> inputs = new HashMap<>();
    inputs.put("subject", "name");
    inputs.put("startDate", "2025-02-03");
    inputs.put("startTime", "02:30");
    inputs.put("endDate", "2025-02-03");
    inputs.put("endTime", "03:30");
    inputs.put("property", "startDateTime");
    inputs.put("newDate", "2025-02-03");
    inputs.put("newTime", "03:00");

    boolean result = controller.editEvent(inputs);
    assertTrue(result);

    String expected = "Edited event\n";
    assertEquals(expected, out.toString());
  }

  @Test
  public void testEditEvents() {
    Map<String, String> inputs = new HashMap<>();
    inputs.put("subject", "name");
    inputs.put("startDate", "2025-02-03");
    inputs.put("startTime", "02:30");
    inputs.put("endDate", "2025-02-03");
    inputs.put("endTime", "03:30");
    inputs.put("property", "startDateTime");
    inputs.put("newDate", "2025-02-03");
    inputs.put("newTime", "03:00");
    inputs.put("editBySubject", "true");
    inputs.put("editByDate", "false");

    boolean result = controller.editEvents(inputs);
    assertTrue(result);

    String expected = "Edited all matching events\n";
    assertEquals(expected, out.toString());
  }

  @Test
  public void testEditEventsRepeatEndDateTime() {
    Map<String, String> inputs = new HashMap<>();
    inputs.put("subject", "name");
    inputs.put("startDate", "2025-02-03");
    inputs.put("startTime", "02:30");
    inputs.put("endDate", "2025-02-03");
    inputs.put("endTime", "03:30");
    inputs.put("property", "repeatEndDateTime");
    inputs.put("newDate", "2025-03-03");
    inputs.put("newTime", "03:00");
    inputs.put("editBySubject", "true");
    inputs.put("editByDate", "false");

    boolean result = controller.editEvents(inputs);
    assertTrue(result);

    String expected = "Edited all matching events\n";
    assertEquals(expected, out.toString());
  }

  @Test
  public void testExportCalendar() {
    String file = "file.csv";
    boolean result = controller.exportCalendar(file);
    assertTrue(result);

    String path = Path.of(System.getProperty("user.home"), "Downloads", file).toString();
    String expected = "Exported calendar to " + path + System.lineSeparator();
    assertEquals(expected, out.toString());
  }

  @Test
  public void testExportCalendarEmpty() {
    String file = "";
    boolean result = controller.exportCalendar(file);
    assertFalse(result);

    String expected = "Please choose a target path." + System.lineSeparator();
    assertEquals(expected, out.toString());
  }

  @Test
  public void testImportCalendar() {
    String filepath = "";
    boolean result = controller.importCalendar(filepath);
    assertFalse(result);

    String expected = "Please choose a file to import." + System.lineSeparator();
    assertEquals(expected, out.toString());
  }
}