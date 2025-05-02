package command;

import java.time.LocalDate;
import java.time.chrono.ChronoZonedDateTime;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import calendar.ICalendar;
import calendar.IEvent;
import calendar.IRepeatingEvent;
import model.IModel;
import calendar.RepeatingEvent;
import view.Viewer;
import utils.TimeUtils;

/**
 * Command for creating a repeating events that repeats N times.
 */
public class CreateNRepeatingEventsCommand extends AbstractCommand {

  private static final Pattern PATTERN = Pattern.compile("^\\s*create\\s+event\\s+"
      + "(?:\"([^\"]+)\"|([^\"\\s]+))\\s+from\\s+(\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2})\\s+to"
      + "\\s+(\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2})\\s+repeats\\s+([MTWRFSU]+)\\s+for\\s+"
      + "(\\d+)\\s+times"
      + OPTIONAL_PARAMS);

  /**
   * Constructs a CreateNRepeatingEventsCommand object.
   */
  public CreateNRepeatingEventsCommand() {
    super(PATTERN);
  }

  /**
   * Creates a recurring event that repeats N times on specific weekdays.
   *
   * @param model   IModel object
   * @param view    Viewer object
   * @param matcher regex matcher
   * @throws Exception if an error occurs
   */
  @Override
  protected void executeMatched(IModel model, Viewer view, Matcher matcher) throws Exception {
    ICalendar calendar = model.getCurrentCalendar();

    String subject = matcher.group(1) != null ? matcher.group(1) : matcher.group(2);
    ChronoZonedDateTime<LocalDate> startDateTime =
        TimeUtils.parseDateTimeString(matcher.group(3), calendar.getTimezone());
    ChronoZonedDateTime<LocalDate> endDateTime =
        TimeUtils.parseDateTimeString(matcher.group(4), calendar.getTimezone());
    char[] days = matcher.group(5).toCharArray();
    int repeatNumber = Integer.parseInt(matcher.group(6));
    String description = matcher.group(7) != null ? matcher.group(7) : matcher.group(8);
    boolean isPrivate = matcher.group(9) != null;
    String location = matcher.group(10) != null ? matcher.group(10) : matcher.group(11);

    IRepeatingEvent baseEvent = new RepeatingEvent.RepeatingEventBuilder()
        .subject(subject)
        .startDateTime(startDateTime)
        .endDateTime(endDateTime)
        .description(description)
        .isPrivate(isPrivate)
        .location(location)
        .repeatNumber(repeatNumber)
        .repeatDays(TimeUtils.getDaysOfWeek(days))
        .build();
    List<IEvent> repeatingEvents = baseEvent.repeatNTimes();
    model.addEvents(repeatingEvents);

    view.print("Created repeating event");
  }
}
