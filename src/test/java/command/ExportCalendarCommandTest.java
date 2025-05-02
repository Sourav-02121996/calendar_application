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
 * A JUnit test class for the ExportCalendarCommand class. Uses the MockModel class to ensure that
 * the parameters are being sent correctly.
 */
public class ExportCalendarCommandTest {

  private Appendable out;
  private IModel model;
  private Viewer view;
  private Command command;

  @Before
  public void setUp() {
    out = new StringBuilder();
    StringBuilder log = new StringBuilder();
    model = new MockModel(log, 1234);
    view = new View(out);
    command = new ExportCalendarCommand();
  }

  @Test
  public void testConstructor() {
    ExportCalendarCommand command = new ExportCalendarCommand();
    assertNotNull(command);
  }

  @Test
  public void testExecute() {
    String commandString = "export cal export.csv";
    try {
      command.execute(model, view, commandString);
    } catch (Exception e) {
      fail(e.getMessage());
    }

    String filepath = Path.of(System.getProperty("user.home"), "Downloads", "export.csv")
            .toString();
    String expected = "Exported calendar to " + filepath + System.lineSeparator();
    assertEquals(expected, out.toString());
  }
}