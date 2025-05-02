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
 * A JUnit test class for the PrintEventsInRangeCommand class. Uses the MockModel class to ensure
 * that the parameters are being sent correctly.
 */
public class PrintEventsInRangeCommandTest {

  private StringBuilder log;
  private IModel model;
  private Viewer view;
  private Command command;

  @Before
  public void setUp() {
    log = new StringBuilder();
    model = new MockModel(log, 1234);
    view = new View(new StringBuilder());
    command = new PrintEventsInRangeCommand();
  }

  @Test
  public void testConstructor() {
    PrintEventsInRangeCommand command = new PrintEventsInRangeCommand();
    assertNotNull(command);
  }

  @Test
  public void testExecute() {
    try {
      String commandString = "print events from 2025-01-01T10:00 to 2025-01-03T12:00";
      command.execute(model, view, commandString);
    } catch (Exception e) {
      fail(e.getMessage());
    }

    String expected = "Input: 2025-01-01T10:00-05:00[US/Eastern] "
        + "- 2025-01-03T12:00-05:00[US/Eastern]";
    assertEquals(expected, log.toString());
  }
}