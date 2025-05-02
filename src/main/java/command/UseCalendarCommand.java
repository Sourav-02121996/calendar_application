package command;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import model.IModel;
import view.Viewer;

/**
 * Command for changing the current calendar in the application.
 */
public class UseCalendarCommand extends AbstractCommand {

  private static final Pattern PATTERN = Pattern.compile("^\\s*use\\s+calendar\\s+--name\\s+"
      + "(?:\"([^\"]+)\"|([^\"\\s]+))\\s*$");

  /**
   * Constructs a UseCalendarCommand object.
   */
  public UseCalendarCommand() {
    super(PATTERN);
  }

  /**
   * Changes the current calendar to the specified calendar name.
   *
   * @param model   IModel object
   * @param view    Viewer object
   * @param matcher regex matcher
   * @throws Exception if an error occurs
   */
  @Override
  protected void executeMatched(IModel model, Viewer view, Matcher matcher) throws Exception {
    String name = matcher.group(1) != null ? matcher.group(1) : matcher.group(2);
    model.useCalendar(name);

    view.print("Current calendar: " + name);
  }
}
