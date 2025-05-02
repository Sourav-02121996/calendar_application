package command;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import model.IModel;
import view.Viewer;

/**
 * Command for copying a single event to another calendar.
 */
public class CopyEventCommand extends AbstractCommand {

  private static final Pattern PATTERN = Pattern.compile("^\\s*copy\\s+event\\s+"
      + "(?:\"([^\"]+)\"|([^\"\\s]+))\\s+on\\s+(\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2})\\s+"
      + "--target\\s+(?:\"([^\"]+)\"|([^\"\\s]+))\\s+to\\s+(\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2})");

  /**
   * Constructs a CopyEventCommand object.
   */
  public CopyEventCommand() {
    super(PATTERN);
  }

  /**
   * Copies an event with the given start time, end time and name into the target calendar at the
   * given new start date and time.
   *
   * @param model   IModel object
   * @param view    Viewer object
   * @param matcher regex matcher
   * @throws Exception if an error occurs
   */
  @Override
  protected void executeMatched(IModel model, Viewer view, Matcher matcher) throws Exception {
    String eventName = matcher.group(1) != null ? matcher.group(1) : matcher.group(2);
    String start = matcher.group(3);
    String calendarName = matcher.group(4) != null ? matcher.group(4) : matcher.group(5);
    String newCalendarStart = matcher.group(6);

    model.copyEvent(eventName, calendarName, start, newCalendarStart);
    view.print("Copied event to calendar " + calendarName);
  }
}
