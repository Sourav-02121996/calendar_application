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
 * A JUnit class for testing the ShowStatusCommand class. Uses the MockModel class to ensure that
 * the parameters are being sent correctly.
 */
public class ShowStatusCommandTest {

  private StringBuilder log;
  private IModel model;
  private Viewer view;
  private Command command;

  @Before
  public void setUp() {
    log = new StringBuilder();
    model = new MockModel(log, 1234);
    view = new View(new StringBuilder());
    command = new ShowStatusCommand();
  }

  @Test
  public void testConstructor() {
    ShowStatusCommand command = new ShowStatusCommand();
    assertNotNull(command);
  }

  @Test
  public void testExecute() {
    String commandString = "show status on 2024-03-02T02:30";
    try {
      command.execute(model, view, commandString);
    } catch (Exception e) {
      fail(e.getMessage());
    }

    String expected = "Input: 2024-03-02T02:30";
    assertEquals(expected, log.toString());
  }
}