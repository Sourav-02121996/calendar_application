package calendar;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.chrono.ChronoZonedDateTime;
import java.util.List;
import java.util.Set;

/**
 * A Java interface representing repeating events in a calendar. Extends the IEvent interface and
 * also includes additional operations around repeating events.
 */
public interface IRepeatingEvent extends IEvent {

  /**
   * Get the repeat end date time.
   *
   * @return the event's repeat end date time
   */
  ChronoZonedDateTime<LocalDate> getRepeatEndDateTime();

  /**
   * Get the repeat number.
   *
   * @return int value of repeat number
   */
  int getRepeatNumber();

  /**
   * Get the repeat days.
   *
   * @return set containing DayOfWeek enums representing repeat days
   */
  Set<DayOfWeek> getRepeatDays();

  /**
   * Edits the repeat rule of repeating events using the given repeat rule property.
   *
   * @param property the property of the repeat rule to be edited.
   * @return List of IEvent objects
   * @throws IllegalArgumentException if an error occurs
   */
  List<IEvent> editRepeat(String property) throws IllegalArgumentException;

  /**
   * Set the repeat number of a repeating event.
   *
   * @param value new repeat number as a string
   * @throws IllegalArgumentException if the value is invalid
   */
  void setRepeatNumber(String value) throws IllegalArgumentException;

  /**
   * Set the repeat days of a repeating event.
   *
   * @param value string containing the new repeat days
   */
  void setRepeatDays(String value);

  /**
   * Set the repeat end date time of a repeating event.
   *
   * @param value new repeat end date and time as a string
   */
  void setRepeatEndDateTime(String value);

  /**
   * Creates events on the given days until the repeat end date and time.
   *
   * @return List of IEvent objects
   * @throws IllegalArgumentException if an error occurs
   */
  List<IEvent> repeatUntilEndDate() throws IllegalArgumentException;

  /**
   * Creates events on the given days N times.
   *
   * @return List of IEvent objects
   * @throws IllegalArgumentException if an error occurs
   */
  List<IEvent> repeatNTimes() throws IllegalArgumentException;
}