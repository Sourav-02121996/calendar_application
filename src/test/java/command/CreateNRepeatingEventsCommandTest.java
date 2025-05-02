package command;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import model.IModel;
import view.View;
import view.Viewer;
import mock.MockModel;

/**
 * A JUnit test class for the CreateNRepeatingEventsCommand class. Uses the MockModel class to
 * ensure that the parameters are being sent correctly.
 */
public class CreateNRepeatingEventsCommandTest {

  private Appendable out;
  private StringBuilder log;
  private IModel model;
  private Viewer view;
  private Command command;

  @Before
  public void setUp() {
    out = new StringBuilder();
    log = new StringBuilder();
    model = new MockModel(log, 1234);
    view = new View(out);
    command = new CreateNRepeatingEventsCommand();
  }

  @Test
  public void testConstructor() {
    Command command = new CreateNRepeatingEventsCommand();
    assertNotNull(command);
  }

  @Test
  public void testExecute() throws IOException {
    String commandString = "create event test from 2025-03-02T12:30 to 2025-03-02T13:30 "
        + "repeats mtu for 2 times";
    try {
      command.execute(model, view, commandString);
    } catch (Exception e) {
      fail(e.getMessage());
    }

    String expected = "Input: [subject: test, startDateTime: 2025-03-02T12:30-05:00[US/Eastern], "
        + "endDateTime: 2025-03-02T13:30-05:00[US/Eastern], description: , location: , "
        + "isAllDay: false, isPrivate: false, repeatNumber: 2, "
        + "repeatEndDateTime: 9999-12-29T23:00-05:00[America/New_York], "
        + "subject: test, startDateTime: 2025-03-03T12:30-05:00[US/Eastern], "
        + "endDateTime: 2025-03-03T13:30-05:00[US/Eastern], description: , "
        + "location: , isAllDay: false, isPrivate: false, repeatNumber: 2, "
        + "repeatEndDateTime: 9999-12-29T23:00-05:00[America/New_York], "
        + "subject: test, startDateTime: 2025-03-04T12:30-05:00[US/Eastern], "
        + "endDateTime: 2025-03-04T13:30-05:00[US/Eastern], description: , "
        + "location: , isAllDay: false, isPrivate: false, repeatNumber: 2, "
        + "repeatEndDateTime: 9999-12-29T23:00-05:00[America/New_York]]";
    assertEquals(expected, log.toString());
  }

  @Test
  public void testExecuteCaseInsensitive() {
    String commandString = "create Event test from 2025-03-02T12:30 to 2025-03-02T13:30 "
        + "repeats mtu for 2 times";
    try {
      boolean result = command.execute(model, view, commandString);
      assertTrue(result);
    } catch (Exception e) {
      fail(e.getMessage());
    }

    String expected = "Input: [subject: test, startDateTime: 2025-03-02T12:30-05:00[US/Eastern], "
        + "endDateTime: 2025-03-02T13:30-05:00[US/Eastern], description: , location: , "
        + "isAllDay: false, isPrivate: false, repeatNumber: 2, "
        + "repeatEndDateTime: 9999-12-29T23:00-05:00[America/New_York], "
        + "subject: test, startDateTime: 2025-03-03T12:30-05:00[US/Eastern], "
        + "endDateTime: 2025-03-03T13:30-05:00[US/Eastern], description: , "
        + "location: , isAllDay: false, isPrivate: false, repeatNumber: 2, "
        + "repeatEndDateTime: 9999-12-29T23:00-05:00[America/New_York], "
        + "subject: test, startDateTime: 2025-03-04T12:30-05:00[US/Eastern], "
        + "endDateTime: 2025-03-04T13:30-05:00[US/Eastern], description: , "
        + "location: , isAllDay: false, isPrivate: false, repeatNumber: 2, "
        + "repeatEndDateTime: 9999-12-29T23:00-05:00[America/New_York]]";
    assertEquals(expected, log.toString());
  }

  @Test
  public void testExecuteAllOptions() {
    String commandString = "create event test from 2025-03-02T12:30 to 2025-03-02T13:30 "
        + "repeats mtu for 2 times -d description -p -l location";
    try {
      command.execute(model, view, commandString);
    } catch (Exception e) {
      fail(e.getMessage());
    }

    String expected = "Input: [subject: test, startDateTime: 2025-03-02T12:30-05:00[US/Eastern], "
        + "endDateTime: 2025-03-02T13:30-05:00[US/Eastern], description: description, "
        + "location: location, isAllDay: false, isPrivate: true, repeatNumber: 2, "
        + "repeatEndDateTime: 9999-12-29T23:00-05:00[America/New_York], subject: test, "
        + "startDateTime: 2025-03-03T12:30-05:00[US/Eastern], "
        + "endDateTime: 2025-03-03T13:30-05:00[US/Eastern], "
        + "description: description, location: location, "
        + "isAllDay: false, isPrivate: true, repeatNumber: 2, "
        + "repeatEndDateTime: 9999-12-29T23:00-05:00[America/New_York], "
        + "subject: test, startDateTime: 2025-03-04T12:30-05:00[US/Eastern], "
        + "endDateTime: 2025-03-04T13:30-05:00[US/Eastern], description: description, "
        + "location: location, isAllDay: false, isPrivate: true, repeatNumber: 2, "
        + "repeatEndDateTime: 9999-12-29T23:00-05:00[America/New_York]]";
    assertEquals(expected, log.toString());
  }

  @Test
  public void testViewPrint() {
    String commandString = "create event test from 2025-03-02T12:30 to 2025-03-02T13:30 "
        + "repeats mtu for 2 times";
    try {
      command.execute(model, view, commandString);
    } catch (Exception e) {
      fail(e.getMessage());
    }
    assertEquals("Created repeating event\n", out.toString());
  }
}