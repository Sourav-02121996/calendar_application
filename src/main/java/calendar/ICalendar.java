package calendar;

import java.time.ZoneId;
import java.util.List;

import model.ConflictException;

/**
 * A Java interface representing operations related to a calendar in the application.
 */
public interface ICalendar {

  /**
   * Get the calendar name.
   *
   * @return calendar name
   */
  String getName();

  /**
   * Get the calendar timezone.
   *
   * @return calendar timezone as a ZoneId
   */
  ZoneId getTimezone();

  /**
   * Set the calendar's name.
   *
   * @param name string value of name
   * @throws IllegalArgumentException if the new value is invalid
   */
  void setName(String name) throws IllegalArgumentException;

  /**
   * Set the calendar's timezone.
   *
   * @param timezone timezone name as a string
   * @throws IllegalArgumentException if the new value is invalid
   */
  void setTimezone(String timezone) throws IllegalArgumentException;

  /**
   * Edit  property of a calendar.
   *
   * @param property property name
   * @param newValue new value as a string
   * @throws IllegalArgumentException if any of the arguments are invalid
   */
  void edit(String property, String newValue) throws IllegalArgumentException;

  /**
   * Add events to the calendar.
   *
   * @param events list of IEvent objects
   * @throws ConflictException if there is a conflict with an existing event
   */
  void addEvents(List<IEvent> events) throws ConflictException;

  /**
   * Get all events that are part of the calendar.
   *
   * @return List of IEvent objects
   */
  List<IEvent> getEvents();

  /**
   * Edit a single event in the calendar.
   *
   * @param property field to be updated
   * @param subject  subject name
   * @param start    event start date and time as a string
   * @param end      event end date and time as a string
   * @param newValue new value of field
   * @throws IllegalArgumentException if an exception occurs
   * @throws ConflictException        if a conflict occurs
   */
  void editSingleEvent(String property, String subject, String start, String end, String newValue)
      throws IllegalArgumentException, ConflictException;

  /**
   * Edits events in the calendar from the given start date that have the given subject name.
   *
   * @param property property name that needs to be changed
   * @param subject  event subject name
   * @param start    string value of start date and time
   * @param newValue new value for the property being updated as a string
   * @throws ConflictException        if a conflict occurs
   * @throws IllegalArgumentException if an exception occurs
   */
  void editEventsFromStartDateTime(String property, String subject, String start, String newValue)
      throws ConflictException, IllegalArgumentException;

  /**
   * Edit all events that have the same subject.
   *
   * @param property field to be updated
   * @param subject  event subject
   * @param newValue new value as a string
   * @throws ConflictException        if a conflict occurs
   * @throws IllegalArgumentException if no events are found
   */
  void editEventsBySubject(String property, String subject, String newValue)
      throws ConflictException, IllegalArgumentException;
}
