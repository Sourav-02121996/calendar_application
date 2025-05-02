package command;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import model.IModel;
import view.Viewer;

/**
 * Command for showing the schedule status on a particular date and time.
 */
public class ShowStatusCommand extends AbstractCommand {

  private static final Pattern PATTERN = Pattern.compile("^\\s*show\\s+status\\s+on"
      + "\\s+(\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2})\\s*$");

  /**
   * Constructs a ShowStatusCommand object.
   */
  public ShowStatusCommand() {
    super(PATTERN);
  }

  /**
   * Prints the calendar status on a given date and time.
   *
   * @param model   IModel object
   * @param view    Viewer object
   * @param matcher regex matcher
   * @throws Exception if an error occurs
   */
  @Override
  protected void executeMatched(IModel model, Viewer view, Matcher matcher) throws Exception {
    String dateTime = matcher.group(1);

    String status = model.getStatus(dateTime);
    view.print(status);
  }
}
