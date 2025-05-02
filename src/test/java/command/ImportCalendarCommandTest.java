package command;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import java.nio.file.Path;

import model.IModel;
import view.View;
import view.Viewer;
import mock.MockModel;

/**
 * A JUnit test class for the ImportCalendarCommand class. Uses the MockModel class to ensure that
 * the parameters are being sent correctly.
 */
public class ImportCalendarCommandTest {

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
    command = new ImportCalendarCommand();
  }

  @Test
  public void testConstructor() {
    Command command = new ImportCalendarCommand();
    assertNotNull(command);
  }

  @Test
  public void testExecute() {
    String filepath = Path.of("src","test", "resources", "export.csv").toString();
    String commandString = "import cal " + filepath;
    try {
      command.execute(model, view, commandString);
    } catch (Exception e) {
      fail(e.getMessage());
    }

    String expected = "Input: [subject: event, startDateTime: 2025-04-08T13:32-04:00[US/Eastern], "
            + "endDateTime: 2025-04-08T14:32-04:00[US/Eastern], description: , location: , "
            + "isAllDay: false, isPrivate: false]";
    assertEquals(expected, log.toString());
  }

  @Test
  public void testExecuteWithMockView() {
    String filepath = Path.of("src","test", "resources", "export.csv").toString();
    String commandString = "import cal " + filepath;
    try {
      command.execute(model, view, commandString);
    } catch (Exception e) {
      fail(e.getMessage());
    }

    String expected = "Input: [subject: event, startDateTime: 2025-04-08T13:32-04:00[US/Eastern], "
            + "endDateTime: 2025-04-08T14:32-04:00[US/Eastern], description: , location: , "
            + "isAllDay: false, isPrivate: false]";
    assertEquals(expected, log.toString());

    String expectedOut = "Finished importing events." + System.lineSeparator();
    assertEquals(expectedOut, out.toString());
  }
}