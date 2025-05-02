package gui;

import javax.swing.JCheckBox;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;
import javax.swing.JSpinner;
import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;
import javax.swing.JDialog;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Insets;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * A Java class representing a GUI window for adding an event. Extends the JDialog class and
 * implements the IDialog interface.
 */
public class AddEventDialog extends JDialog implements IDialog {

  private final IViewModel viewModel;
  private final Consumer<Date> onEventAdded;

  private final JTextField subjectTextField;
  private final JSpinner startDateSpinner;
  private final JSpinner startTimeSpinner;
  private final JSpinner endDateSpinner;
  private final JSpinner endTimeSpinner;
  private final JCheckBox allDayCheckBox;
  private final JCheckBox privateCheckBox;
  private final JTextField descriptionTextField;
  private final JTextField locationTextField;

  private final JCheckBox repeatingCheckBox;
  private final JCheckBox repeatMondayCheckBox;
  private final JCheckBox repeatTuesdayCheckBox;
  private final JCheckBox repeatWednesdayCheckBox;
  private final JCheckBox repeatThursdayCheckBox;
  private final JCheckBox repeatFridayCheckBox;
  private final JCheckBox repeatSaturdayCheckBox;
  private final JCheckBox repeatSundayCheckBox;

  private final JRadioButton repeatNumberRadioButton;
  private final JRadioButton repeatUntilRadioButton;

  private final JTextField repeatNumberTextField;
  private final JSpinner repeatEndDateSpinner;
  private final JSpinner repeatEndTimeSpinner;

  private final JButton addButton;

