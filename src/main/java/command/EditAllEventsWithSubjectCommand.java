package command;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import model.IModel;
import view.Viewer;

/**
 * Command for editing all events with the same subject.
 */
public class EditAllEventsWithSubjectCommand extends AbstractCommand {

  private static final Pattern PATTERN = Pattern.compile("^\\s*edit\\s+events\\s+([a-zA-Z]+)"
      + "\\s+(?:\"([^\"]+)\"|([^\"\\s]+))\\s+(?:\"([^\"]*)\"|([^\"\\s]+))\\s*$");

  /**
   * Constructs an EditAllEventsWithSubjectCommand object.
   */
  public EditAllEventsWithSubjectCommand() {
    super(PATTERN);
  }

  /**
   * Changes the given property of all events with the same subject.
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
    String newProperty = matcher.group(4) != null ? matcher.group(4) : matcher.group(5);

    model.editEventsBySubject(property, subject, newProperty);
    view.print("Edited all matching events");
  }
}
