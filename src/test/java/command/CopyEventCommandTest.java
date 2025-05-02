package command;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import model.IModel;
import view.Viewer;
import mock.MockModel;
import view.View;

import org.junit.Before;
import org.junit.Test;

/**
 * A JUnit test class for testing the CopyEventCommand class. Uses the MockModel class to ensure
 * that the parameters are being sent correctly.
 */
public class CopyEventCommandTest {

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
    command = new CopyEventCommand();
  }

  @Test
  public void testExecute() {
    String commandString = "copy event a on 2025-03-16T12:00 --target b to 2025-03-17T12:00";
    try {
      command.execute(model, view, commandString);
    } catch (Exception e) {
      fail(e.getMessage());
    }

    String expected = "Input: a - b - 2025-03-16T12:00 - 2025-03-17T12:00";
    assertEquals(expected, log.toString());

    String expectedOut = "Copied event to calendar b\n";
    assertEquals(expectedOut, out.toString());
  }

  @Test
  public void testCommandWithQuotes() {
    String commandString = "copy event \"a b\" on 2025-03-16T12:00 --target \"c d\" "
        + "to 2025-03-17T12:00";
    try {
      command.execute(model, view, commandString);
    } catch (Exception e) {
      fail(e.getMessage());
    }

    String expected = "Input: a b - c d - 2025-03-16T12:00 - 2025-03-17T12:00";
    assertEquals(expected, log.toString());
  }
}