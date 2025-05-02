package calendar;

import java.time.LocalDate;
import java.time.chrono.ChronoZonedDateTime;

/**
 * An abstract builder class for constructing event objects with optional fields.
 *
 * @param <T> the type of the concrete builder extending this class
 */
public abstract class AbstractEventBuilder<T> {

  protected String subject;
  protected ChronoZonedDateTime<LocalDate> startDateTime;
  protected ChronoZonedDateTime<LocalDate> endDateTime;
  protected boolean isAllDay;

  // Optional fields
  protected String description;
  protected String location;
  protected boolean isPrivate;

  /**
   * Constructs an AbstractEventBuilder object.
   */
  protected AbstractEventBuilder() {
    this.isAllDay = false;
    this.description = "";
    this.location = "";
    this.isPrivate = false;
  }

  /**
   * Set the subject.
   *
   * @param subject subject string
   * @return builder of type T
   */
  public T subject(String subject) {
    this.subject = subject;
    return getBuilder();
  }

  /**
   * Set the startDateTime.
   *
   * @param startDateTime ChronoZonedDateTime object
   * @return builder of type T
   */
  public T startDateTime(ChronoZonedDateTime<LocalDate> startDateTime) {
    this.startDateTime = startDateTime;
    return getBuilder();
  }

  /**
   * Set the endDateTime.
   *
   * @param endDateTime ChronoZonedDateTime object
   * @return builder of type T
   */
  public T endDateTime(ChronoZonedDateTime<LocalDate> endDateTime) {
    this.endDateTime = endDateTime;
    return getBuilder();
  }

  /**
   * Set the description.
   *
   * @param description description string
   * @return builder of type T
   */
  public T description(String description) {
    this.description = description == null ? "" : description;
    return getBuilder();
  }

  /**
   * Set the location.
   *
   * @param location location string
   * @return builder of type T
   */
  public T location(String location) {
    this.location = location == null ? "" : location;
    return getBuilder();
  }

  /**
   * Set the isPrivate flag.
   *
   * @param isPrivate boolean flag
   * @return builder of type T
   */
  public T isPrivate(boolean isPrivate) {
    this.isPrivate = isPrivate;
    return getBuilder();
  }

  /**
   * Set the isAllDay flag.
   *
   * @param isAllDay boolean flag.
   * @return builder of type T
   */
  public T isAllDay(boolean isAllDay) {
    this.isAllDay = isAllDay;
    return getBuilder();
  }

  /**
   * Returns a builder object of type T.
   *
   * @return builder object
   */
  protected abstract T getBuilder();
}
