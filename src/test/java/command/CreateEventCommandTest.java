package command;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import model.IModel;
import view.View;
import view.Viewer;
import mock.MockModel;

/**
 * A JUnit test class for the CreateEventCommand class. Uses the MockModel class to ensure that the
 * parameters are being sent correctly.
 */
public class CreateEventCommandTest {

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
    command = new CreateEventCommand();
  }

  @Test
  public void testConstructor() {
    CreateEventCommand command = new CreateEventCommand();
    assertNotNull(command);
  }

  @Test
  public void testExecute() {
    String commandString = "create event test from 2024-03-02T02:30 to 2024-03-02T04:30";
    try {
      command.execute(model, view, commandString);
    } catch (Exception e) {
      System.err.println(e.getMessage());
      fail();
    }

    String expected = "Input: [subject: test, startDateTime: 2024-03-02T02:30-05:00[US/Eastern], "
        + "endDateTime: 2024-03-02T04:30-05:00[US/Eastern], description: , location: , "
        + "isAllDay: false, isPrivate: false]";
    assertEquals(expected, log.toString());
  }

  @Test
  public void testExecuteCaseInsensitive() {
    String commandString = "Create Event test frOm 2024-03-02T02:30 to 2024-03-02T04:30";
    try {
      command.execute(model, view, commandString);
    } catch (Exception e) {
      fail(e.getMessage());
    }

    String expected = "Input: [subject: test, startDateTime: 2024-03-02T02:30-05:00[US/Eastern], "
        + "endDateTime: 2024-03-02T04:30-05:00[US/Eastern], description: , location: , "
        + "isAllDay: false, isPrivate: false]";
    assertEquals(expected, log.toString());
  }

  @Test
  public void testExecuteSpecialCharacters() {
    String commandString = "Create Event test! frOm 2024-03-02T02:30 to 2024-03-02T04:30";
    try {
      command.execute(model, view, commandString);
    } catch (Exception e) {
      fail(e.getMessage());
    }

    String expected = "Input: [subject: test!, startDateTime: 2024-03-02T02:30-05:00[US/Eastern], "
        + "endDateTime: 2024-03-02T04:30-05:00[US/Eastern], description: , location: , "
        + "isAllDay: false, isPrivate: false]";
    assertEquals(expected, log.toString());
  }

  @Test
  public void testExecuteMultipleWords() {
    String commandString = "create event test name from 2024-03-02T02:30 to 2024-03-02T04:30";
    try {
      boolean result = command.execute(model, view, commandString);
      assertFalse(result);
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  @Test
  public void testExecuteEmptyString() {
    String commandString = "create event \"\" from 2024-03-02T02:30 to 2024-03-02T04:30";
    try {
      boolean result = command.execute(model, view, commandString);
      assertFalse(result);
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void testExecuteWhitespaceString() throws Exception {
    String commandString = "create event \" \" from 2024-03-02T02:30 to 2024-03-02T04:30";
    command.execute(model, view, commandString);
  }

  @Test
  public void testExecuteMultipleWordsWithQuotes() {
    String commandString = "create event \"test name\" from 2024-03-02T02:30 to 2024-03-02T04:30";
    try {
      boolean result = command.execute(model, view, commandString);
      assertTrue(result);
    } catch (Exception e) {
      fail(e.getMessage());
    }

    String expected = "Input: [subject: test name, startDateTime: "
        + "2024-03-02T02:30-05:00[US/Eastern], endDateTime: "
        + "2024-03-02T04:30-05:00[US/Eastern], description: , location: , isAllDay: false, "
        + "isPrivate: false]";
    assertEquals(expected, log.toString());
  }

  @Test
  public void testExecuteAllOptions() {
    String commandString = "create event test from 2024-03-02T02:30 to "
        + "2024-03-02T04:30 -d description -p -l location";
    try {
      command.execute(model, view, commandString);
    } catch (Exception e) {
      fail(e.getMessage());
    }

    String expected = "Input: [subject: test, startDateTime: 2024-03-02T02:30-05:00[US/Eastern], "
        + "endDateTime: 2024-03-02T04:30-05:00[US/Eastern], description: description, "
        + "location: location, isAllDay: false, isPrivate: true]";
    assertEquals(expected, log.toString());
  }

  @Test
  public void testViewPrint() {
    String commandString = "create event test from 2024-03-02T02:30 to 2024-03-02T04:30";
    try {
      command.execute(model, view, commandString);
    } catch (Exception e) {
      System.err.println(e.getMessage());
      fail();
    }
    assertEquals("Created event\n", out.toString());
  }
}