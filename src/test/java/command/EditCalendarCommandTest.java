package command;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import model.IModel;
import view.View;
import view.Viewer;
import mock.MockModel;

import org.junit.Before;
import org.junit.Test;

/**
 * A JUnit test class for testing the EditCalendarCommand class. Uses the MockModel class to ensure
 * that the parameters are being sent correctly.
 */
public class EditCalendarCommandTest {

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
    command = new EditCalendarCommand();
  }

  @Test
  public void testExecute() {
    String commandString = "edit calendar --name test --property name calendar";
    try {
      command.execute(model, view, commandString);
    } catch (Exception e) {
      fail(e.getMessage());
    }

    String expected = "Input: test - name - calendar";
    assertEquals(expected, log.toString());

    String expectedOut = "Edited calendar name\n";
    assertEquals(expectedOut, out.toString());
  }

  @Test
  public void testCommandWithQuotes() {
    String commandString = "edit calendar --name test --property name \"new calendar\"";
    try {
      command.execute(model, view, commandString);
    } catch (Exception e) {
      fail(e.getMessage());
    }

    String expected = "Input: test - name - new calendar";
    assertEquals(expected, log.toString());
  }

  @Test
  public void testCommandWithCalendarAndPropertyValueInQuotes() {
    String commandString = "edit calendar --name \"test cal\" --property name \"new cal\"";
    try {
      command.execute(model, view, commandString);
    } catch (Exception e) {
      fail(e.getMessage());
    }

    String expected = "Input: test cal - name - new cal";
    assertEquals(expected, log.toString());
  }
}
