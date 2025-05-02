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
 * A JUnit test class for the CreateNAllDayRepeatingEventsCommand class. Uses the MockModel class to
 * ensure that the parameters are being sent correctly.
 */
public class CreateNAllDayRepeatingEventsCommandTest {

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
    command = new CreateNAllDayRepeatingEventsCommand();
  }

  @Test
  public void testConstructor() {
    Command command = new CreateNAllDayRepeatingEventsCommand();
    assertNotNull(command);
  }

  @Test
  public void testExecute() throws IOException {
    String commandString = "create event test on 2025-03-02 repeats mtu for 5 times";
    try {
      command.execute(model, view, commandString);
    } catch (Exception e) {
      fail(e.getMessage());
    }

    String expected = "Input: [subject: test, startDateTime: 2025-03-02T00:00-05:00[US/Eastern], "
        + "endDateTime: 2025-03-03T00:00-05:00[US/Eastern], description: , location: , "
        + "isAllDay: true, isPrivate: false, repeatNumber: 5, "
        + "repeatEndDateTime: 9999-12-29T23:00-05:00[America/New_York], "
        + "subject: test, startDateTime: 2025-03-03T00:00-05:00[US/Eastern], "
        + "endDateTime: 2025-03-04T00:00-05:00[US/Eastern], description: , "
        + "location: , isAllDay: true, isPrivate: false, repeatNumber: 5, "
        + "repeatEndDateTime: 9999-12-29T23:00-05:00[America/New_York], "
        + "subject: test, startDateTime: 2025-03-04T00:00-05:00[US/Eastern], "
        + "endDateTime: 2025-03-05T00:00-05:00[US/Eastern], description: , "
        + "location: , isAllDay: true, isPrivate: false, repeatNumber: 5, "
        + "repeatEndDateTime: 9999-12-29T23:00-05:00[America/New_York], "
        + "subject: test, startDateTime: 2025-03-09T00:00-05:00[US/Eastern], "
        + "endDateTime: 2025-03-10T00:00-04:00[US/Eastern], description: , "
        + "location: , isAllDay: true, isPrivate: false, repeatNumber: 5, "
        + "repeatEndDateTime: 9999-12-29T23:00-05:00[America/New_York], "
        + "subject: test, startDateTime: 2025-03-10T00:00-04:00[US/Eastern], "
        + "endDateTime: 2025-03-11T00:00-04:00[US/Eastern], description: , "
        + "location: , isAllDay: true, isPrivate: false, repeatNumber: 5, "
        + "repeatEndDateTime: 9999-12-29T23:00-05:00[America/New_York], "
        + "subject: test, startDateTime: 2025-03-11T00:00-04:00[US/Eastern], "
        + "endDateTime: 2025-03-12T00:00-04:00[US/Eastern], description: , "
        + "location: , isAllDay: true, isPrivate: false, repeatNumber: 5, "
        + "repeatEndDateTime: 9999-12-29T23:00-05:00[America/New_York]]";
    assertEquals(expected, log.toString());
  }

  @Test
  public void testExecuteCaseInsensitive() {
    String commandString = "create EVENT test on 2025-03-02 repeats mtu for 5 times";
    try {
      boolean result = command.execute(model, view, commandString);
      assertTrue(result);
    } catch (Exception e) {
      fail(e.getMessage());
    }

    String expected = "Input: [subject: test, startDateTime: 2025-03-02T00:00-05:00[US/Eastern],"
        + " endDateTime: 2025-03-03T00:00-05:00[US/Eastern], description: , location: ,"
        + " isAllDay: true, isPrivate: false, repeatNumber: 5, "
        + "repeatEndDateTime: 9999-12-29T23:00-05:00[America/New_York], "
        + "subject: test, startDateTime: 2025-03-03T00:00-05:00[US/Eastern], "
        + "endDateTime: 2025-03-04T00:00-05:00[US/Eastern], description: , "
        + "location: , isAllDay: true, isPrivate: false, repeatNumber: 5, "
        + "repeatEndDateTime: 9999-12-29T23:00-05:00[America/New_York], "
        + "subject: test, startDateTime: 2025-03-04T00:00-05:00[US/Eastern], "
        + "endDateTime: 2025-03-05T00:00-05:00[US/Eastern], description: , "
        + "location: , isAllDay: true, isPrivate: false, repeatNumber: 5, "
        + "repeatEndDateTime: 9999-12-29T23:00-05:00[America/New_York], "
        + "subject: test, startDateTime: 2025-03-09T00:00-05:00[US/Eastern], "
        + "endDateTime: 2025-03-10T00:00-04:00[US/Eastern], description: , "
        + "location: , isAllDay: true, isPrivate: false, repeatNumber: 5, "
        + "repeatEndDateTime: 9999-12-29T23:00-05:00[America/New_York], "
        + "subject: test, startDateTime: 2025-03-10T00:00-04:00[US/Eastern], "
        + "endDateTime: 2025-03-11T00:00-04:00[US/Eastern], description: , "
        + "location: , isAllDay: true, isPrivate: false, repeatNumber: 5, "
        + "repeatEndDateTime: 9999-12-29T23:00-05:00[America/New_York], "
        + "subject: test, startDateTime: 2025-03-11T00:00-04:00[US/Eastern], "
        + "endDateTime: 2025-03-12T00:00-04:00[US/Eastern], description: , "
        + "location: , isAllDay: true, isPrivate: false, repeatNumber: 5, "
        + "repeatEndDateTime: 9999-12-29T23:00-05:00[America/New_York]]";
    assertEquals(expected, log.toString());
  }

  @Test
  public void testExecuteAllOptions() {
    String commandString = "create event test on 2025-03-02 repeats mtu for 5 times -d description"
        + " -p -l location";
    try {
      command.execute(model, view, commandString);
    } catch (Exception e) {
      fail(e.getMessage());
    }

    String expected = "Input: [subject: test, startDateTime: 2025-03-02T00:00-05:00[US/Eastern], "
        + "endDateTime: 2025-03-03T00:00-05:00[US/Eastern], description: description,"
        + " location: location, isAllDay: true, isPrivate: true, repeatNumber: 5, "
        + "repeatEndDateTime: 9999-12-29T23:00-05:00[America/New_York],"
        + " subject: test, startDateTime: 2025-03-03T00:00-05:00[US/Eastern],"
        + " endDateTime: 2025-03-04T00:00-05:00[US/Eastern], description: description, "
        + "location: location, isAllDay: true, isPrivate: true, repeatNumber: 5,"
        + " repeatEndDateTime: 9999-12-29T23:00-05:00[America/New_York], "
        + "subject: test, startDateTime: 2025-03-04T00:00-05:00[US/Eastern],"
        + " endDateTime: 2025-03-05T00:00-05:00[US/Eastern], description: description, "
        + "location: location, isAllDay: true, isPrivate: true, repeatNumber: 5, "
        + "repeatEndDateTime: 9999-12-29T23:00-05:00[America/New_York], subject: test, "
        + "startDateTime: 2025-03-09T00:00-05:00[US/Eastern], "
        + "endDateTime: 2025-03-10T00:00-04:00[US/Eastern], description: description, "
        + "location: location, isAllDay: true, isPrivate: true, repeatNumber: 5, "
        + "repeatEndDateTime: 9999-12-29T23:00-05:00[America/New_York], subject: test, "
        + "startDateTime: 2025-03-10T00:00-04:00[US/Eastern], "
        + "endDateTime: 2025-03-11T00:00-04:00[US/Eastern], description: description, "
        + "location: location, isAllDay: true, isPrivate: true, repeatNumber: 5, "
        + "repeatEndDateTime: 9999-12-29T23:00-05:00[America/New_York], subject: test,"
        + " startDateTime: 2025-03-11T00:00-04:00[US/Eastern], "
        + "endDateTime: 2025-03-12T00:00-04:00[US/Eastern], description: description, "
        + "location: location, isAllDay: true, isPrivate: true, repeatNumber: 5, "
        + "repeatEndDateTime: 9999-12-29T23:00-05:00[America/New_York]]";
    assertEquals(expected, log.toString());
  }

  @Test
  public void testViewPrint() {
    String commandString = "create event test on 2025-03-02 repeats mtu for 5 times";
    try {
      command.execute(model, view, commandString);
    } catch (Exception e) {
      fail(e.getMessage());
    }
    assertEquals("Created repeating event\n", out.toString());
  }
}