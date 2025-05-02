package calendar;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.chrono.ChronoZonedDateTime;

/**
 * A Java interface representing an event in a calendar and operations involving getting its values,
 * setting values, and checking for conflicts.
 */
public interface IEvent extends Comparable<IEvent> {

  /**
   * Get the event's subject name.
   *
   * @return subject string
   */
  String getSubject();

  /**
   * Get the event's start date time.
   *
   * @return start date time
   */
  ChronoZonedDateTime<LocalDate> getStartDateTime();

  /**
   * Get the event's end date time.
   *
   * @return end date time
   */
  ChronoZonedDateTime<LocalDate> getEndDateTime();

  /**
   * Check if the event is an all day event.
   *
   * @return true if all day, false otherwise
   */
  boolean isAllDay();

  /**
   * Get the event's description.
   *
   * @return description string
   */
  String getDescription();

  /**
   * Get the event's location.
   *
   * @return location string
   */
  String getLocation();

  /**
   * Check if the event is a private event.
   *
   * @return true if private, false otherwise
   */
  boolean isPrivate();

  /**
   * Returns the previous event associated to an event.
   *
   * @return IEvent object
   */
  IEvent getPrevious();

  /**
   * Set the property of an event to a new value.
   *
   * @param property property name
   * @param newValue new value
   * @throws IllegalArgumentException if the given property name or value is invalid
   */
  void setNewProperty(String property, String newValue) throws IllegalArgumentException;

  /**
   * Sets the event's subject.
   *
   * @param subject string value of new subject
   * @throws IllegalArgumentException if the new value is invalid
   */
  void setSubject(String subject) throws IllegalArgumentException;

  /**
   * Set the event's start date time.
   *
   * @param value new start date time as a string
   * @throws IllegalArgumentException if the given startDateTime is null or invalid
   */
  void setStartDateTime(String value) throws IllegalArgumentException;

  /**
   * Set the event's end date time.
   *
   * @param value new end date time as a string
   * @throws IllegalArgumentException if the given endDateTime is null or invalid
   */
  void setEndDateTime(String value) throws IllegalArgumentException;

  /**
   * Set the event's description.
   *
   * @param description string value of new description
   */
  void setDescription(String description);

  /**
   * Set the event's location.
   *
   * @param location string value of new location
   */
  void setLocation(String location);

  /**
   * Set the event's private flag.
   *
   * @param isPrivate new flag value
   */
  void setIsPrivate(boolean isPrivate);

  /**
   * Checks if this event has the given subject and startDateTime.
   *
   * @param subject       string value of subject
   * @param startDateTime ChronoZonedDateTime object
   * @return true if all fields match, false otherwise
   */
  boolean matches(String subject, ChronoZonedDateTime<LocalDate> startDateTime);

  /**
   * Checks if this event overlaps with the given time window.
   *
   * @param startDateTime ChronoZonedDateTime object representing the start of the window
   * @param endDateTime   ChronoZonedDateTime object representing the end of the window
   * @return true if there is an overlap, false otherwise
   */
  boolean isOverlap(ChronoZonedDateTime<LocalDate> startDateTime,
      ChronoZonedDateTime<LocalDate> endDateTime);

  /**
   * Checks if the given date and time clashes with this event. If this event starts before and ends
   * after the time, there is a clash.
   *
   * @param dateTime ChronoZonedDateTime object
   * @return true if there is a clash, false otherwise
   */
  boolean clashesWith(ChronoZonedDateTime<LocalDate> dateTime);

  /**
   * Check if an event starts or ends on the same day as the given date.
   *
   * @param date ChronoZonedDateTime object representing a date
   * @return true if the event is on the same date, false otherwise
   */
  boolean isOnSameDay(ChronoZonedDateTime<LocalDate> date);

  /**
   * Copies an event and updates its start date time using the given timezone and difference in
   * minutes.
   *
   * @param difference long representing the difference in minutes that the copy needs to be
   *                   adjusted by
   * @param timeZone   ZoneId object representing the new timezone
   * @return copy of the event
   */
  IEvent copy(long difference, ZoneId timeZone);

  /**
   * Checks if this event conflicts with the given event.
   *
   * @param other IEvent to compare this event with
   * @return true if there is a conflict, false otherwise
   */
  boolean conflictsWith(IEvent other);

  /**
   * Updates the time zone of this event, adjusting its start and end times accordingly.
   *
   * @param timezone the new time zone to apply to this event.
   */
  void updateTimezone(ZoneId timezone);

  /**
   * Determines whether this event is a repeating event.
   *
   * @return true if this is a repeating event, false otherwise.
   */
  default boolean isRepeating() {
    return false;
  }
}
