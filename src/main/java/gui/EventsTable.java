package gui;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import calendar.IEvent;
import utils.TimeUtils;

/**
 * A Java class representing a table of events in the calendar application. Extends the JTable
 * class.
 */
public class EventsTable extends JTable {

  private static final String[] COLUMN_NAMES = {"Subject", "Start Date", "Start Time", "End Date",
      "End Time", "All Day", "Private", "Description", "Location"};

  private final IViewModel viewModel;
  private List<IEvent> events;

  /**
   * Constructs an EventsTable object.
   *
   * @param viewModel     IViewModel object
   * @param calendarFrame CalendarFrame object
   */
  public EventsTable(IViewModel viewModel, CalendarFrame calendarFrame) {
    super();
    this.events = new ArrayList<>();
    this.viewModel = viewModel;

    updateTableModel(viewModel.getCurrentDate(), calendarFrame);
    this.setPreferredScrollableViewportSize(getPreferredSize());

    getSelectionModel().addListSelectionListener(e -> {
      if (!e.getValueIsAdjusting() && getSelectedRow() > -1) {
        viewModel.setSelectedEvent(events.get(getSelectedRow()));
        calendarFrame.setEditEventButtonEnabled(true);
      }
    });
  }

  /**
   * Updates the tableModel to show events occurring on the given date.
   *
   * @param date          Date object
   * @param calendarFrame CalendarFrame object
   */
  public void updateTableModel(Date date, CalendarFrame calendarFrame) {
    this.events = viewModel.getEventsOnDate(date);

    Object[][] data = new Object[events.size()][COLUMN_NAMES.length];
    for (int i = 0; i < events.size(); i++) {
      data[i][0] = events.get(i).getSubject();

      String startDateTime = TimeUtils.format(events.get(i).getStartDateTime(),
          "MM-dd-yyyy'T'HH:mm");
      data[i][1] = startDateTime.split("T")[0];
      data[i][2] = startDateTime.split("T")[1];

      String endDateTime = TimeUtils.format(events.get(i).getEndDateTime(),
          "MM-dd-yyyy'T'HH:mm");
      data[i][3] = endDateTime.split("T")[0];
      data[i][4] = endDateTime.split("T")[1];
      data[i][5] = events.get(i).isAllDay() ? "Yes" : "No";
      data[i][6] = events.get(i).isPrivate() ? "Yes" : "No";
      data[i][7] = events.get(i).getDescription();
      data[i][8] = events.get(i).getLocation();
    }

    DefaultTableModel tableModel = new DefaultTableModel(data, COLUMN_NAMES) {
      @Override
      public boolean isCellEditable(int row, int column) {
        return false;
      }
    };
    setModel(tableModel);
    viewModel.setSelectedEvent(null);
    // disable edit button
    calendarFrame.setEditEventButtonEnabled(false);

    repaint();
  }
}
