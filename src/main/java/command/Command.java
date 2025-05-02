package command;

import model.IModel;
import view.Viewer;

/**
 * A Java interface representing a command executable by the calendar application.
 */
public interface Command {

  /**
   * Execute the command.
   *
   * @param model         IModel object
   * @param view          Viewer object
   * @param commandString command string
   * @return true if the command was handled, false otherwise
   * @throws Exception if an error occurs
   */
  boolean execute(IModel model, Viewer view, String commandString) throws Exception;
}