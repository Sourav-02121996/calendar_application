package command;

import java.time.LocalDate;
import java.time.chrono.ChronoZonedDateTime;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import calendar.ICalendar;
import calendar.IEvent;
import model.IModel;
import view.Viewer;
import utils.TimeUtils;

/**
 * Command for printing all events occurring inside a date and time range.
 */
public class PrintEventsInRangeCommand extends AbstractCommand {

  private static final Pattern PATTERN = Pattern.compile("^\\s*print\\s+events\\s+from"
      + "\\s+(\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2})\\s+to\\s+"
      + "(\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2})\\s*$");

  /**
   * Constructs a PrintEventsInRangeCommand object.
   */
  public PrintEventsInRangeCommand() {
    super(PATTERN);
  }

  /**
   * Prints a bulleted list of all events in the given range including their start-date and time,
   * end date and time and the location (if any).
   *
   * @param model   IModel object
   * @param view    Viewer object
   * @param matcher regex matcher
   * @throws Exception if an error occurs
   */
  @Override
  protected void executeMatched(IModel model, Viewer view, Matcher matcher) throws Exception {
    ICalendar calendar = model.getCurrentCalendar();

    ChronoZonedDateTime<LocalDate> startDateTime = TimeUtils.parseDateTimeString(matcher.group(1),
        calendar.getTimezone());
    ChronoZonedDateTime<LocalDate> endDateTime = TimeUtils.parseDateTimeString(matcher.group(2),
        calendar.getTimezone());

    List<IEvent> events = model.getEventsInRange(startDateTime, endDateTime);
    view.printEvents(events);
  }
}
