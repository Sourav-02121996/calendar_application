package view;

import java.io.IOException;
import java.util.List;

import calendar.IEvent;
import gui.Features;

/**
 * A Java interface for a user interface view.
 */
public interface Viewer {

  /**
   * Prints the given string.
   *
   * @param message message string
   */
  void print(String message);

  /**
   * Add features to the view.
   *
   * @param features Features object
   */
  void addFeatures(Features features);

  /**
   * Prints the given error message.
   *
   * @param message error message string
   */
  void printError(String message);

  /**
   * Prints the list of IEvents.
   *
   * @param events List of IEvents
   * @throws IOException if an IO exception occurs
   */
  void printEvents(List<IEvent> events) throws IOException;
}
