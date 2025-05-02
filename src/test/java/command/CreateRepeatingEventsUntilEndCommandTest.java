package command;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
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
 * A JUnit test class for the CreateRepeatingEventsUntilEndCommand class. Uses the MockModel class
 * to ensure that the parameters are being sent correctly.
 */
public class CreateRepeatingEventsUntilEndCommandTest {

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
    command = new CreateRepeatingEventsUntilEndCommand();
  }

  @Test
  public void testConstructor() {
    CreateRepeatingEventsUntilEndCommand command = new CreateRepeatingEventsUntilEndCommand();
    assertNotNull(command);
  }

  @Test
  public void testExecute() throws IOException {
    String commandString = "create event test from 2025-03-02T12:30 to 2025-03-02T13:30 "
        + "repeats mtu until 2025-03-09T04:30";
    try {
      command.execute(model, view, commandString);
    } catch (Exception e) {
      fail(e.getMessage());
    }

    String expected = "Input: [subject: test, startDateTime: 2025-03-02T12:30-05:00[US/Eastern],"
        + " endDateTime: 2025-03-02T13:30-05:00[US/Eastern], description: , location: ,"
        + " isAllDay: false, isPrivate: false, repeatNumber: 0,"
        + " repeatEndDateTime: 2025-03-09T04:30-04:00[US/Eastern], subject: test,"
        + " startDateTime: 2025-03-03T12:30-05:00[US/Eastern],"
        + " endDateTime: 2025-03-03T13:30-05:00[US/Eastern], description: , location: ,"
        + " isAllDay: false, isPrivate: false, repeatNumber: 0,"
        + " repeatEndDateTime: 2025-03-09T04:30-04:00[US/Eastern], subject: test,"
        + " startDateTime: 2025-03-04T12:30-05:00[US/Eastern],"
        + " endDateTime: 2025-03-04T13:30-05:00[US/Eastern], description: , location: ,"
        + " isAllDay: false, isPrivate: false, repeatNumber: 0,"
        + " repeatEndDateTime: 2025-03-09T04:30-04:00[US/Eastern], subject: test,"
        + " startDateTime: 2025-03-09T12:30-04:00[US/Eastern],"
        + " endDateTime: 2025-03-09T13:30-04:00[US/Eastern], description: , location: ,"
        + " isAllDay: false, isPrivate: false, repeatNumber: 0,"
        + " repeatEndDateTime: 2025-03-09T04:30-04:00[US/Eastern]]";
    assertEquals(expected, log.toString());
  }

  @Test
  public void testExecuteDuplicateChars() {
    String commandString = "create event test from 2025-03-02T12:30 to 2025-03-02T13:30 "
        + "repeats mtmu until 2025-03-09T04:30";
    try {
      command.execute(model, view, commandString);
    } catch (Exception e) {
      fail(e.getMessage());
    }

    String expected = "Input: [subject: test, startDateTime: 2025-03-02T12:30-05:00[US/Eastern],"
        + " endDateTime: 2025-03-02T13:30-05:00[US/Eastern], description: , location: ,"
        + " isAllDay: false, isPrivate: false, repeatNumber: 0,"
        + " repeatEndDateTime: 2025-03-09T04:30-04:00[US/Eastern], subject: test,"
        + " startDateTime: 2025-03-03T12:30-05:00[US/Eastern],"
        + " endDateTime: 2025-03-03T13:30-05:00[US/Eastern], description: , location: ,"
        + " isAllDay: false, isPrivate: false, repeatNumber: 0,"
        + " repeatEndDateTime: 2025-03-09T04:30-04:00[US/Eastern], subject: test,"
        + " startDateTime: 2025-03-04T12:30-05:00[US/Eastern],"
        + " endDateTime: 2025-03-04T13:30-05:00[US/Eastern], description: , location: ,"
        + " isAllDay: false, isPrivate: false, repeatNumber: 0,"
        + " repeatEndDateTime: 2025-03-09T04:30-04:00[US/Eastern], subject: test,"
        + " startDateTime: 2025-03-09T12:30-04:00[US/Eastern],"
        + " endDateTime: 2025-03-09T13:30-04:00[US/Eastern], description: , location: ,"
        + " isAllDay: false, isPrivate: false, repeatNumber: 0,"
        + " repeatEndDateTime: 2025-03-09T04:30-04:00[US/Eastern]]";
    assertEquals(expected, log.toString());
  }

  @Test
  public void testExecuteMultipleWords() {
    String commandString = "create event \"test name\" from 2025-03-02T12:30 to 2025-03-02T13:30 "
        + "repeats mtu until 2025-03-09T04:30";
    try {
      command.execute(model, view, commandString);
    } catch (Exception e) {
      fail(e.getMessage());
    }

    String expected = "Input: [subject: test name,"
        + " startDateTime: 2025-03-02T12:30-05:00[US/Eastern],"
        + " endDateTime: 2025-03-02T13:30-05:00[US/Eastern], description: , location: ,"
        + " isAllDay: false, isPrivate: false, repeatNumber: 0,"
        + " repeatEndDateTime: 2025-03-09T04:30-04:00[US/Eastern], subject: test name,"
        + " startDateTime: 2025-03-03T12:30-05:00[US/Eastern],"
        + " endDateTime: 2025-03-03T13:30-05:00[US/Eastern], description: , location: ,"
        + " isAllDay: false, isPrivate: false, repeatNumber: 0,"
        + " repeatEndDateTime: 2025-03-09T04:30-04:00[US/Eastern], subject: test name,"
        + " startDateTime: 2025-03-04T12:30-05:00[US/Eastern],"
        + " endDateTime: 2025-03-04T13:30-05:00[US/Eastern], description: , location: ,"
        + " isAllDay: false, isPrivate: false, repeatNumber: 0,"
        + " repeatEndDateTime: 2025-03-09T04:30-04:00[US/Eastern], subject: test name,"
        + " startDateTime: 2025-03-09T12:30-04:00[US/Eastern],"
        + " endDateTime: 2025-03-09T13:30-04:00[US/Eastern], description: , location: ,"
        + " isAllDay: false, isPrivate: false, repeatNumber: 0,"
        + " repeatEndDateTime: 2025-03-09T04:30-04:00[US/Eastern]]";
    assertEquals(expected, log.toString());
  }

  @Test
  public void testExecuteMultipleWordsWithoutQuotes() {
    String commandString = "create event test name from 2025-03-02T02:30 to 2025-03-02T03:30 "
        + "repeats mtu until 2025-03-09T04:30";
    try {
      boolean result = command.execute(model, view, commandString);
      assertFalse(result);
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  @Test
  public void testExecuteInvalidChars() {
    String commandString = "create event test name from 2025-03-02T02:30 to 2025-03-02T03:30 "
        + "repeats mtLu until 2025-03-09T04:30";
    try {
      boolean result = command.execute(model, view, commandString);
      assertFalse(result);
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  @Test
  public void testExecuteCaseInsensitive() {
    String commandString = "create Event test from 2025-03-02T12:30 to 2025-03-02T13:30 "
        + "repeats mtu until 2025-03-09T04:30";
    try {
      boolean result = command.execute(model, view, commandString);
      assertTrue(result);
    } catch (Exception e) {
      fail(e.getMessage());
    }

    String expected = "Input: [subject: test, startDateTime: 2025-03-02T12:30-05:00[US/Eastern],"
        + " endDateTime: 2025-03-02T13:30-05:00[US/Eastern], description: , location: ,"
        + " isAllDay: false, isPrivate: false, repeatNumber: 0,"
        + " repeatEndDateTime: 2025-03-09T04:30-04:00[US/Eastern], subject: test,"
        + " startDateTime: 2025-03-03T12:30-05:00[US/Eastern],"
        + " endDateTime: 2025-03-03T13:30-05:00[US/Eastern], description: , location: ,"
        + " isAllDay: false, isPrivate: false, repeatNumber: 0,"
        + " repeatEndDateTime: 2025-03-09T04:30-04:00[US/Eastern], subject: test,"
        + " startDateTime: 2025-03-04T12:30-05:00[US/Eastern],"
        + " endDateTime: 2025-03-04T13:30-05:00[US/Eastern], description: , location: ,"
        + " isAllDay: false, isPrivate: false, repeatNumber: 0,"
        + " repeatEndDateTime: 2025-03-09T04:30-04:00[US/Eastern], subject: test,"
        + " startDateTime: 2025-03-09T12:30-04:00[US/Eastern],"
        + " endDateTime: 2025-03-09T13:30-04:00[US/Eastern], description: , location: ,"
        + " isAllDay: false, isPrivate: false, repeatNumber: 0,"
        + " repeatEndDateTime: 2025-03-09T04:30-04:00[US/Eastern]]";
    assertEquals(expected, log.toString());
  }

  @Test
  public void testExecuteAllOptions() {
    String commandString = "create event test from 2025-03-02T12:30 to 2025-03-02T13:30 "
        + "repeats mtu until 2025-03-09T04:30 -d description -p -l location";
    try {
      command.execute(model, view, commandString);
    } catch (Exception e) {
      fail(e.getMessage());
    }

    String expected = "Input: [subject: test, startDateTime: 2025-03-02T12:30-05:00[US/Eastern],"
        + " endDateTime: 2025-03-02T13:30-05:00[US/Eastern], description: description,"
        + " location: location, isAllDay: false, isPrivate: true, repeatNumber: 0,"
        + " repeatEndDateTime: 2025-03-09T04:30-04:00[US/Eastern], subject: test,"
        + " startDateTime: 2025-03-03T12:30-05:00[US/Eastern],"
        + " endDateTime: 2025-03-03T13:30-05:00[US/Eastern], description: description,"
        + " location: location, isAllDay: false, isPrivate: true, repeatNumber: 0,"
        + " repeatEndDateTime: 2025-03-09T04:30-04:00[US/Eastern], subject: test,"
        + " startDateTime: 2025-03-04T12:30-05:00[US/Eastern],"
        + " endDateTime: 2025-03-04T13:30-05:00[US/Eastern], description: description,"
        + " location: location, isAllDay: false, isPrivate: true, repeatNumber: 0,"
        + " repeatEndDateTime: 2025-03-09T04:30-04:00[US/Eastern], subject: test,"
        + " startDateTime: 2025-03-09T12:30-04:00[US/Eastern],"
        + " endDateTime: 2025-03-09T13:30-04:00[US/Eastern], description: description,"
        + " location: location, isAllDay: false, isPrivate: true, repeatNumber: 0,"
        + " repeatEndDateTime: 2025-03-09T04:30-04:00[US/Eastern]]";
    assertEquals(expected, log.toString());
  }

  @Test
  public void testViewPrint() {
    String commandString = "create event test from 2025-03-02T12:30 to 2025-03-02T13:30 "
        + "repeats mtu until 2025-03-09T04:30";
    try {
      command.execute(model, view, commandString);
    } catch (Exception e) {
      fail(e.getMessage());
    }
    assertEquals("Created repeating event\n", out.toString());
  }
}