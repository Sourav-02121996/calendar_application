package command;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import calendar.Calendar;
import calendar.ICalendar;
import model.IModel;
import view.Viewer;

/**
 * Command for creating a calendar with a name and timezone.
 */
public class CreateCalendarCommand extends AbstractCommand {

  private static final Pattern PATTERN = Pattern.compile("^\\s*create\\s+calendar\\s+"
      + "--name\\s+(?:\"([^\"]+)\"|([^\"\\s]+))\\s+--timezone\\s+([\\w/-]+)\\s*$");

  /**
   * Constructs a CreateCalendarCommand object.
   */
  public CreateCalendarCommand() {
    super(PATTERN);
  }

  /**
   * Creates a new calendar with the given name and timezone.
   *
   * @param model   IModel object
   * @param view    Viewer object
   * @param matcher regex matcher
   * @throws Exception if an error occurs
   */
  @Override
  protected void executeMatched(IModel model, Viewer view, Matcher matcher) throws Exception {
    String name = matcher.group(1) != null ? matcher.group(1) : matcher.group(2);
    String timeZone = matcher.group(3);

    ICalendar calendar = new Calendar(name, timeZone);
    model.addCalendar(calendar);
    view.print("Created calendar");
  }
}
