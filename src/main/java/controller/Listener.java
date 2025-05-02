package controller;

import java.io.IOException;

import view.Viewer;

/**
 * A Java interface representing a controller.
 */
public interface Listener {

  /**
   * Listens for commands from an input source.
   */
  void listen() throws IOException;

  /**
   * Handles and processes a command string.
   *
   * @param command command string
   * @throws Exception if an error occurs
   */
  void handleCommand(String command) throws Exception;

  /**
   * Sets the Viewer to be used by the Listener.
   *
   * @param view Viewer object
   */
  void setView(Viewer view);
}
