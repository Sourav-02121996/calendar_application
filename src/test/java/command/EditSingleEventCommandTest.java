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
 * A JUnit test class for testing the EditSingleEventCommandTest class. Uses the MockModel class to
 * ensure that the parameters are being sent correctly.
 */
public class EditSingleEventCommandTest {

  private StringBuilder log;
  private IModel model;
  private Viewer view;
  private Command command;

  @Before
  public void setUp() {
    log = new StringBuilder();
    model = new MockModel(log, 1234);
    view = new View(new StringBuilder());
    command = new EditSingleEventCommand();
  }

  @Test
  public void testConstructor() {
    EditSingleEventCommand command = new EditSingleEventCommand();
    assertNotNull(command);
  }

  @Test
  public void testExecute() {
    try {
      String commandString = "edit event location test from 2025-01-01T10:00 to"
          + " 2025-01-01T16:00 with Boston ";
      command.execute(model, view, commandString);
    } catch (Exception e) {
      fail(e.getMessage());
    }

    String expected = "Input: location - test - 2025-01-01T10:00 - 2025-01-01T16:00 - Boston";
    assertEquals(expected, log.toString());
  }

  @Test
  public void testEditDescriptionEvent() {
    try {
      String commandString = "edit event description test from 2025-01-01T10:00 to"
          + " 2025-01-01T16:00 with Workshop ";
      command.execute(model, view, commandString);
    } catch (Exception e) {
      fail();
    }

    String expected = "Input: description - test - 2025-01-01T10:00 - 2025-01-01T16:00 - Workshop";
    assertEquals(expected, log.toString());
  }

  @Test
  public void testEditSetStartDateForSingleEvent() {
    try {
      String commandString = "edit event startDateTime test from 2025-01-01T10:00 to"
          + " 2025-01-01T16:00 with 2025-01-01T13:00 ";
      command.execute(model, view, commandString);
    } catch (Exception e) {
      fail(e.getMessage());
    }

    String expected = "Input: startDateTime - test - 2025-01-01T10:00 - 2025-01-01T16:00 "
        + "- 2025-01-01T13:00";
    assertEquals(expected, log.toString());
  }
}