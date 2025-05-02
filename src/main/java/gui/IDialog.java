package gui;

/**
 * A Java interface containing operations around UI dialog windows.
 */
public interface IDialog {

  /**
   * Toggles the dialogs visibility.
   *
   * @param visible boolean value
   */
  void setVisible(boolean visible);

  /**
   * Adds features to a UI dialog.
   *
   * @param features Features object
   */
  void setFeatures(Features features);
}
