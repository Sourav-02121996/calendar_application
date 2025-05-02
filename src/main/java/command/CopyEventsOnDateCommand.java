package command;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import model.IModel;
import view.Viewer;

/**
 * Command for copying events occurring on a specified date to a calendar.
 */
public class CopyEventsOnDateCommand extends AbstractCommand {

  private static final Pattern PATTERN = Pattern.compile("^\\s*copy\\s+events\\s+on\\s+"
      + "(\\d{4}-\\d{2}-\\d{2})\\s+--target\\s+(?:\"([^\"]+)\"|([^\"\\s]+))\\s+to"
      + "\\s+(\\d{4}-\\d{2}-\\d{2})");

  /**
   * Constructs a CopyEventsOnDateCommand object.
   */
  public CopyEventsOnDateCommand() {
    super(PATTERN);
  }

  /**
   * Copies events occurring on the specified date to the target calendar at the specified new start
   * date.
   *
   * @param model   IModel object
   * @param view    Viewer object
   * @param matcher regex matcher
   * @throws Exception if an error occurs
   */
  @Override
  protected void executeMatched(IModel model, Viewer view, Matcher matcher) throws Exception {
    String start = matcher.group(1);
    String calendarName = matcher.group(2) != null ? matcher.group(2) : matcher.group(3);
    String newCalendarStart = matcher.group(4);

    model.copyEventsOnDate(start, newCalendarStart, calendarName);
    view.print("Copied events to calendar " + calendarName);
  }
}
