package command;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import model.IModel;
import view.View;
import view.Viewer;
import mock.MockModel;

/**
 * A JUnit test class for the UseCalendarCommand class. Uses the MockModel class to ensure that the
 * parameters are being sent correctly.
 */
public class UseCalendarCommandTest {

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
    command = new UseCalendarCommand();
  }

  @Test
  public void testExecute() {
    String commandString = "use calendar --name test";
    try {
      command.execute(model, view, commandString);
    } catch (Exception e) {
      fail(e.getMessage());
    }

    String expected = "Input: test";
    assertEquals(expected, log.toString());

    String expectedOut = "Current calendar: test\n";
    assertEquals(expectedOut, out.toString());
  }

  @Test
  public void testCommandWithQuotes() {
    String commandString = "use calendar --name \"new calendar\"";
    try {
      command.execute(model, view, commandString);
    } catch (Exception e) {
      fail(e.getMessage());
    }

    String expected = "Input: new calendar";
    assertEquals(expected, log.toString());
  }
}