  /**
   * Constructs an AddEventDialog object. Calls the JDialog constructor.
   *
   * @param parent    parent Frame object
   * @param viewModel IViewModel object
   * @param callback  callback function that accepts a Date parameter
   */
  public AddEventDialog(Frame parent, IViewModel viewModel, Consumer<Date> callback) {
    super(parent, "Add Event");
    this.onEventAdded = callback;
    this.viewModel = viewModel;

    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    setBounds(200, 200, 500, 480);
    setLocationRelativeTo(null);
    setResizable(false);

    JPanel panel = new JPanel(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(5, 5, 5, 5);
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.HORIZONTAL;

    // Row 0: Name
    gbc.gridx = 0;
    gbc.gridy = 0;
    panel.add(new JLabel("Name"), gbc);
    gbc.gridx = 1;
    gbc.gridwidth = 2;
    subjectTextField = new JTextField(20);
    panel.add(subjectTextField, gbc);

    // Row 1: Start Date
    gbc.gridwidth = 1;
    gbc.gridx = 0;
    gbc.gridy = 1;
    panel.add(new JLabel("Start"), gbc);
    gbc.gridx = 1;

    Date selectedDate = viewModel.getSelectedDate();
    SpinnerDateModel startDateSpinnerModel = new SpinnerDateModel(selectedDate, null, null,
        Calendar.DAY_OF_MONTH);
    startDateSpinner = new JSpinner(startDateSpinnerModel);
    startDateSpinner.setEditor(new JSpinner.DateEditor(startDateSpinner,
        "MM/dd/yyyy"));
    panel.add(startDateSpinner, gbc);

    // Start Time
    gbc.gridx = 2;
    SpinnerDateModel startTimeSpinnerModel = new SpinnerDateModel();
    startTimeSpinner = new JSpinner(startTimeSpinnerModel);
    startTimeSpinner.setEditor(new JSpinner.DateEditor(startTimeSpinner, "HH:mm"));
    panel.add(startTimeSpinner, gbc);

    // all day checkbox
    gbc.gridx = 3;
    allDayCheckBox = new JCheckBox("All Day");
    panel.add(allDayCheckBox, gbc);

    // Row 2: End date
    gbc.gridwidth = 1;
    gbc.gridx = 0;
    gbc.gridy = 2;
    panel.add(new JLabel("End"), gbc);
    gbc.gridx = 1;

    SpinnerDateModel endDateSpinnerModel = new SpinnerDateModel(selectedDate, null, null,
        Calendar.DAY_OF_MONTH);
    endDateSpinner = new JSpinner(endDateSpinnerModel);
    endDateSpinner.setEditor(new JSpinner.DateEditor(endDateSpinner, "MM/dd/yyyy"));
    panel.add(endDateSpinner, gbc);

    // end time
    gbc.gridx = 2;
    SpinnerDateModel endTimeSpinnerModel = new SpinnerDateModel();
    endTimeSpinner = new JSpinner(endTimeSpinnerModel);
    endTimeSpinner.setEditor(new JSpinner.DateEditor(endTimeSpinner,
        "HH:mm"));
    panel.add(endTimeSpinner, gbc);

    // disable end date and time inputs if all day is checked
    allDayCheckBox.addActionListener(e -> {
      endDateSpinner.setEnabled(!allDayCheckBox.isSelected());
      endTimeSpinner.setEnabled(!allDayCheckBox.isSelected());
      startTimeSpinner.setEnabled(!allDayCheckBox.isSelected());

      Date currentStartDate = startDateSpinnerModel.getDate();
      if (allDayCheckBox.isSelected()) {
        startTimeSpinnerModel.setValue(getStartOfDay(currentStartDate).getTime());
        endDateSpinnerModel.setValue(plusOneDay(currentStartDate).getTime());
        endTimeSpinnerModel.setValue(getStartOfDay(currentStartDate).getTime());
      } else {
        Calendar calendar = Calendar.getInstance();
        startTimeSpinnerModel.setValue(calendar.getTime());
        endDateSpinnerModel.setValue(currentStartDate);
        endTimeSpinnerModel.setValue(calendar.getTime());
      }
    });

    // Row 3: Private
    gbc.gridx = 0;
    gbc.gridy = 3;
    panel.add(new JLabel("Private"), gbc);
    gbc.gridx = 1;
    privateCheckBox = new JCheckBox();
    panel.add(privateCheckBox, gbc);

    // Row 4: Description
    gbc.gridx = 0;
    gbc.gridy = 4;
    panel.add(new JLabel("Description"), gbc);
    gbc.gridx = 1;
    gbc.gridwidth = 3;
    descriptionTextField = new JTextField(20);
    panel.add(descriptionTextField, gbc);
    gbc.gridwidth = 1;

    // Row 5: Location
    gbc.gridx = 0;
    gbc.gridy = 5;
    panel.add(new JLabel("Location"), gbc);
    gbc.gridx = 1;
    gbc.gridwidth = 3;
    locationTextField = new JTextField(20);
    panel.add(locationTextField, gbc);
    gbc.gridwidth = 1;

    // Row 6: Repeating checkbox
    gbc.gridx = 0;
    gbc.gridy = 6;
    panel.add(new JLabel("Repeating"), gbc);
    gbc.gridx = 1;
    repeatingCheckBox = new JCheckBox();
    panel.add(repeatingCheckBox, gbc);

    JPanel repeatingPanel = new JPanel(new GridBagLayout());
    repeatingPanel.setVisible(false);

    GridBagConstraints rg = new GridBagConstraints();
    rg.insets = new Insets(5, 5, 5, 5);
    rg.anchor = GridBagConstraints.WEST;
    rg.fill = GridBagConstraints.HORIZONTAL;
    rg.gridx = 0;
    rg.gridy = 0;
    repeatingPanel.add(new JLabel("Repeat Days"), rg);

    rg.gridx = 0;
    rg.gridy = 1;
    rg.gridwidth = 4;
    JPanel daysPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));

    repeatMondayCheckBox = new JCheckBox("Mon");
    daysPanel.add(repeatMondayCheckBox);

    repeatTuesdayCheckBox = new JCheckBox("Tue");
    daysPanel.add(repeatTuesdayCheckBox);

    repeatWednesdayCheckBox = new JCheckBox("Wed");
    daysPanel.add(repeatWednesdayCheckBox);

    repeatThursdayCheckBox = new JCheckBox("Thu");
    daysPanel.add(repeatThursdayCheckBox);

