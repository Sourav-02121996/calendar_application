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
 * Command for printing all events occurring on a particular date.
 */
public class PrintEventsOnCommand extends AbstractCommand {

  private static final Pattern PATTERN = Pattern.compile("^\\s*print\\s+events\\s+on"
      + "\\s+(\\d{4}-\\d{2}-\\d{2})\\s*$");

  /**
   * Constructs a PrintEventsOnCommand object.
   */
  public PrintEventsOnCommand() {
    super(PATTERN);
  }

  /**
   * Prints a bulleted list of all events on the given date along with the event's start date and
   * time, end date and time, and location (if any).
   *
   * @param model   IModel object
   * @param view    Viewer object
   * @param matcher regex matcher
   * @throws Exception if an error occurs
   */
  @Override
  protected void executeMatched(IModel model, Viewer view, Matcher matcher) throws Exception {
    ICalendar calendar = model.getCurrentCalendar();

    ChronoZonedDateTime<LocalDate> date = TimeUtils.parseDateString(matcher.group(1),
        calendar.getTimezone());

    List<IEvent> events = model.getEventsOnDate(date);
    view.printEvents(events);
  }
}
