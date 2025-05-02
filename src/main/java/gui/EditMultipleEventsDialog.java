package gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;
import javax.swing.ButtonGroup;
import javax.swing.JDialog;

/**
 * A Java class representing a UI frame for editing multiple events. Extends the JDialog class and
 * implements the IDialog.
 */
public class EditMultipleEventsDialog extends JDialog implements IDialog {

  private final IViewModel viewModel;
  private final Consumer<Date> onEventsEdited;

  private final JTextField subjectTextField;
  private final JRadioButton subjectRadioButton;
  private final JRadioButton rangeRadioButton;
  private final JSpinner fromDateSpinner;
  private final JSpinner fromTimeSpinner;
  private final JComboBox<String> selectPropertyDropdown;
  private final JTextField newValueField;
  private final JSpinner dateSpinner;
  private final JSpinner timeSpinner;
  private final JCheckBox isPrivateCheckbox;

  private JCheckBox repeatMondayCheckBox;
  private JCheckBox repeatTuesdayCheckBox;
  private JCheckBox repeatWednesdayCheckBox;
  private JCheckBox repeatThursdayCheckBox;
  private JCheckBox repeatFridayCheckBox;
  private JCheckBox repeatSaturdayCheckBox;
  private JCheckBox repeatSundayCheckBox;

  private final JButton confirmButton;

