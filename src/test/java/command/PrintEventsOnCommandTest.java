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
 * A JUnit test class for the PrintEventsOnCommand class. Uses the MockModel class to ensure that
 * the parameters are being sent correctly.
 */
public class PrintEventsOnCommandTest {

  private Appendable out;
  private StringBuilder log;
  private IModel model;
  private Viewer view;
  private PrintEventsOnCommand command;

  @Before
  public void setUp() {
    out = new StringBuilder();
    log = new StringBuilder();
    model = new MockModel(log, 1234);
    view = new View(out);
    command = new PrintEventsOnCommand();
  }

  @Test
  public void testConstructor() {
    PrintEventsOnCommand command = new PrintEventsOnCommand();
    assertNotNull(command);
  }

  @Test
  public void testExecute() {
    try {
      String commandString = "print events on 2025-01-01";
      command.execute(model, view, commandString);
    } catch (Exception e) {
      fail(e.getMessage());
    }
    String expected = "Input: 2025-01-01T00:00-05:00[US/Eastern]";
    assertEquals(expected, log.toString());
  }

  @Test
  public void testExecuteWithPrintEvent() {
    try {
      String commandString = "print events on 2025-01-01";
      command.execute(model, view, commandString);
    } catch (Exception e) {
      fail(e.getMessage());
    }
    //As there are no events hence the out.toString() is empty
    assertEquals("\n", out.toString());
    String expected = "Input: 2025-01-01T00:00-05:00[US/Eastern]";
    assertEquals(expected, log.toString());
  }
}