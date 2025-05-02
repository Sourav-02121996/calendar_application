package command;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import model.IModel;
import view.Viewer;

/**
 * Command for editing an existing calendar.
 */
public class EditCalendarCommand extends AbstractCommand {

  private static final Pattern PATTERN = Pattern.compile("^\\s*edit\\s+calendar\\s+--name\\s+"
      + "(?:\"([^\"]+)\"|([^\"\\s]+))\\s+--property\\s+([A-Za-z]+)\\s+"
      + "(?:\"([^\"]+)\"|([^\"\\s]+))\\s*$");

  /**
   * Constructs an EditCalendarCommand object.
   */
  public EditCalendarCommand() {
    super(PATTERN);
  }

  /**
   * Edits a property of an existing calendar.
   *
   * @param model   IModel object
   * @param view    Viewer object
   * @param matcher regex matcher
   * @throws Exception if an error occurs
   */
  @Override
  protected void executeMatched(IModel model, Viewer view, Matcher matcher) throws Exception {
    String name = matcher.group(1) != null ? matcher.group(1) : matcher.group(2);
    String property = matcher.group(3);
    String newValue = matcher.group(4) != null ? matcher.group(4) : matcher.group(5);

    model.editCalendar(name, property, newValue);
    view.print("Edited calendar " + property);
  }
}
