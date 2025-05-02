package command;

import java.time.LocalDate;
import java.time.chrono.ChronoZonedDateTime;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import calendar.Event;
import calendar.ICalendar;
import calendar.IEvent;
import model.IModel;
import view.Viewer;
import utils.TimeUtils;

/**
 * Command for creating an event with a startDateTime and endDateTime.
 */
public class CreateEventCommand extends AbstractCommand {

  private static final Pattern PATTERN = Pattern.compile("^\\s*create\\s+event\\s+"
      + "(?:\"([^\"]+)\"|([^\"\\s]+))\\s+from\\s+(\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2})\\s+"
      + "to\\s+(\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2})"
      + OPTIONAL_PARAMS);

  /**
   * Constructs a CreateEventCommand object.
   */
  public CreateEventCommand() {
    super(PATTERN);
  }

  /**
   * Creates a single event in the calendar from the given start date and time to the end date and
   * time.
   *
   * @param model   model manges events where events will be added.
   * @param view    responsible for the user-facing output.
   * @param matcher regex matcher containing matched groups for event properties.
   * @throws Exception handles if any error occurs during event creation or addition.
   */
  @Override
  protected void executeMatched(IModel model, Viewer view, Matcher matcher) throws Exception {
    ICalendar calendar = model.getCurrentCalendar();

    String subject = matcher.group(1) != null ? matcher.group(1) : matcher.group(2);
    ChronoZonedDateTime<LocalDate> startDateTime =
        TimeUtils.parseDateTimeString(matcher.group(3), calendar.getTimezone());
    ChronoZonedDateTime<LocalDate> endDateTime =
        TimeUtils.parseDateTimeString(matcher.group(4), calendar.getTimezone());
    String description = matcher.group(5) != null ? matcher.group(5) : matcher.group(6);
    boolean isPrivate = matcher.group(7) != null;
    String location = matcher.group(8) != null ? matcher.group(8) : matcher.group(9);

    IEvent event = new Event.EventBuilder()
        .subject(subject)
        .startDateTime(startDateTime)
        .endDateTime(endDateTime)
        .description(description)
        .isPrivate(isPrivate)
        .location(location)
        .build();

    model.addEvents(List.of(event));
    view.print("Created event");
  }
}
