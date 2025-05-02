package gui;

import java.awt.Dialog;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.BoxLayout;
import javax.swing.Box;
import javax.swing.JDialog;

import java.util.function.Consumer;

import calendar.ICalendar;

/**
 * A Java class representing a UI frame for changing the active calendar in the application. Extends
 * the JDialog class.
 */
public class SelectCalendarDialog extends JDialog implements IDialog {

  private final IViewModel viewModel;
  private final Consumer<ICalendar> onCalendarSelected;

  private final SelectCalendarComboBox selectCalendarComboBox;
  private final JButton confirmButton;

  /**
   * Constructs a SelectCalendarDialog object. Calls the JDialog constructor.
   *
   * @param parent    parent Dialog object
   * @param viewModel IViewModel object
   * @param callback  Consumer that accepts an ICalendar object
   */
  public SelectCalendarDialog(Dialog parent, IViewModel viewModel, Consumer<ICalendar> callback) {
    super(parent, "Select Calendar");
    this.viewModel = viewModel;
    this.onCalendarSelected = callback;

    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    setBounds(100, 100, 450, 180);
    setLocationRelativeTo(null);
    setResizable(false);

    JPanel panel = new JPanel();
    panel.setLayout(new GridBagLayout());
    GridBagConstraints constraint = new GridBagConstraints();
    constraint.insets = new Insets(5, 10, 5, 10); // Add padding

    // Label
    constraint.gridx = 0;
    constraint.gridy = 0;
    constraint.anchor = GridBagConstraints.LINE_END;
    constraint.fill = GridBagConstraints.NONE;
    panel.add(new JLabel("Select calendar"), constraint);

    constraint.gridx = 1;
    constraint.gridy = 0;
    constraint.anchor = GridBagConstraints.LINE_START;
    constraint.fill = GridBagConstraints.HORIZONTAL;

    selectCalendarComboBox = new SelectCalendarComboBox(viewModel.getAllCalendars());
    selectCalendarComboBox.setSelectedItem(viewModel.getCurrentCalendar().getName());
    panel.add(selectCalendarComboBox, constraint);

    JPanel buttonPanel = new JPanel();
    buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
    buttonPanel.add(Box.createHorizontalStrut(10));

    confirmButton = new JButton("Confirm");
    buttonPanel.add(confirmButton);

    buttonPanel.add(Box.createHorizontalStrut(5));
    JButton cancelButton = new JButton("Cancel");
    cancelButton.addActionListener(e -> dispose());
    buttonPanel.add(cancelButton);

    // Add the button panel to the main panel
    constraint.gridx = 0;
    constraint.gridy = 2;
    constraint.gridwidth = 2;
    constraint.anchor = GridBagConstraints.CENTER;
    constraint.fill = GridBagConstraints.NONE;
    panel.add(buttonPanel, constraint);

    add(panel);
  }

  @Override
  public void setFeatures(Features features) {
    confirmButton.addActionListener(e -> {
      String name = (String) selectCalendarComboBox.getSelectedItem();

      if (features.useCalendar(name)) {
        callback();
        dispose();
      }
    });
  }

  private void callback() {
    onCalendarSelected.accept(viewModel.getCurrentCalendar());
  }
}
