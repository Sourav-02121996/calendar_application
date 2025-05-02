package command;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import model.IModel;
import view.Viewer;

/**
 * Command for editing a single event.
 */
public class EditSingleEventCommand extends AbstractCommand {

  private static final Pattern PATTERN = Pattern.compile("^\\s*edit\\s+event\\s+"
      + "([a-zA-Z]+)\\s+(?:\"([^\"]+)\"|([^\"\\s]+))\\s+from\\s+"
      + "(\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2})\\s+to\\s+(\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2})"
      + "\\s+with\\s+(?:\"([^\"]*)\"|([^\"\\s]+))\\s*$");

  /**
   * Constructs an EditSingleEventCommand object.
   */
  public EditSingleEventCommand() {
    super(PATTERN);
  }

  /**
   * Changes the property of a single event.
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
    String start = matcher.group(4);
    String end = matcher.group(5);
    String newProperty = matcher.group(6) != null ? matcher.group(6) : matcher.group(7);

    model.editSingleEvent(property, subject, start, end, newProperty);
    view.print("Edited event");
  }
}
