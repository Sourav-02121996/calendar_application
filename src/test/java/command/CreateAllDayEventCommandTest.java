package command;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import model.IModel;
import view.View;
import view.Viewer;
import mock.MockModel;

/**
 * A JUnit test class for the CreateAllDayEventCommand class. Uses the MockModel class to ensure
 * that the parameters are being sent correctly.
 */
public class CreateAllDayEventCommandTest {

  private StringBuilder log;
  private IModel model;
  private Viewer view;
  private Command command;

  @Before
  public void setUp() {
    log = new StringBuilder();
    model = new MockModel(log, 1234);
    view = new View(new StringBuilder());
    command = new CreateAllDayEventCommand();
  }

  @Test
  public void testConstructor() {
    CreateAllDayEventCommand command = new CreateAllDayEventCommand();
    assertNotNull(command);
  }

  @Test
  public void testExecute() {
    String commandString = "create event test on 2024-03-02T02:30";
    try {
      command.execute(model, view, commandString);
    } catch (Exception e) {
      fail(e.getMessage());
    }

    String expected = "Input: [subject: test, startDateTime: 2024-03-02T00:00-05:00[US/Eastern], "
        + "endDateTime: 2024-03-03T00:00-05:00[US/Eastern], description: , location: , "
        + "isAllDay: true, isPrivate: false]";
    assertEquals(expected, log.toString());
  }

  @Test
  public void testExecuteAllOptionsMultipleWords() {
    String commandString = "create event \"test subject\" on 2024-03-02T02:30 -d "
        + "\"description string!\" -p -l \"location, string\"";

    try {
      command.execute(model, view, commandString);
    } catch (Exception e) {
      fail(e.getMessage());
    }

    String expected = "Input: [subject: test subject, "
        + "startDateTime: 2024-03-02T00:00-05:00[US/Eastern], "
        + "endDateTime: 2024-03-03T00:00-05:00[US/Eastern], description: description string!, "
        + "location: location, string, isAllDay: true, isPrivate: true]";
    assertEquals(expected, log.toString());
  }

  @Test
  public void testExecuteCaseInsensitive() {
    String commandString = "Create Event test ON 2024-03-02T02:30";
    try {
      command.execute(model, view, commandString);
    } catch (Exception e) {
      fail(e.getMessage());
    }

    String expected = "Input: [subject: test, startDateTime: 2024-03-02T00:00-05:00[US/Eastern], "
        + "endDateTime: 2024-03-03T00:00-05:00[US/Eastern], description: , location: , "
        + "isAllDay: true, isPrivate: false]";
    assertEquals(expected, log.toString());
  }
}