  /**
   * Constructs an EditMultipleEventsDialog object.
   *
   * @param parent    parent Frame object
   * @param viewModel IViewModel object
   * @param callback  Consumer that accepts a Date parameter
   */
  public EditMultipleEventsDialog(Frame parent, IViewModel viewModel,
      Consumer<Date> callback) {
    super(parent, "Edit Events");
    this.viewModel = viewModel;
    this.onEventsEdited = callback;

    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    setBounds(100, 100, 540, 400);
    setLocationRelativeTo(null);
    setResizable(false);

    // Create a content panel with BorderLayout to separate the main fields from the button panel
    JPanel contentPanel = new JPanel(new BorderLayout());

    // Create the main panel with GridBagLayout for the labels, text fields, and other inputs.
    JPanel mainPanel = new JPanel(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(5, 5, 5, 5);
    gbc.fill = GridBagConstraints.HORIZONTAL;

    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.gridwidth = 1;
    mainPanel.add(new JLabel("Enter subject"), gbc);
    gbc.gridx = 1;
    subjectTextField = new JTextField(10);
    mainPanel.add(subjectTextField, gbc);

    // Label for choosing method
    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.gridwidth = 2;
    mainPanel.add(new JLabel("Choose method"), gbc);

    // Radio buttons for methods
    ButtonGroup methodGroup = new ButtonGroup();
    gbc.gridx = 0;
    gbc.gridy = 2;
    gbc.gridwidth = 2;
    gbc.weightx = 1.0;
    subjectRadioButton = new JRadioButton("Edit events by subject", true);
    methodGroup.add(subjectRadioButton);
    mainPanel.add(subjectRadioButton, gbc);

    gbc.gridy = 3;
    rangeRadioButton = new JRadioButton("Edit events by subject from a date");
    methodGroup.add(rangeRadioButton);
    mainPanel.add(rangeRadioButton, gbc);

    JPanel rangePanel = new JPanel(new GridBagLayout());
    rangePanel.setVisible(false);
    GridBagConstraints rangeConstraints = new GridBagConstraints();
    rangeConstraints.insets = new Insets(5, 5, 5, 5);
    rangeConstraints.fill = GridBagConstraints.WEST;
    rangeConstraints.gridx = 0;
    rangeConstraints.gridy = 0;
    rangePanel.add(new JLabel("Enter range start date"), rangeConstraints);

    rangeConstraints.gridx = 1;
    fromDateSpinner = new JSpinner(new SpinnerDateModel());
    fromDateSpinner.setEditor(new JSpinner.DateEditor(fromDateSpinner, "MM/dd/yyyy"));
    rangePanel.add(fromDateSpinner, rangeConstraints);

    rangeConstraints.gridx = 0;
    rangeConstraints.gridy = 1;
    rangePanel.add(new JLabel("Enter range start time"), rangeConstraints);

    rangeConstraints.gridx = 1;
    fromTimeSpinner = new JSpinner(new SpinnerDateModel());
    fromTimeSpinner.setEditor(new JSpinner.DateEditor(fromTimeSpinner, "HH:mm"));
    rangePanel.add(fromTimeSpinner, rangeConstraints);

    JPanel inputPanel = new JPanel(new CardLayout());
    inputPanel.add(new JPanel(), "none");
    inputPanel.add(rangePanel, "range");
    CardLayout cl = (CardLayout) inputPanel.getLayout();
    cl.show(inputPanel, "none");

    GridBagConstraints inputPanelConstraints = new GridBagConstraints();
    inputPanelConstraints.insets = new Insets(5, 5, 5, 5);
    inputPanelConstraints.fill = GridBagConstraints.WEST;
    inputPanelConstraints.gridx = 0;
    inputPanelConstraints.gridy = 4;
    inputPanelConstraints.gridwidth = 2;
    mainPanel.add(inputPanel, inputPanelConstraints);

    gbc.gridx = 0;
    gbc.gridy = 5;
    gbc.gridwidth = 1;
    gbc.weightx = 0;
    gbc.fill = GridBagConstraints.NONE;
    gbc.anchor = GridBagConstraints.WEST;
    mainPanel.add(new JLabel("Select property"), gbc);

    gbc.gridx = 1;
    gbc.gridwidth = 1;
    gbc.weightx = 1.0;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.anchor = GridBagConstraints.WEST;
    String[] options = viewModel.getEditableMultipleEventProperties();
    selectPropertyDropdown = new JComboBox<>(options);
    selectPropertyDropdown.setPreferredSize(
        new Dimension(100, selectPropertyDropdown.getPreferredSize().height));
    mainPanel.add(selectPropertyDropdown, gbc);

    gbc.gridy = 6;
    gbc.gridx = 0;
    gbc.gridwidth = 1;
    gbc.weightx = 0;
    gbc.fill = GridBagConstraints.NONE;
    gbc.anchor = GridBagConstraints.WEST;
    mainPanel.add(new JLabel("New value"), gbc);

    gbc.gridx = 1;
    gbc.weightx = 1.0;
    gbc.fill = GridBagConstraints.HORIZONTAL;

    JPanel newValueCardPanel = new JPanel(new CardLayout());
    JPanel textFieldPanel = new JPanel();
    textFieldPanel.setLayout(new BoxLayout(textFieldPanel, BoxLayout.X_AXIS));

    newValueField = new JTextField(10);
    newValueField.setMaximumSize(
        new Dimension(Integer.MAX_VALUE, newValueField.getPreferredSize().height));
    textFieldPanel.add(newValueField);
    newValueCardPanel.add(textFieldPanel, "text");

    JPanel repeatDaysPanel = createRepeatDaysPanel();
    newValueCardPanel.add(repeatDaysPanel, "repeatDays");

    // Date time spinners
    JPanel dateTimePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    dateSpinner = new JSpinner(new SpinnerDateModel());
    dateSpinner.setEditor(new JSpinner.DateEditor(dateSpinner, "MM/dd/yyyy"));
    dateTimePanel.add(dateSpinner);

    timeSpinner = new JSpinner(new SpinnerDateModel());
    timeSpinner.setEditor(new JSpinner.DateEditor(timeSpinner, "HH:mm"));
    dateTimePanel.add(timeSpinner);
    newValueCardPanel.add(dateTimePanel, "dateTime");

    // private option input
    JPanel privatePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    isPrivateCheckbox = new JCheckBox("Private");
    privatePanel.add(isPrivateCheckbox);
    newValueCardPanel.add(privatePanel, "private");

    CardLayout newValueCardLayout = (CardLayout) newValueCardPanel.getLayout();
    newValueCardLayout.show(newValueCardPanel, "text");

    mainPanel.add(newValueCardPanel, gbc);

    JPanel buttonPanel = new JPanel();
    buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
    buttonPanel.add(Box.createHorizontalGlue());

    confirmButton = new JButton("Confirm");
    buttonPanel.add(confirmButton);
    buttonPanel.add(Box.createRigidArea(new Dimension(20, 0)));

    JButton cancelButton = new JButton("Cancel");
    cancelButton.addActionListener(e -> dispose());
    buttonPanel.add(cancelButton);
    buttonPanel.add(Box.createHorizontalGlue());

    contentPanel.add(mainPanel, BorderLayout.CENTER);
    contentPanel.add(buttonPanel, BorderLayout.SOUTH);
    setContentPane(contentPanel);

    subjectRadioButton.addActionListener(e -> {
      cl.show(inputPanel, "none");
      mainPanel.revalidate();
      mainPanel.repaint();
    });

    rangeRadioButton.addActionListener(e -> {
      cl.show(inputPanel, "range");
      mainPanel.revalidate();
      mainPanel.repaint();
    });

    selectPropertyDropdown.addActionListener(e -> {
      String selected = (String) selectPropertyDropdown.getSelectedItem();

      assert selected != null;
      switch (selected) {
        case "repeatDays":
          newValueCardLayout.show(newValueCardPanel, "repeatDays");
          break;
        case "private":
          newValueCardLayout.show(newValueCardPanel, "private");
          break;
        case "repeatEndDateTime":
          newValueCardLayout.show(newValueCardPanel, "dateTime");
          break;
        default:
          newValueCardLayout.show(newValueCardPanel, "text");
          break;
      }
      mainPanel.revalidate();
      mainPanel.repaint();
    });
  }

  private JPanel createRepeatDaysPanel() {
    JPanel repeatDaysPanel = new JPanel(new GridLayout(1, 7, 2, 5));
    repeatMondayCheckBox = new JCheckBox("Mon");
    repeatDaysPanel.add(repeatMondayCheckBox);

    repeatTuesdayCheckBox = new JCheckBox("Tue");
    repeatDaysPanel.add(repeatTuesdayCheckBox);

    repeatWednesdayCheckBox = new JCheckBox("Wed");
    repeatDaysPanel.add(repeatWednesdayCheckBox);

    repeatThursdayCheckBox = new JCheckBox("Thu");
    repeatDaysPanel.add(repeatThursdayCheckBox);

    repeatFridayCheckBox = new JCheckBox("Fri");
    repeatDaysPanel.add(repeatFridayCheckBox);

    repeatSaturdayCheckBox = new JCheckBox("Sat");
    repeatDaysPanel.add(repeatSaturdayCheckBox);

    repeatSundayCheckBox = new JCheckBox("Sun");
    repeatDaysPanel.add(repeatSundayCheckBox);
    return repeatDaysPanel;
  }

  private void callback() {
    onEventsEdited.accept(viewModel.getSelectedDate());
  }

  @Override
  public void setFeatures(Features features) {
    confirmButton.addActionListener(e -> {
      Map<String, String> inputs = new HashMap<>();
      inputs.put("editBySubject", subjectRadioButton.isSelected() ? "true" : "false");
      inputs.put("editByDate", rangeRadioButton.isSelected() ? "true" : "false");

      inputs.put("subject", subjectTextField.getText());
      inputs.put("property", (String) selectPropertyDropdown.getSelectedItem());
      inputs.put("isPrivate", isPrivateCheckbox.isSelected() ? "true" : "false");
      inputs.put("newDate", new SimpleDateFormat("yyyy-MM-dd")
          .format(dateSpinner.getValue()));
      inputs.put("newTime", new SimpleDateFormat("HH:mm").format(timeSpinner.getValue()));
      inputs.put("newValue", newValueField.getText());
      inputs.put("startDate", new SimpleDateFormat("yyyy-MM-dd")
          .format(fromDateSpinner.getValue()));
      inputs.put("startTime", new SimpleDateFormat("HH:mm")
          .format(fromTimeSpinner.getValue()));

      inputs.put("repeatMonday", repeatMondayCheckBox.isSelected() ? "true" : "false");
      inputs.put("repeatTuesday", repeatTuesdayCheckBox.isSelected() ? "true" : "false");
      inputs.put("repeatWednesday", repeatWednesdayCheckBox.isSelected() ? "true" : "false");
      inputs.put("repeatThursday", repeatThursdayCheckBox.isSelected() ? "true" : "false");
      inputs.put("repeatFriday", repeatFridayCheckBox.isSelected() ? "true" : "false");
      inputs.put("repeatSaturday", repeatSaturdayCheckBox.isSelected() ? "true" : "false");
      inputs.put("repeatSunday", repeatSundayCheckBox.isSelected() ? "true" : "false");

      if (features.editEvents(inputs)) {
        callback();
        dispose();
      }
    });
  }
}
