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
 * A JUnit test class for testing the EditAllEventsWithSubjectCommand class. Uses the MockModel
 * class to ensure that the parameters are being sent correctly.
 */
public class EditAllEventsWithSubjectCommandTest {

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
    command = new EditAllEventsWithSubjectCommand();
  }

  @Test
  public void testConstructor() {
    Command command = new EditAllEventsWithSubjectCommand();
    assertNotNull(command);
  }

  @Test
  public void testExecute() {
    try {
      String commandString = "edit events location test Boston ";
      command.execute(model, view, commandString);
    } catch (Exception e) {
      fail(e.getMessage());
    }

    String expected = "Input: location - test - Boston";
    assertEquals(expected, log.toString());
  }

  @Test
  public void testViewPrint() {
    try {
      String commandString = "edit events location test Boston ";
      command.execute(model, view, commandString);
    } catch (Exception e) {
      fail(e.getMessage());
    }
    assertEquals("Edited all matching events\n", out.toString());
  }
}