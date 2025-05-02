package command;

import java.time.ZoneId;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import calendar.IEvent;
import model.ConflictException;
import model.IModel;
import utils.ImportExportUtils;
import view.Viewer;

/**
 * Command for importing a calendar from a CSV file.
 */
public class ImportCalendarCommand extends AbstractCommand {

  private static final Pattern PATTERN = Pattern.compile("^\\s*import\\s+cal\\s+"
      + "(/*(?:[\\w\\-.]+/)*[\\w\\-.]+\\.csv)$");

  /**
   * Constructs an ImportCalendarCommand object.
   */
  public ImportCalendarCommand() {
    super(PATTERN);
  }

  /**
   * Imports a calendar from a csv file.
   *
   * @param model   IModel object
   * @param view    Viewer object
   * @param matcher regex matcher
   * @throws Exception if an error occurs
   */
  @Override
  protected void executeMatched(IModel model, Viewer view, Matcher matcher) throws Exception {
    String filename = matcher.group(1);

    ZoneId zone = model.getCurrentCalendar().getTimezone();
    List<IEvent> events = ImportExportUtils.importCalendar(filename, zone);

    int count = 0;
    for (IEvent event : events) {
      try {
        model.addEvents(List.of(event));
      } catch (ConflictException e) {
        count++;
      }
    }

    String message = "Finished importing events.";
    if (count > 0) {
      message += " Skipped " + count + " conflicting events.";
    }
    view.print(message);
  }
}
