package command;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import model.IModel;
import view.Viewer;

/**
 * Command for copying events in a given time range to a target calendar.
 */
public class CopyEventsInRangeCommand extends AbstractCommand {

  private static final Pattern PATTERN = Pattern.compile("^\\s*copy\\s+events\\s+between\\s+"
      + "(\\d{4}-\\d{2}-\\d{2})\\s+and\\s+(\\d{4}-\\d{2}-\\d{2})\\s+--target\\s+"
      + "(?:\"([^\"]+)\"|([^\"\\s]+))\\s+to\\s+(\\d{4}-\\d{2}-\\d{2})");

  /**
   * Constructs a CopyEventsInRangeCommand object.
   */
  public CopyEventsInRangeCommand() {
    super(PATTERN);
  }

  /**
   * Copies events between the specified start and end date and times to the target calendar at the
   * specified new start date.
   *
   * @param model   IModel object
   * @param view    Viewer object
   * @param matcher regex matcher
   * @throws Exception if an error occurs
   */
  @Override
  protected void executeMatched(IModel model, Viewer view, Matcher matcher) throws Exception {
    String start = matcher.group(1);
    String end = matcher.group(2);
    String calendarName = matcher.group(3) != null ? matcher.group(3) : matcher.group(4);
    String newCalendarStart = matcher.group(5);

    model.copyEventsInRange(start, end, calendarName, newCalendarStart);
    view.print("Copied events to calendar " + calendarName);
  }
}
