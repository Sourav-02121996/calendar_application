package command;

import java.time.LocalDate;
import java.time.chrono.ChronoZonedDateTime;
import java.time.temporal.ChronoUnit;
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
 * Command for creating a repeating all day event that repeats N times.
 */
public class CreateNAllDayRepeatingEventsCommand extends AbstractCommand {

  private static final Pattern PATTERN = Pattern.compile("^\\s*create\\s+event\\s+"
      + "(?:\"([^\"]+)\"|([^\"\\s]+))\\s+on\\s+(\\d{4}-\\d{2}-\\d{2})\\s+repeats\\s+"
      + "([MTWRFSU]+)\\s+for\\s+(\\d+)\\s+times"
      + OPTIONAL_PARAMS);

  /**
   * Constructs a CreateNAllDayRepeatingEventsCommand object.
   */
  public CreateNAllDayRepeatingEventsCommand() {
    super(PATTERN);
  }

  /**
   * Creates all day repeating events in the calendar.
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
    ChronoZonedDateTime<LocalDate> startDate =
        TimeUtils.parseDateString(matcher.group(3), calendar.getTimezone());
    char[] days = matcher.group(4).toCharArray();
    int repeatNumber = Integer.parseInt(matcher.group(5));
    String description = matcher.group(6) != null ? matcher.group(6) : matcher.group(7);
    boolean isPrivate = matcher.group(8) != null;
    String location = matcher.group(9) != null ? matcher.group(9) : matcher.group(10);

    IRepeatingEvent baseEvent = new RepeatingEvent.RepeatingEventBuilder()
        .subject(subject)
        .startDateTime(startDate)
        .endDateTime(startDate.plus(1, ChronoUnit.DAYS))
        .isAllDay(true)
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
