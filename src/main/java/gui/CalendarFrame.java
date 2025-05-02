package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JDialog;

import java.util.Date;

import calendar.ICalendar;

/**
 * A Java class representing the main calendar GUI frame of the calendar application. Contains a
 * custom date picker and an events table. Extends the JFrame class.
 */
public class CalendarFrame extends JFrame {

  private final IViewModel viewModel;
  private final EventsTable eventsTable;

  private final JLabel calendarNameLabel;
  private final JLabel calendarTimezoneLabel;

  private final JButton calendarSettingsButton;
  private final JButton editEventsButton;
  private final JButton addEventButton;
  private final JButton editEventButton;

  /**
   * Constructs a CalendarFrame object.
   *
   * @param viewModel IViewModel object
   */
  public CalendarFrame(IViewModel viewModel) {
    super("Calendar App");
    this.viewModel = viewModel;

    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setSize(950, 500);
    setLocationRelativeTo(null);
    setLayout(new BorderLayout());

    JPanel topPanel = new JPanel(new BorderLayout());
    JPanel leftTopPanel = new JPanel();

    calendarSettingsButton = new JButton("Calendar Settings");
    leftTopPanel.add(calendarSettingsButton);

    String calendarName = viewModel.getCurrentCalendar().getName();
    calendarNameLabel = new JLabel("Calendar: " + calendarName);
    leftTopPanel.add(calendarNameLabel);

    String calendarTimezone = viewModel.getCurrentCalendar().getTimezone().toString();
    calendarTimezoneLabel = new JLabel("Timezone: " + calendarTimezone);
    leftTopPanel.add(calendarTimezoneLabel);

    topPanel.add(leftTopPanel, BorderLayout.WEST);

    // Right-side buttons
    JPanel rightTopPanel = new JPanel(new GridLayout(3, 1, 5, 5));

    editEventsButton = new JButton("Edit Events");
    rightTopPanel.add(editEventsButton);

    addEventButton = new JButton("Add Event");
    rightTopPanel.add(addEventButton);

    editEventButton = new JButton("Edit Event");
    editEventButton.setEnabled(false);
    rightTopPanel.add(editEventButton);

    topPanel.add(rightTopPanel, BorderLayout.EAST);
    add(topPanel, BorderLayout.NORTH);

    // create events table
    eventsTable = new EventsTable(viewModel, this);
    JScrollPane eventsTableView = new JScrollPane(eventsTable);
    eventsTableView.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

    CustomDatePicker customDatePicker = new CustomDatePicker(viewModel, eventsTable, this);
    JPanel leftPanel = new JPanel(new BorderLayout());
    leftPanel.setMinimumSize(new Dimension(100, 100));
    leftPanel.add(customDatePicker, BorderLayout.CENTER);

    // Splitting Pane, Left (Date Picker), Right (Events Table)
    JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, eventsTableView);
    splitPane.setDividerLocation(300);
    splitPane.setResizeWeight(0.2);
    splitPane.setContinuousLayout(true);

    add(splitPane, BorderLayout.CENTER);
    setVisible(true);
  }

  /**
   * Action listeners for the calendar app frame.
   *
   * @param features features which needs to be implemented
   */
  public void addFeatures(Features features) {
    calendarSettingsButton.addActionListener(e ->
        displayCalendarSettingsDialog(viewModel, features));
    editEventsButton.addActionListener(e ->
        displayEditMultipleEventsDialog(viewModel, features));
    addEventButton.addActionListener(e ->
        displayAddEventDialog(viewModel, features));
    editEventButton.addActionListener(e ->
        displayEditEventDialog(viewModel, features));
  }

  /**
   * Set the isEnabled property of the edit event button.
   *
   * @param enabled new value
   */
  public void setEditEventButtonEnabled(boolean enabled) {
    this.editEventButton.setEnabled(enabled);
  }

  private void displayCalendarSettingsDialog(IViewModel viewModel, Features features) {
    JDialog calendarSettingsDialog = new CalendarSettingsDialog(this, viewModel,
        features, this::updateCurrentCalendar);
    calendarSettingsDialog.setVisible(true);
  }

  private void displayEditMultipleEventsDialog(IViewModel viewModel, Features features) {
    IDialog editMultipleEventsDialog = new EditMultipleEventsDialog(this, viewModel,
        this::updateTable);
    editMultipleEventsDialog.setFeatures(features);
    editMultipleEventsDialog.setVisible(true);
  }

  private void displayAddEventDialog(IViewModel viewModel, Features features) {
    IDialog addEventDialog = new AddEventDialog(this, viewModel, this::updateTable);
    addEventDialog.setFeatures(features);
    addEventDialog.setVisible(true);
  }

  private void displayEditEventDialog(IViewModel viewModel, Features features) {
    IDialog editEventDialog = new EditEventDialog(this, viewModel, this::updateTable);
    editEventDialog.setFeatures(features);
    editEventDialog.setVisible(true);
  }

  private void updateTable(Date date) {
    eventsTable.updateTableModel(date, this);
  }

  /**
   * Update the current calendar name and timezone labels.
   *
   * @param calendar ICalendar object
   */
  private void updateCurrentCalendar(ICalendar calendar) {
    this.calendarNameLabel.setText("Calendar: " + calendar.getName());
    this.calendarTimezoneLabel.setText("Timezone: " + calendar.getTimezone().toString());
    updateTable(viewModel.getSelectedDate());
  }
}
