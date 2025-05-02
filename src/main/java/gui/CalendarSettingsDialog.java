package gui;

import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Insets;

import java.util.function.Consumer;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JDialog;

import calendar.ICalendar;

/**
 * Java class representing a Calendar Settings GUI dialog. Extends the JDialog class.
 */
public class CalendarSettingsDialog extends JDialog {

  private final IViewModel viewModel;
  private final Consumer<ICalendar> onCalendarUpdated;
  private final JLabel currentCalendarLabel;

  /**
   * Constructs a CalendarSettingsDialog object. Calls the JDialog constructor.
   *
   * @param parent    parent Frame object
   * @param viewModel IViewModel object
   * @param callback  consumer that accepts an ICalendar parameter
   */
  public CalendarSettingsDialog(Frame parent, IViewModel viewModel, Features features,
      Consumer<ICalendar> callback) {
    super(parent, "Calendar Settings");
    this.viewModel = viewModel;
    this.onCalendarUpdated = callback;

    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    setBounds(200, 200, 400, 280);
    setLocationRelativeTo(null);

    JPanel mainPanel = new JPanel(new BorderLayout());

    JPanel northPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    currentCalendarLabel = new JLabel();
    updateLabel(viewModel.getCurrentCalendar().getName());
    northPanel.add(currentCalendarLabel);
    mainPanel.add(northPanel, BorderLayout.NORTH);

    JPanel centerPanel = new JPanel();
    centerPanel.setLayout(new GridBagLayout());
    GridBagConstraints constraint = new GridBagConstraints();

    // Add calendar button
    constraint.gridx = 0;
    constraint.gridy = 0;
    constraint.gridwidth = 2;
    constraint.anchor = GridBagConstraints.CENTER; // Center align
    constraint.fill = GridBagConstraints.HORIZONTAL; // Allow stretching

    JButton addCalendarButton = new JButton("Add Calendar");
    addCalendarButton.addActionListener(e -> {
      IDialog addCalendarDialog = new AddCalendarDialog(this, viewModel);
      addCalendarDialog.setFeatures(features);
      addCalendarDialog.setVisible(true);
    });
    centerPanel.add(addCalendarButton, constraint);

    constraint.gridx = 0;
    constraint.gridy = 1;

    JButton editCalendarButton = new JButton("Edit Calendar");
    editCalendarButton.addActionListener(e -> {
      IDialog editCalendarDialog = new EditCalendarDialog(this, viewModel,
          calendar -> updateLabel(calendar.getName()));
      editCalendarDialog.setFeatures(features);
      editCalendarDialog.setVisible(true);
    });
    centerPanel.add(editCalendarButton, constraint);

    // select calendar button
    constraint.gridx = 0;
    constraint.gridy = 2;
    constraint.anchor = GridBagConstraints.WEST;
    constraint.insets = new Insets(2, 5, 2, 5);
    JButton selectCalendarButton = new JButton("Select Calendar");
    selectCalendarButton.addActionListener(e -> {
      IDialog selectCalendarDialog = new SelectCalendarDialog(
          this, viewModel, calendar -> updateLabel(calendar.getName()));
      selectCalendarDialog.setFeatures(features);
      selectCalendarDialog.setVisible(true);
    });
    centerPanel.add(selectCalendarButton, constraint);

    // Import export buttons
    constraint.gridx = 0;
    constraint.gridy = 3;
    constraint.gridwidth = 1;
    constraint.fill = GridBagConstraints.NONE;
    constraint.anchor = GridBagConstraints.WEST;
    constraint.insets = new Insets(10, 5, 2, 5);

    JButton importButton = new JButton("Import calendar");
    importButton.addActionListener(
        e -> displayImportCalendarDialog(viewModel, features));
    centerPanel.add(importButton, constraint);

    constraint.gridx = 1;
    JButton exportButton = new JButton("Export calendar");
    exportButton.addActionListener(e -> displayExportCalendarDialog(features));
    centerPanel.add(exportButton, constraint);

    // back button
    constraint.gridx = 0;
    constraint.gridy = 4;
    constraint.gridwidth = 2;
    constraint.anchor = GridBagConstraints.CENTER;
    JButton backButton = new JButton("Back");
    backButton.addActionListener(e -> dispose());
    centerPanel.add(backButton, constraint);

    mainPanel.add(centerPanel, BorderLayout.CENTER);
    add(mainPanel);
  }

  /**
   * Calls the callback function before disposing the window.
   */
  @Override
  public void dispose() {
    callback();
    super.dispose();
  }

  private void callback() {
    onCalendarUpdated.accept(viewModel.getCurrentCalendar());
  }

  private void updateLabel(String calendarName) {
    this.currentCalendarLabel.setText("Current calendar: " + calendarName);
  }

  private void displayExportCalendarDialog(Features features) {
    IDialog exportCalendarDialog = new ExportCalendarDialog(this);
    exportCalendarDialog.setFeatures(features);
    exportCalendarDialog.setVisible(true);
  }

  private void displayImportCalendarDialog(IViewModel viewModel, Features features) {
    IDialog importCalendarDialog = new ImportCalendarDialog(this, viewModel,
        localDate -> viewModel.getCurrentCalendar());
    importCalendarDialog.setFeatures(features);
    importCalendarDialog.setVisible(true);
  }
}
