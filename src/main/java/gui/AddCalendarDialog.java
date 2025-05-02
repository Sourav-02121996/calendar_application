package gui;

import java.awt.Dialog;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.JPanel;
import javax.swing.JDialog;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.BoxLayout;
import javax.swing.Box;

/**
 * A Java class representing a GUI frame for adding a new calendar in the application. Extends the
 * JDialog class and implements the IDialog interface.
 */
public class AddCalendarDialog extends JDialog implements IDialog {

  private final JButton addButton;
  private final JTextField nameTextField;
  private final JComboBox<String> timezoneComboBox;

  /**
   * Constructs an AddCalendarDialog object.
   *
   * @param parent    parent Dialog object
   * @param viewModel IViewModel object
   */
  public AddCalendarDialog(Dialog parent, IViewModel viewModel) {
    super(parent, "Add Calendar");

    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    setBounds(100, 100, 450, 180);
    setLocationRelativeTo(null);
    setResizable(false);

    JPanel panel = new JPanel();
    panel.setLayout(new GridBagLayout());
    GridBagConstraints constraint = new GridBagConstraints();
    constraint.insets = new Insets(5, 10, 5, 10); // Add padding

    // Name label
    constraint.gridx = 0;
    constraint.gridy = 0;
    constraint.anchor = GridBagConstraints.LINE_END;
    constraint.fill = GridBagConstraints.NONE;
    panel.add(new JLabel("Name:"), constraint);

    // Name Text Field
    constraint.gridx = 1;
    constraint.gridy = 0;
    constraint.anchor = GridBagConstraints.LINE_START;
    constraint.fill = GridBagConstraints.HORIZONTAL;
    nameTextField = new JTextField(15);
    panel.add(nameTextField, constraint);

    // Timezone Label
    constraint.gridx = 0;
    constraint.gridy = 1;
    constraint.anchor = GridBagConstraints.LINE_END;
    constraint.fill = GridBagConstraints.NONE;
    panel.add(new JLabel("Timezone:"), constraint);

    // Timezone dropdown
    constraint.gridx = 1;
    constraint.gridy = 1;
    constraint.anchor = GridBagConstraints.LINE_START;
    constraint.fill = GridBagConstraints.HORIZONTAL;
    String[] timeZones = viewModel.getAvailableTimezones();
    timezoneComboBox = new JComboBox<>(timeZones);
    panel.add(timezoneComboBox, constraint);

    JPanel buttonPanel = new JPanel();
    buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
    buttonPanel.add(Box.createHorizontalStrut(10));

    addButton = new JButton("Add");
    buttonPanel.add(addButton);

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
    addButton.addActionListener(e -> {
      String name = nameTextField.getText();
      String timezone = (String) timezoneComboBox.getSelectedItem();

      if (features.addCalendar(name, timezone)) {
        dispose();
      }
    });
  }
}
