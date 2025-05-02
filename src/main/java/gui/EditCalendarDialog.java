package gui;

import java.awt.CardLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import java.util.function.Consumer;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.BoxLayout;
import javax.swing.Box;

import calendar.ICalendar;

/**
 * A Java class representing a GUI dialog for editing a calendar in the application. Extends the
 * JDialog class and implements the IDialog interface.
 */
public class EditCalendarDialog extends JDialog implements IDialog {

  private final IViewModel viewModel;
  private final Consumer<ICalendar> onCalendarEdited;

  private final JButton confirmButton;
  private final JComboBox<String> selectCalendarComboBox;
  private final JComboBox<String> selectPropertyComboBox;
  private final JTextField nameTextField;
  private final JComboBox<String> timezoneComboBox;

  /**
   * Constructs an EditCalendarDialog object.
   *
   * @param parent    parent Dialog object
   * @param viewModel IViewModel object
   * @param callback  Consumer that accepts an ICalendar object
   */
  public EditCalendarDialog(Dialog parent, IViewModel viewModel, Consumer<ICalendar> callback) {
    super(parent, "Edit Calendar");
    this.onCalendarEdited = callback;
    this.viewModel = viewModel;
    ICalendar calendar = viewModel.getCurrentCalendar();

    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    setBounds(100, 100, 450, 180);
    setLocationRelativeTo(null);
    setResizable(false);

    JPanel panel = new JPanel();
    panel.setLayout(new GridBagLayout());
    GridBagConstraints constraint = new GridBagConstraints();
    constraint.insets = new Insets(5, 10, 5, 10);

    // Name label
    constraint.gridx = 0;
    constraint.gridy = 0;
    constraint.anchor = GridBagConstraints.LINE_END;
    constraint.fill = GridBagConstraints.NONE;
    panel.add(new JLabel("Calendar name"), constraint);

    // calendar name dropdown
    constraint.gridx = 1;
    constraint.gridy = 0;
    selectCalendarComboBox = new SelectCalendarComboBox(viewModel.getAllCalendars());
    selectCalendarComboBox.setSelectedItem(viewModel.getCurrentCalendar().getName());
    panel.add(selectCalendarComboBox, constraint);

    // property dropdown
    constraint.gridx = 0;
    constraint.gridy = 1;
    panel.add(new JLabel("Select property"), constraint);
    constraint.gridx = 1;
    String[] options = viewModel.getEditableCalendarProperties();
    selectPropertyComboBox = new JComboBox<>(options);
    selectPropertyComboBox.setPreferredSize(new Dimension(150,
        selectPropertyComboBox.getPreferredSize().height));
    panel.add(selectPropertyComboBox, constraint);

    // Timezone Label
    constraint.gridx = 0;
    constraint.gridy = 2;
    constraint.anchor = GridBagConstraints.LINE_END;
    constraint.fill = GridBagConstraints.NONE;
    panel.add(new JLabel("New value"), constraint);

    Dimension fieldSize = selectCalendarComboBox.getPreferredSize();
    JPanel newValuePanel = new JPanel(new CardLayout());

    // timezones dropdown
    String[] timezones = viewModel.getAvailableTimezones();
    timezoneComboBox = new JComboBox<>(timezones);
    timezoneComboBox.setSelectedItem(calendar.getTimezone().toString());
    timezoneComboBox.setPreferredSize(new Dimension(fieldSize.width, fieldSize.height));
    newValuePanel.add(timezoneComboBox, "timezone");

    // new name text field
    nameTextField = new JTextField(10);
    nameTextField.setText(calendar.getName());
    nameTextField.setPreferredSize(new Dimension(fieldSize.width, fieldSize.height));
    newValuePanel.add(nameTextField, "name");

    constraint.gridx = 1;
    panel.add(newValuePanel, constraint);

    selectPropertyComboBox.addActionListener(e -> {
      String selectedProperty = (String) selectPropertyComboBox.getSelectedItem();
      CardLayout cardLayout = (CardLayout) (newValuePanel.getLayout());
      // Show the card corresponding to the selected property ("name" or "timezone")
      cardLayout.show(newValuePanel, selectedProperty);
    });

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
    constraint.gridy = 3;
    constraint.gridwidth = 2;
    constraint.anchor = GridBagConstraints.CENTER;
    constraint.fill = GridBagConstraints.NONE;
    panel.add(buttonPanel, constraint);

    // Add some empty space at the bottom
    constraint.gridx = 0;
    constraint.gridy = 3;
    constraint.gridwidth = 2;
    constraint.weighty = 1.0;
    panel.add(Box.createVerticalStrut(10), constraint);

    add(panel);
  }

  @Override
  public void setFeatures(Features features) {
    confirmButton.addActionListener(e -> {

      String name = (String) selectCalendarComboBox.getSelectedItem();
      String property = (String) selectPropertyComboBox.getSelectedItem();
      String newValue;

      assert property != null;
      if (property.equals("name")) {
        newValue = nameTextField.getText();
      } else {
        newValue = (String) timezoneComboBox.getSelectedItem();
      }

      if (features.editCalendar(name, property, newValue)) {
        callback();
        dispose();
      }
    });
  }

  private void callback() {
    onCalendarEdited.accept(viewModel.getCurrentCalendar());
  }
}
