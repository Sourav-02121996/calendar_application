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
 * A JUnit test class for the CreateAllDayRepeatingEventsUntilEndCommandTest class. Uses the
 * MockModel class to ensure that the parameters are being sent correctly.
 */
public class CreateAllDayRepeatingEventsUntilEndCommandTest {

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
    command = new CreateAllDayRepeatingEventsUntilEndCommand();
  }

  @Test
  public void testConstructor() {
    CreateAllDayRepeatingEventsUntilEndCommand command =
        new CreateAllDayRepeatingEventsUntilEndCommand();
    assertNotNull(command);
  }

  @Test
  public void testExecute() throws IOException {
    String commandString = "create event test on 2025-03-02 repeats mtu until 2025-03-09";
    try {
      command.execute(model, view, commandString);
    } catch (Exception e) {
      fail(e.getMessage());
    }

    String expected = "Input: [subject: test, startDateTime: 2025-03-02T00:00-05:00[US/Eastern], "
        + "endDateTime: 2025-03-03T00:00-05:00[US/Eastern], description: , location: , "
        + "isAllDay: true, isPrivate: false, repeatNumber: 0, "
        + "repeatEndDateTime: 2025-03-09T00:00-05:00[US/Eastern], "
        + "subject: test, startDateTime: 2025-03-03T00:00-05:00[US/Eastern], "
        + "endDateTime: 2025-03-04T00:00-05:00[US/Eastern], description: , "
        + "location: , isAllDay: true, isPrivate: false, repeatNumber: 0, "
        + "repeatEndDateTime: 2025-03-09T00:00-05:00[US/Eastern], "
        + "subject: test, startDateTime: 2025-03-04T00:00-05:00[US/Eastern], "
        + "endDateTime: 2025-03-05T00:00-05:00[US/Eastern], description: , "
        + "location: , isAllDay: true, isPrivate: false, repeatNumber: 0, "
        + "repeatEndDateTime: 2025-03-09T00:00-05:00[US/Eastern], "
        + "subject: test, startDateTime: 2025-03-09T00:00-05:00[US/Eastern], "
        + "endDateTime: 2025-03-10T00:00-04:00[US/Eastern], description: , location: , "
        + "isAllDay: true, isPrivate: false, repeatNumber: 0, "
        + "repeatEndDateTime: 2025-03-09T00:00-05:00[US/Eastern]]";
    assertEquals(expected, log.toString());
  }

  @Test
  public void testExecuteDuplicateChars() {
    String commandString = "create event test on 2025-03-02 repeats mtmu until 2025-03-09";
    try {
      command.execute(model, view, commandString);
    } catch (Exception e) {
      fail(e.getMessage());
    }

    String expected = "Input: [subject: test, startDateTime: 2025-03-02T00:00-05:00[US/Eastern],"
        + " endDateTime: 2025-03-03T00:00-05:00[US/Eastern], description: , location: ,"
        + " isAllDay: true, isPrivate: false, repeatNumber: 0,"
        + " repeatEndDateTime: 2025-03-09T00:00-05:00[US/Eastern], subject: test,"
        + " startDateTime: 2025-03-03T00:00-05:00[US/Eastern],"
        + " endDateTime: 2025-03-04T00:00-05:00[US/Eastern], description: , location: ,"
        + " isAllDay: true, isPrivate: false, repeatNumber: 0,"
        + " repeatEndDateTime: 2025-03-09T00:00-05:00[US/Eastern], subject: test,"
        + " startDateTime: 2025-03-04T00:00-05:00[US/Eastern],"
        + " endDateTime: 2025-03-05T00:00-05:00[US/Eastern], description: , location: ,"
        + " isAllDay: true, isPrivate: false, repeatNumber: 0,"
        + " repeatEndDateTime: 2025-03-09T00:00-05:00[US/Eastern], subject: test,"
        + " startDateTime: 2025-03-09T00:00-05:00[US/Eastern],"
        + " endDateTime: 2025-03-10T00:00-04:00[US/Eastern], description: , location: ,"
        + " isAllDay: true, isPrivate: false, repeatNumber: 0,"
        + " repeatEndDateTime: 2025-03-09T00:00-05:00[US/Eastern]]";
    assertEquals(expected, log.toString());
  }

  @Test
  public void testExecuteEmptySubject() {
    String commandString = "create event \" \" on 2025-03-02 repeats mtmu until 2025-03-09";
    try {
      command.execute(model, view, commandString);
    } catch (Exception e) {
      assertEquals("Subject cannot be empty", e.getMessage());
    }
  }

  @Test
  public void testExecuteMultipleWords() {
    String commandString = "create event \"test name\" on 2025-03-02 repeats MTU until 2025-03-09"
        + " -l \"loc ation\"";
    try {
      command.execute(model, view, commandString);
    } catch (Exception e) {
      fail(e.getMessage());
    }

    String expected = "Input: [subject: test name, "
        + "startDateTime: 2025-03-02T00:00-05:00[US/Eastern], "
        + "endDateTime: 2025-03-03T00:00-05:00[US/Eastern], description: , location: loc ation, "
        + "isAllDay: true, isPrivate: false, repeatNumber: 0, "
        + "repeatEndDateTime: 2025-03-09T00:00-05:00[US/Eastern], subject: test name, "
        + "startDateTime: 2025-03-03T00:00-05:00[US/Eastern], "
        + "endDateTime: 2025-03-04T00:00-05:00[US/Eastern], description: , location: loc ation, "
        + "isAllDay: true, isPrivate: false, repeatNumber: 0, "
        + "repeatEndDateTime: 2025-03-09T00:00-05:00[US/Eastern], subject: test name, "
        + "startDateTime: 2025-03-04T00:00-05:00[US/Eastern], "
        + "endDateTime: 2025-03-05T00:00-05:00[US/Eastern], description: , location: loc ation, "
        + "isAllDay: true, isPrivate: false, repeatNumber: 0, "
        + "repeatEndDateTime: 2025-03-09T00:00-05:00[US/Eastern], subject: test name, "
        + "startDateTime: 2025-03-09T00:00-05:00[US/Eastern], "
        + "endDateTime: 2025-03-10T00:00-04:00[US/Eastern], description: , location: loc ation, "
        + "isAllDay: true, isPrivate: false, repeatNumber: 0, "
        + "repeatEndDateTime: 2025-03-09T00:00-05:00[US/Eastern]]";
    assertEquals(expected, log.toString());
  }

  @Test
  public void testExecuteMultipleWordsWithoutQuotes() {
    String commandString = "create event test name on 2025-03-02 repeats MTU until 2025-09-02";
    try {
      boolean result = command.execute(model, view, commandString);
      assertFalse(result);
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  @Test
  public void testExecuteCaseInsensitive() {
    String commandString = "create Event \"test name\" ON 2025-03-02 repeATs MTU until 2025-03-09";
    try {
      boolean result = command.execute(model, view, commandString);
      assertTrue(result);
    } catch (Exception e) {
      fail(e.getMessage());
    }

    String expected = "Input: [subject: test name, "
        + "startDateTime: 2025-03-02T00:00-05:00[US/Eastern], "
        + "endDateTime: 2025-03-03T00:00-05:00[US/Eastern], description: , location: , "
        + "isAllDay: true, isPrivate: false, repeatNumber: 0, "
        + "repeatEndDateTime: 2025-03-09T00:00-05:00[US/Eastern], subject: test name, "
        + "startDateTime: 2025-03-03T00:00-05:00[US/Eastern], "
        + "endDateTime: 2025-03-04T00:00-05:00[US/Eastern], description: , location: , "
        + "isAllDay: true, isPrivate: false, repeatNumber: 0, "
        + "repeatEndDateTime: 2025-03-09T00:00-05:00[US/Eastern], subject: test name, "
        + "startDateTime: 2025-03-04T00:00-05:00[US/Eastern], "
        + "endDateTime: 2025-03-05T00:00-05:00[US/Eastern], description: , location: , "
        + "isAllDay: true, isPrivate: false, repeatNumber: 0, "
        + "repeatEndDateTime: 2025-03-09T00:00-05:00[US/Eastern], "
        + "subject: test name, startDateTime: 2025-03-09T00:00-05:00[US/Eastern], "
        + "endDateTime: 2025-03-10T00:00-04:00[US/Eastern], description: , location: , "
        + "isAllDay: true, isPrivate: false, repeatNumber: 0, "
        + "repeatEndDateTime: 2025-03-09T00:00-05:00[US/Eastern]]";
    assertEquals(expected, log.toString());
  }

  @Test
  public void testExecuteAllOptions() {
    String commandString = "create event \"test name\" on 2025-03-02 repeats MTU until 2025-03-09"
        + " -d description -p -l location";
    try {
      command.execute(model, view, commandString);
    } catch (Exception e) {
      fail(e.getMessage());
    }

    String expected = "Input: [subject: test name, "
        + "startDateTime: 2025-03-02T00:00-05:00[US/Eastern], "
        + "endDateTime: 2025-03-03T00:00-05:00[US/Eastern], "
        + "description: description, location: location, isAllDay: true, isPrivate: true, "
        + "repeatNumber: 0, repeatEndDateTime: 2025-03-09T00:00-05:00[US/Eastern], "
        + "subject: test name, startDateTime: 2025-03-03T00:00-05:00[US/Eastern], "
        + "endDateTime: 2025-03-04T00:00-05:00[US/Eastern], description: description, "
        + "location: location, isAllDay: true, isPrivate: true, repeatNumber: 0, "
        + "repeatEndDateTime: 2025-03-09T00:00-05:00[US/Eastern], subject: test name, "
        + "startDateTime: 2025-03-04T00:00-05:00[US/Eastern], "
        + "endDateTime: 2025-03-05T00:00-05:00[US/Eastern], description: description, "
        + "location: location, isAllDay: true, isPrivate: true, repeatNumber: 0, "
        + "repeatEndDateTime: 2025-03-09T00:00-05:00[US/Eastern], subject: test name, "
        + "startDateTime: 2025-03-09T00:00-05:00[US/Eastern], "
        + "endDateTime: 2025-03-10T00:00-04:00[US/Eastern], description: description, "
        + "location: location, isAllDay: true, isPrivate: true, repeatNumber: 0, "
        + "repeatEndDateTime: 2025-03-09T00:00-05:00[US/Eastern]]";
    assertEquals(expected, log.toString());
  }

  @Test
  public void testViewPrint() {
    String commandString = "create event test on 2025-03-02 repeats mtu until 2025-03-09";
    try {
      command.execute(model, view, commandString);
    } catch (Exception e) {
      fail(e.getMessage());
    }
    System.out.println(out.toString());
    assertEquals("Created repeating event\n", out.toString());
  }
}