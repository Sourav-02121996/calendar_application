package gui;

import java.util.Map;

/**
 * A Java interface specifying the operations of the calendar GUI.
 */
public interface Features {

  /**
   * Add a new calendar.
   *
   * @param name     name string
   * @param timezone timezone as a string
   * @return true if successful, false otherwise
   */
  boolean addCalendar(String name, String timezone);

  /**
   * Edit a calendar property.
   *
   * @param name     calendar name
   * @param property property name
   * @param newValue new value as a string
   * @return true if successful, false otherwise
   */
  boolean editCalendar(String name, String property, String newValue);

  /**
   * Change the currently selected calendar.
   *
   * @param name new calendar name
   * @return true if successful, false otherwise
   */
  boolean useCalendar(String name);

  /**
   * Add a new event.
   *
   * @param inputs map containing input fields
   * @return true if successful, false otherwise
   */
  boolean addEvent(Map<String, String> inputs);

  /**
   * Edit a property of an event.
   *
   * @param inputs map containing input fields
   * @return true if successful, false otherwise
   */
  boolean editEvent(Map<String, String> inputs);

  /**
   * Edit a property of multiple events.
   *
   * @param inputs map containing input fields
   * @return true if successful, false otherwise
   */
  boolean editEvents(Map<String, String> inputs);

  /**
   * Export events to a file.
   *
   * @param path export filepath as a string
   * @return true if successful, false otherwise
   */
  boolean exportCalendar(String path);

  /**
   * Import events from a file.
   *
   * @param path import filepath as a string
   * @return true if successful, false otherwise
   */
  boolean importCalendar(String path);
}
