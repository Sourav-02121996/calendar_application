package model;

import java.time.LocalDate;
import java.time.chrono.ChronoZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;

import calendar.ICalendar;
import calendar.IEvent;

/**
 * A Java interface representing the operations carried out by a Model in the calendar application.
 * The model is responsible for event operations and managing multiple calendars in the
 * application.
 */
public interface IModel {

  /**
   * Get the calendar that is currently in use.
   *
   * @return ICalendar object
   */
  ICalendar getCurrentCalendar();

  /**
   * Get all existing calendars.
   *
   * @return List of ICalendar objects
   */
  List<ICalendar> getAllCalendars();

  /**
   * Creates and adds a new calendar. Stores the calendar in the calendar map along with a new
   * linked list for its events.
   *
   * @param calendar ICalendar object
   * @throws IllegalArgumentException if the calendar already exists
   */
  void addCalendar(ICalendar calendar) throws IllegalArgumentException;

  /**
   * Edits an existing calendar.
   *
   * @param name     string value of the calendar's name
   * @param property property name
   * @param newValue new value as a string
   * @throws IllegalArgumentException if the given property is invalid.
   */
  void editCalendar(String name, String property, String newValue)
      throws IllegalArgumentException;

  /**
   * Change the current calendar to another existing calendar.
   *
   * @param name name of the new calendar
   * @throws IllegalArgumentException if the name doesn't match with an existing calendar.
   */
  void useCalendar(String name) throws IllegalArgumentException;

  /**
   * Add events to the current calendar.
   *
   * @param events List of IEvent objects
   * @throws IllegalStateException if there is no current calendar
   * @throws ConflictException     if there is a conflict
   */
  void addEvents(List<IEvent> events) throws IllegalStateException, ConflictException;

  /**
   * Get all events that occur on the given day.
   *
   * @param date ChronoZonedDateTime object representing a day
   * @return List of IEvent objects
   * @throws IllegalArgumentException if an error occurs or if no results are found
   */
  List<IEvent> getEventsOnDate(ChronoZonedDateTime<LocalDate> date)
      throws IllegalArgumentException;

  /**
   * Get all events that occur in the given time range.
   *
   * @param startDateTime start of time range
   * @param endDateTime   end of time range
   * @return list of matching events
   * @throws IllegalArgumentException if the given dates are invalid
   */
  List<IEvent> getEventsInRange(ChronoZonedDateTime<LocalDate> startDateTime,
      ChronoZonedDateTime<LocalDate> endDateTime)
      throws IllegalArgumentException;

  /**
   * Get the status on a given date and time.
   *
   * @param dateTime date and time as a string
   * @return string value of status
   */
  String getStatus(String dateTime);

  /**
   * Edits events in the calendar from the given start date that have the given subject name.
   *
   * @param property property name that needs to be changed
   * @param subject  event subject name
   * @param start    start date and time as a string
   * @param newValue new value for the property being updated as a string
   * @throws IllegalArgumentException if an argument is invalid
   * @throws ConflictException        if a conflict occurs
   */
  void editEventsFromStartDateTime(String property, String subject, String start, String newValue)
      throws ConflictException, IllegalArgumentException;

  /**
   * Edit a single event in the calendar.
   *
   * @param property field to be updated
   * @param subject  subject name
   * @param start    event start
   * @param end      event end
   * @param newValue new value of field
   * @throws IllegalArgumentException if an argument is invalid
   * @throws ConflictException        if a conflict occurs
   */
  void editSingleEvent(String property, String subject, String start, String end, String newValue)
      throws IllegalArgumentException, ConflictException;

  /**
   * Edit all events that have the same subject.
   *
   * @param property field to be updated
   * @param subject  event subject
   * @param newValue new value as a string
   * @throws ConflictException        if a conflict occurs
   * @throws IllegalArgumentException if an argument is invalid
   */
  void editEventsBySubject(String property, String subject, String newValue)
      throws ConflictException, IllegalArgumentException;

  /**
   * Copy an event with the given subject and start date and time to the target calendar at the
   * given start date and time.
   *
   * @param eventName        Event subject name
   * @param targetCalendar   target calendar name
   * @param start            start date and time as a string
   * @param newCalendarStart new start date and time as a string
   * @throws IllegalArgumentException if an argument is invalid
   * @throws DateTimeParseException   if an error occurs when parsing the time
   */
  void copyEvent(String eventName, String targetCalendar, String start, String newCalendarStart)
      throws IllegalArgumentException, DateTimeParseException;

  /**
   * Copy all events from the current calendar to the target calendar at a new time that occur in
   * the given time range.
   *
   * @param startDate            start date and time of the range as a string
   * @param endDate              end date and time of the range as string
   * @param calendarName         target calendar name
   * @param newCalendarStartDate new start date and time as a string
   * @throws IllegalArgumentException if an argument is invalid
   * @throws ConflictException        if a conflict occurs
   */
  void copyEventsInRange(String startDate, String endDate, String calendarName,
      String newCalendarStartDate) throws IllegalArgumentException, ConflictException;

  /**
   * Copy all events that occur on the given date to the target calendar at the new start date.
   *
   * @param start            date and time as a string
   * @param newCalendarStart date and time as a string
   * @param target           target calendar name
   * @throws IllegalArgumentException if an argument is invalid
   * @throws ConflictException        if a conflict occurs
   */
  void copyEventsOnDate(String start, String newCalendarStart, String target)
      throws IllegalArgumentException, ConflictException;
}
