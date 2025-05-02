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
 * A JUnit test class for testing the CopyEventsOnDateCommand class. Uses the MockModel class to
 * ensure that the parameters are being sent correctly.
 */
public class CopyEventsOnDateCommandTest {

  private Appendable out;
  private StringBuilder log;
  private IModel model;
  private Viewer view;
  private Command command;

  @Before
  public void setUp() throws Exception {
    out = new StringBuilder();
    log = new StringBuilder();
    model = new MockModel(log, 1234);
    view = new View(out);
    command = new CopyEventsOnDateCommand();
  }

  @Test
  public void testExecute() {
    String commandString = "copy events on 2025-03-01 --target cal to 2025-05-01";
    try {
      command.execute(model, view, commandString);
    } catch (Exception e) {
      fail(e.getMessage());
    }

    String expected = "Input: 2025-03-01 - 2025-05-01 - cal";
    assertEquals(expected, log.toString());

    String expectedOut = "Copied events to calendar cal\n";
    assertEquals(expectedOut, out.toString());
  }

  @Test
  public void testCommandWithQuotes() {
    String commandString = "copy events on 2025-03-01 --target \"Team meeting\" to 2025-05-01";
    try {
      command.execute(model, view, commandString);
    } catch (Exception e) {
      fail(e.getMessage());
    }
    String expected = "Input: 2025-03-01 - 2025-05-01 - Team meeting";
    assertEquals(expected, log.toString());
  }
}
