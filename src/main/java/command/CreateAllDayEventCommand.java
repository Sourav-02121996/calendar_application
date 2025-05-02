package command;

import java.time.LocalDate;
import java.time.chrono.ChronoZonedDateTime;
import java.time.temporal.ChronoUnit;
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
 * Command for creating an all day event.
 */
public class CreateAllDayEventCommand extends AbstractCommand {

  private static final Pattern PATTERN = Pattern.compile("^\\s*create\\s+event\\s+"
      + "(?:\"([^\"]+)\"|([^\"\\s]+))\\s+on\\s+(\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2})"
      + OPTIONAL_PARAMS);

  /**
   * Constructs a CreateAllDayEventCommand object.
   */
  public CreateAllDayEventCommand() {
    super(PATTERN);
  }

  /**
   * Creates a single all day event.
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
        TimeUtils.parseDateTimeAtStartOfDay(matcher.group(3), calendar.getTimezone());
    String description = matcher.group(4) != null ? matcher.group(4) : matcher.group(5);
    boolean isPrivate = matcher.group(6) != null;
    String location = matcher.group(7) != null ? matcher.group(7) : matcher.group(8);

    IEvent event = new Event.EventBuilder()
        .subject(subject)
        .startDateTime(startDateTime)
        .endDateTime(startDateTime.plus(1, ChronoUnit.DAYS))
        .isAllDay(true)
        .description(description)
        .isPrivate(isPrivate)
        .location(location)
        .build();

    model.addEvents(List.of(event));
    view.print("Created all day event");
  }
}
