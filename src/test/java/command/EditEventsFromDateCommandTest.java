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
 * A JUnit test class for testing the EditEventsFromDateCommand class. Uses the MockModel class to
 * ensure that the parameters are being sent correctly.
 */
public class EditEventsFromDateCommandTest {

  private StringBuilder log;
  private IModel model;
  private Viewer view;
  private Command command;

  @Before
  public void setUp() {
    log = new StringBuilder();
    model = new MockModel(log, 1234);
    view = new View(new StringBuilder());
    command = new EditEventsFromDateCommand();
  }

  @Test
  public void testConstructor() {
    EditEventsFromDateCommand command = new EditEventsFromDateCommand();
    assertNotNull(command);
  }

  @Test
  public void testExecute() {
    try {
      String commandString = "edit events location test from 2025-01-01T08:00 with Boston ";
      command.execute(model, view, commandString);
    } catch (Exception e) {
      fail(e.getMessage());
    }

    String expected = "Input: location - test - 2025-01-01T08:00 - Boston";
    assertEquals(expected, log.toString());
  }
}