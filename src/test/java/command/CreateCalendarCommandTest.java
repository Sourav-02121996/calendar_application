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
 * A JUnit test class for the CreateCalendarCommand class. Uses the MockModel class to ensure that
 * the parameters are being sent correctly.
 */
public class CreateCalendarCommandTest {

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
    command = new CreateCalendarCommand();
  }

  @Test
  public void testExecute() {
    String commandString = "create calendar --name test --timezone America/New_York";
    try {
      command.execute(model, view, commandString);
    } catch (Exception e) {
      fail(e.getMessage());
    }

    String expected = "Input: name: test, timezone: America/New_York";
    assertEquals(expected, log.toString());

    String expectedOut = "Created calendar\n";
    assertEquals(expectedOut, out.toString());
  }

  @Test
  public void testExecuteInvalidTimezone() {
    String commandString = "create calendar --name test --timezone Invalid";
    try {
      command.execute(model, view, commandString);
    } catch (Exception e) {
      assertEquals("Invalid time zone: Invalid", e.getMessage());
    }
  }

  @Test
  public void testCommandWithQuotes() {
    String commandString = "create calendar --name \"new test\" --timezone America/New_York";
    try {
      command.execute(model, view, commandString);
    } catch (Exception e) {
      fail(e.getMessage());
    }
    String expected = "Input: name: new test, timezone: America/New_York";
    assertEquals(expected, log.toString());
  }
}