    repeatFridayCheckBox = new JCheckBox("Fri");
    daysPanel.add(repeatFridayCheckBox);

    repeatSaturdayCheckBox = new JCheckBox("Sat");
    daysPanel.add(repeatSaturdayCheckBox);

    repeatSundayCheckBox = new JCheckBox("Sun");
    daysPanel.add(repeatSundayCheckBox);

    repeatingPanel.add(daysPanel, rg);

    rg.gridwidth = 1;
    rg.gridx = 0;
    rg.gridy = 2;
    repeatingPanel.add(new JLabel("Repeat Type"), rg);

    rg.gridx = 1;
    rg.gridwidth = 3;
    JPanel repeatTypePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
    repeatNumberRadioButton = new JRadioButton("Repeat N times");
    repeatUntilRadioButton = new JRadioButton("Repeat until date");

    ButtonGroup repeatTypeGroup = new ButtonGroup();
    repeatTypeGroup.add(repeatNumberRadioButton);
    repeatTypeGroup.add(repeatUntilRadioButton);

    repeatTypePanel.add(repeatNumberRadioButton);
    repeatTypePanel.add(repeatUntilRadioButton);
    repeatingPanel.add(repeatTypePanel, rg);

    rg.gridwidth = 1;
    rg.gridx = 0;
    rg.gridy = 3;
    JLabel repeatNumberLabel = new JLabel("Repeat Number");
    repeatingPanel.add(repeatNumberLabel, rg);
    repeatNumberLabel.setVisible(false);

    rg.gridx = 1;
    repeatNumberTextField = new JTextField(5);
    repeatingPanel.add(repeatNumberTextField, rg);
    repeatNumberTextField.setVisible(false);

    rg.gridx = 0;
    rg.gridy = 4;
    JLabel repeatEndDateLabel = new JLabel("Repeat End Date");
    repeatingPanel.add(repeatEndDateLabel, rg);
    repeatEndDateLabel.setVisible(false);

    rg.gridx = 1;
    SpinnerDateModel repeatEndDateSpinnerModel = new SpinnerDateModel(selectedDate,
        null, null, Calendar.DAY_OF_MONTH);
    repeatEndDateSpinner = new JSpinner(repeatEndDateSpinnerModel);
    repeatEndDateSpinner.setEditor(new JSpinner.DateEditor(repeatEndDateSpinner,
        "MM/dd/yyyy"));
    repeatEndDateSpinner.setVisible(false);
    repeatingPanel.add(repeatEndDateSpinner, rg);

    rg.gridx = 2;
    SpinnerDateModel repeatEndTimeSpinnerModel = new SpinnerDateModel(selectedDate,
        null, null, Calendar.MINUTE);
    repeatEndTimeSpinner = new JSpinner(repeatEndTimeSpinnerModel);
    repeatEndTimeSpinner.setEditor(new JSpinner.DateEditor(repeatEndTimeSpinner,
        "HH:mm"));
    repeatEndTimeSpinner.setVisible(false);
    repeatingPanel.add(repeatEndTimeSpinner, rg);

    // show repeat number fields when selected
    repeatNumberRadioButton.addActionListener(e -> {
      repeatNumberLabel.setVisible(true);
      repeatNumberTextField.setVisible(true);
      repeatEndDateLabel.setVisible(false);
      repeatEndDateSpinner.setVisible(false);
      repeatEndTimeSpinner.setVisible(false);
      repeatingPanel.revalidate();
      repeatingPanel.repaint();
    });

    // show repeat until end date fields when selected
    repeatUntilRadioButton.addActionListener(e -> {
      repeatNumberLabel.setVisible(false);
      repeatNumberTextField.setVisible(false);
      repeatEndDateLabel.setVisible(true);
      repeatEndDateSpinner.setVisible(true);
      repeatEndTimeSpinner.setVisible(true);
      repeatingPanel.revalidate();
      repeatingPanel.repaint();
    });

    gbc.gridx = 0;
    gbc.gridy = 10;
    gbc.gridwidth = 4;
    panel.add(repeatingPanel, gbc);

