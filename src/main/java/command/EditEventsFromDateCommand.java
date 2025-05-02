package command;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import model.IModel;
import view.Viewer;

/**
 * Command for editing events from a given date and time.
 */
public class EditEventsFromDateCommand extends AbstractCommand {

  private static final Pattern PATTERN = Pattern.compile("^\\s*edit\\s+events\\s+"
      + "([a-zA-Z]+)\\s+(?:\"([^\"]+)\"|([^\"\\s]+))\\s+from\\s+"
      + "(\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2})\\s+with\\s+(?:\"([^\"]*)\"|([^\"\\s]+))\\s*$");

  /**
   * Constructs an EditEventsFromDateCommand object.
   */
  public EditEventsFromDateCommand() {
    super(PATTERN);
  }

  /**
   * Changes the property of all events starting at a specific date/time and have the given
   * subject.
   *
   * @param model   IModel object
   * @param view    Viewer object
   * @param matcher regex matcher
   * @throws Exception if an error occurs
   */
  @Override
  protected void executeMatched(IModel model, Viewer view, Matcher matcher) throws Exception {
    String property = matcher.group(1);
    String subject = matcher.group(2) != null ? matcher.group(2) : matcher.group(3);
    String dateTimeString = matcher.group(4);
    String newProperty = matcher.group(5) != null ? matcher.group(5) : matcher.group(6);

    model.editEventsFromStartDateTime(property, subject, dateTimeString, newProperty);
    view.print("Edited all matching events");
  }
}
