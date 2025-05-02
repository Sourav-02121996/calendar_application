package gui;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JDialog;

/**
 * A Java class representing a UI frame for exporting a calendar to a file. Extends the JDialog
 * class and implements the IDialog interface.
 */
public class ExportCalendarDialog extends JDialog implements IDialog {

  private final JTextField folderTextField;
  private final JButton exportButton;

  /**
   * Constructs an ExportCalendarDialog object. Calls the JDialog constructor.
   *
   * @param parent parent Dialog object
   */
  public ExportCalendarDialog(Dialog parent) {
    super(parent, "Export Calendar");

    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    setSize(500, 120);
    setLocationRelativeTo(null);
    setResizable(false);

    JPanel mainPanel = new JPanel(new BorderLayout());
    mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

    JPanel folderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 3, 3));

    // Label to prompt the user
    JLabel folderLabel = new JLabel("Choose folder");
    folderPanel.add(folderLabel);

    // Text field to hold the folder's absolute path (initially empty)
    folderTextField = new JTextField(20);
    folderTextField.setEditable(false);
    folderPanel.add(folderTextField);

    // Button to open the file chooser
    JButton browseButton = new JButton("Browse");
    folderPanel.add(browseButton);

    // Add an ActionListener to the "Browse" button to open the file chooser
    browseButton.addActionListener(e -> {
      JFileChooser fileChooser = new JFileChooser();
      fileChooser.setDialogTitle("Select target path");

      int response = fileChooser.showSaveDialog(null);
      if (response == JFileChooser.APPROVE_OPTION) {
        folderTextField.setText(fileChooser.getSelectedFile().getAbsolutePath());
      }
    });

    mainPanel.add(folderPanel, BorderLayout.CENTER);

    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 3, 3));
    exportButton = new JButton("Export");
    buttonPanel.add(exportButton);

    JButton cancelButton = new JButton("Cancel");
    cancelButton.addActionListener(e -> dispose());
    buttonPanel.add(cancelButton);

    mainPanel.add(buttonPanel, BorderLayout.SOUTH);
    add(mainPanel);
  }

  @Override
  public void setFeatures(Features features) {
    exportButton.addActionListener(e -> {
      String path = folderTextField.getText();

      if (features.exportCalendar(path)) {
        dispose();
      }
    });
  }
}
