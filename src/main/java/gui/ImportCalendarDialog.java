package gui;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.FlowLayout;

import java.util.Date;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JDialog;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * A Java class representing a UI frame for importing a calendar from an existing file into the
 * application. Extends the JDialog class and implements the IDialog interface.
 */
public class ImportCalendarDialog extends JDialog implements IDialog {

  private final IViewModel viewModel;
  private final Consumer<Date> onCalendarImported;

  private final JTextField folderTextField;
  private final JButton importButton;

  /**
   * Constructs an ImportCalendarDialog object. Calls the JDialog constructor.
   *
   * @param parent    parent Dialog object
   * @param viewModel IViewModel object
   * @param callback  Consumer that accepts a Date parameter
   */
  public ImportCalendarDialog(Dialog parent, IViewModel viewModel, Consumer<Date> callback) {
    super(parent, "Import Calendar");
    this.viewModel = viewModel;
    this.onCalendarImported = callback;

    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    setSize(500, 120);
    setLocationRelativeTo(null);
    setResizable(false);

    JPanel mainPanel = new JPanel(new BorderLayout());
    mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

    JPanel folderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 3, 3));

    JLabel folderLabel = new JLabel("Choose folder");
    folderPanel.add(folderLabel);

    // Text field to hold the folder's absolute path (initially empty)
    folderTextField = new JTextField(20);
    folderTextField.setEditable(false);
    folderPanel.add(folderTextField);

    // Button to open the file chooser
    JButton browseButton = new JButton("Browse");
    browseButton.addActionListener(e -> displayImportFileChooser());
    folderPanel.add(browseButton);

    mainPanel.add(folderPanel, BorderLayout.CENTER);

    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 3, 3));
    importButton = new JButton("Import");
    buttonPanel.add(importButton);

    JButton cancelButton = new JButton("Cancel");
    cancelButton.addActionListener(e -> dispose());
    buttonPanel.add(cancelButton);

    mainPanel.add(buttonPanel, BorderLayout.SOUTH);

    add(mainPanel);
  }

  @Override
  public void setFeatures(Features features) {
    importButton.addActionListener(e -> {
      String path = folderTextField.getText();

      if (features.importCalendar(path)) {
        callback();
        dispose();
      }
    });
  }

  private void callback() {
    onCalendarImported.accept(viewModel.getSelectedDate());
  }

  private void displayImportFileChooser() {
    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setDialogTitle("Select file");
    FileFilter filter = new FileNameExtensionFilter("CSV files", "csv");
    fileChooser.setFileFilter(filter);
    fileChooser.setAcceptAllFileFilterUsed(false);

    int response = fileChooser.showOpenDialog(null);
    if (response == JFileChooser.APPROVE_OPTION) {
      folderTextField.setText(fileChooser.getSelectedFile().getAbsolutePath());
    }
  }
}
