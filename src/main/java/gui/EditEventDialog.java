package gui;

import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JCheckBox;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;
import javax.swing.JSpinner;
import javax.swing.JComboBox;
import javax.swing.JDialog;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.chrono.ChronoZonedDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.function.Consumer;

import calendar.IEvent;
import utils.TimeUtils;

/**
 * A Java class representing a GUI dialog for editing a single existing event in the application.
 * Extends the JDialog class and implements the IDialog interface.
 */
public class EditEventDialog extends JDialog implements IDialog {

  private final IViewModel viewModel;
  private final Consumer<Date> onEventEdited;

  private final JTextField subjectTextField;
  private final JSpinner startDateSpinner;
  private final JSpinner startTimeSpinner;
  private final JSpinner endDateSpinner;
  private final JSpinner endTimeSpinner;

  private final JTextField newValueTextField;
  private final JSpinner dateSpinner;
  private final JSpinner timeSpinner;
  private final JCheckBox isPrivateCheckbox;
  private final JComboBox<String> selectPropertyDropdown;

  private final JButton confirmButton;

  /**
   * Constructs an EditEventDialog object. Calls the JDialog constructor.
   *
   * @param parent    parent Frame object
   * @param viewModel IViewModel object
   * @param callback  Consumer that accepts a Date parameter
   */
  public EditEventDialog(Frame parent, IViewModel viewModel, Consumer<Date> callback) {
    super(parent, "Edit Event");
    this.viewModel = viewModel;
    this.onEventEdited = callback;
    IEvent event = viewModel.getSelectedEvent();

    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    setBounds(100, 100, 450, 400);
    setLocationRelativeTo(null);
    setResizable(false);

    JPanel panel = new JPanel(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(5, 5, 5, 5);
    gbc.fill = GridBagConstraints.HORIZONTAL;

    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.gridwidth = 1;
    panel.add(new JLabel("Subject"), gbc);
    gbc.gridx = 1;
    subjectTextField = new JTextField(event.getSubject());
    subjectTextField.setEditable(false);
    panel.add(subjectTextField, gbc);

    gbc.gridx = 0;
    gbc.gridy = 1;
    panel.add(new JLabel("Start date"), gbc);
    gbc.gridx = 1;
    SpinnerDateModel startDateTimeModel = new SpinnerDateModel();
    ChronoZonedDateTime<LocalDate> startDateTime = event.getStartDateTime();
    startDateTimeModel.setValue(TimeUtils.asDate(startDateTime));
    startDateSpinner = new JSpinner(startDateTimeModel);

    SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
    dateFormat.setTimeZone(TimeZone.getTimeZone(startDateTime.getZone()));
    JSpinner.DateEditor editor = new JSpinner.DateEditor(startDateSpinner, dateFormat.toPattern());
    editor.getFormat().setTimeZone(dateFormat.getTimeZone());
    startDateSpinner.setEditor(editor);
    startDateSpinner.setEnabled(false);
    panel.add(startDateSpinner, gbc);

    gbc.gridx = 0;
    gbc.gridy = 2;
    panel.add(new JLabel("Start time"), gbc);
    gbc.gridx = 1;
    startTimeSpinner = new JSpinner(startDateTimeModel);
    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
    timeFormat.setTimeZone(TimeZone.getTimeZone(startDateTime.getZone()));
    JSpinner.DateEditor timeEditor = new JSpinner.DateEditor(startTimeSpinner,
        timeFormat.toPattern());
    timeEditor.getFormat().setTimeZone(timeFormat.getTimeZone());
    startTimeSpinner.setEditor(timeEditor);
    startTimeSpinner.setEnabled(false);
    panel.add(startTimeSpinner, gbc);

    gbc.gridx = 0;
    gbc.gridy = 3;
    panel.add(new JLabel("End date"), gbc);
    gbc.gridx = 1;
    SpinnerDateModel endDateTimeModel = new SpinnerDateModel();
    endDateTimeModel.setValue(TimeUtils.asDate(event.getEndDateTime()));
    endDateSpinner = new JSpinner(endDateTimeModel);
    endDateSpinner.setEditor(new JSpinner.DateEditor(endDateSpinner, "MM/dd/yyyy"));
    endDateSpinner.setEnabled(false);
    panel.add(endDateSpinner, gbc);

    gbc.gridx = 0;
    gbc.gridy = 4;
    panel.add(new JLabel("End time"), gbc);
    gbc.gridx = 1;
    endTimeSpinner = new JSpinner(endDateTimeModel);
    endTimeSpinner.setEditor(new JSpinner.DateEditor(endTimeSpinner, "HH:mm"));
    endTimeSpinner.setEnabled(false);
    panel.add(endTimeSpinner, gbc);

    JPanel newValuePanel = new JPanel(new CardLayout());
    newValueTextField = new JTextField(10);
    newValuePanel.add(newValueTextField, "text");

    // Date + time inputs
    JPanel dateTimePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    dateSpinner = new JSpinner(new SpinnerDateModel());
    dateSpinner.setEditor(new JSpinner.DateEditor(dateSpinner, "MM/dd/yyyy"));
    dateTimePanel.add(dateSpinner);

    timeSpinner = new JSpinner(new SpinnerDateModel());
    timeSpinner.setEditor(new JSpinner.DateEditor(timeSpinner, "HH:mm"));
    dateTimePanel.add(timeSpinner);
    newValuePanel.add(dateTimePanel, "dateTime");

    // private option input
    JPanel privatePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    isPrivateCheckbox = new JCheckBox("Private", event.isPrivate());
    privatePanel.add(isPrivateCheckbox);
    newValuePanel.add(privatePanel, "private");

    gbc.gridx = 0;
    gbc.gridy = 6;
    panel.add(new JLabel("New value"), gbc);
    gbc.gridx = 1;
    panel.add(newValuePanel, gbc);

    gbc.gridx = 0;
    gbc.gridy = 5;
    JLabel propertyLabel = new JLabel("Select property");
    panel.add(propertyLabel, gbc);
    gbc.gridx = 1;
    String[] options = viewModel.getEditableEventProperties();
    selectPropertyDropdown = new JComboBox<>(options);
    selectPropertyDropdown.setPreferredSize(new Dimension(150,
        selectPropertyDropdown.getPreferredSize().height));
    panel.add(selectPropertyDropdown, gbc);

    selectPropertyDropdown.addActionListener(e -> {
      String selected = (String) selectPropertyDropdown.getSelectedItem();
      CardLayout cl = (CardLayout) (newValuePanel.getLayout());

      assert selected != null;
      if (selected.equals("private")) {
        cl.show(newValuePanel, "private");
      } else if (selected.equals("startDateTime")
          || selected.equals("endDateTime")) {
        cl.show(newValuePanel, "dateTime");
      } else {
        cl.show(newValuePanel, "text");
      }
    });

    JPanel buttonPanel = new JPanel(new FlowLayout());
    confirmButton = new JButton("Confirm");
    buttonPanel.add(confirmButton);

    JButton cancelButton = new JButton("Cancel");
    cancelButton.addActionListener(e -> dispose());
    buttonPanel.add(cancelButton);

    gbc.gridy = 8;
    gbc.gridx = 0;
    gbc.gridwidth = 2;
    gbc.anchor = GridBagConstraints.EAST;
    panel.add(buttonPanel, gbc);

    add(panel);
  }

  @Override
  public void setFeatures(Features features) {
    confirmButton.addActionListener(e -> {
      Map<String, String> inputs = new HashMap<>();
      inputs.put("subject", subjectTextField.getText());
      inputs.put("property", (String) selectPropertyDropdown.getSelectedItem());
      inputs.put("startDate", new SimpleDateFormat("yyyy-MM-dd")
          .format(startDateSpinner.getValue()));
      inputs.put("startTime", new SimpleDateFormat("HH:mm")
          .format(startTimeSpinner.getValue()));
      inputs.put("endDate", new SimpleDateFormat("yyyy-MM-dd")
          .format(endDateSpinner.getValue()));
      inputs.put("endTime", new SimpleDateFormat("HH:mm")
          .format(endTimeSpinner.getValue()));

      inputs.put("isPrivate", isPrivateCheckbox.isSelected() ? "true" : "false");
      inputs.put("newDate", new SimpleDateFormat("yyyy-MM-dd")
          .format(dateSpinner.getValue()));
      inputs.put("newTime", new SimpleDateFormat("HH:mm").format(timeSpinner.getValue()));
      inputs.put("newValue", newValueTextField.getText());

      if (features.editEvent(inputs)) {
        callback();
        dispose();
      }
    });
  }

  private void callback() {
    this.onEventEdited.accept(viewModel.getSelectedDate());
  }
}