    gbc.gridx = 0;
    gbc.gridy = 11;
    gbc.gridwidth = 4;
    gbc.anchor = GridBagConstraints.CENTER;
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
    addButton = new JButton("Add");
    buttonPanel.add(addButton);

    JButton cancelButton = new JButton("Cancel");
    cancelButton.addActionListener(e -> dispose());
    buttonPanel.add(cancelButton);
    panel.add(buttonPanel, gbc);
    add(panel);

    // Toggling the visibility of the repeating panel based on the checkbox
    repeatingCheckBox.addActionListener(e -> {
      boolean isSelected = repeatingCheckBox.isSelected();
      repeatingPanel.setVisible(isSelected);
      panel.revalidate();
      panel.repaint();
    });

    // update the endDate if the start date is changed and all day is selected
    startDateSpinner.addChangeListener(e -> {
      if (allDayCheckBox.isSelected()) {
        Date newDate = plusOneDay((Date) startDateSpinner.getValue()).getTime();
        endDateSpinner.setValue(newDate);
      }
    });
  }

  @Override
  public void setFeatures(Features features) {
    addButton.addActionListener(e -> {
      Map<String, String> inputs = new HashMap<>();
      inputs.put("subject", subjectTextField.getText());
      inputs.put("startDate", new SimpleDateFormat("yyyy-MM-dd")
          .format(startDateSpinner.getValue()));
      inputs.put("startTime", new SimpleDateFormat("HH:mm")
          .format(startTimeSpinner.getValue()));
      inputs.put("endDate", new SimpleDateFormat("yyyy-MM-dd")
          .format(endDateSpinner.getValue()));
      inputs.put("endTime", new SimpleDateFormat("HH:mm")
          .format(endTimeSpinner.getValue()));

      inputs.put("isAllDay", allDayCheckBox.isSelected() ? "true" : "false");
      inputs.put("isPrivate", privateCheckBox.isSelected() ? "true" : "false");
      inputs.put("description", descriptionTextField.getText());
      inputs.put("location", locationTextField.getText());
      inputs.put("isRepeating", repeatingCheckBox.isSelected() ? "true" : "false");

      inputs.put("repeatMonday", repeatMondayCheckBox.isSelected() ? "true" : "false");
      inputs.put("repeatTuesday", repeatTuesdayCheckBox.isSelected() ? "true" : "false");
      inputs.put("repeatWednesday", repeatWednesdayCheckBox.isSelected() ? "true" : "false");
      inputs.put("repeatThursday", repeatThursdayCheckBox.isSelected() ? "true" : "false");
      inputs.put("repeatFriday", repeatFridayCheckBox.isSelected() ? "true" : "false");
      inputs.put("repeatSaturday", repeatSaturdayCheckBox.isSelected() ? "true" : "false");
      inputs.put("repeatSunday", repeatSundayCheckBox.isSelected() ? "true" : "false");

      inputs.put("repeatNumberType", repeatNumberRadioButton.isSelected() ? "true" : "false");
      inputs.put("repeatUntilType", repeatUntilRadioButton.isSelected() ? "true" : "false");

      inputs.put("repeatNumber", repeatNumberTextField.getText());
      inputs.put("repeatEndDate", new SimpleDateFormat("yyyy-MM-dd")
          .format(repeatEndDateSpinner.getValue()));
      inputs.put("repeatEndTime", new SimpleDateFormat("HH:mm")
          .format(repeatEndTimeSpinner.getValue()));

      if (features.addEvent(inputs)) {
        callback();
        dispose();
      }
    });
  }

  private void callback() {
    onEventAdded.accept(viewModel.getSelectedDate());
  }

  private Calendar getStartOfDay(Date date) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    calendar.set(Calendar.HOUR_OF_DAY, 0);
    calendar.set(Calendar.MINUTE, 0);
    return calendar;
  }

  private Calendar plusOneDay(Date date) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    calendar.add(Calendar.DATE, 1);
    return calendar;
  }
}
