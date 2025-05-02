package command;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import calendar.IEvent;
import model.IModel;
import utils.ImportExportUtils;
import view.Viewer;

/**
 * Command for exporting the calendar.
 */
public class ExportCalendarCommand extends AbstractCommand {

  private static final Pattern PATTERN = Pattern.compile("^\\s*export\\s+cal\\s+"
      + "(\\/*(?:[\\w\\-\\.]+\\/)*[\\w\\-\\.]+\\.csv)$");

  /**
   * Constructs an ExportCalendarCommand object.
   */
  public ExportCalendarCommand() {
    super(PATTERN);
  }

  /**
   * Exports the calendar with events as a csv file and also prints the absolute path of the
   * generated CSV file.
   *
   * @param model   IModel object
   * @param view    Viewer object
   * @param matcher regex matcher
   * @throws Exception if an error occurs
   */
  @Override
  protected void executeMatched(IModel model, Viewer view, Matcher matcher) throws Exception {
    String filename = matcher.group(1);

    List<IEvent> events = model.getCurrentCalendar().getEvents();
    String filepath = ImportExportUtils.exportCalendar(filename, events);
    view.print("Exported calendar to " + filepath);
  }
}
