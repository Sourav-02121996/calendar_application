package gui;

import java.util.Date;
import java.util.List;

import calendar.ICalendar;
import calendar.IEvent;

/**
 * A Java interface containing read-only operations needed by a UI to display data from the Model.
 */
public interface IViewModel {

  /**
   * Get all existing calendars.
   *
   * @return List of ICalendar objects
   */
  List<ICalendar> getAllCalendars();

  /**
   * Get the current calendar.
   *
   * @return ICalendar object
   */
  ICalendar getCurrentCalendar();

  /**
   * Get all events that occur on the given date.
   *
   * @param dateTime Date object
   * @return List of IEvents
   */
  List<IEvent> getEventsOnDate(Date dateTime);

  /**
   * Sets the event that's currently selected in the GUI.
   *
   * @param event IEvent object
   */
  void setSelectedEvent(IEvent event);

  /**
   * Get the currently selected event.
   *
   * @return IEvent object
   */
  IEvent getSelectedEvent();

  /**
   * Sets the currently selected date in the GUI.
   *
   * @param date Date object
   */
  void setSelectedDate(Date date);

  /**
   * Get the currently selected date.
   *
   * @return Date object
   */
  Date getSelectedDate();

  /**
   * Get the current date as a Date object.
   *
   * @return current date
   */
  Date getCurrentDate();

  /**
   * Get all editable properties of an event.
   *
   * @return array of property names
   */
  String[] getEditableEventProperties();

  /**
   * Get all editable properties when editing multiple events.
   *
   * @return array of property names
   */
  String[] getEditableMultipleEventProperties();

  /**
   * Get all editable properties of a calendar.
   *
   * @return array of property names
   */
  String[] getEditableCalendarProperties();

  /**
   * Gets all timezones that can be used.
   *
   * @return array of timezone names
   */
  String[] getAvailableTimezones();
}
