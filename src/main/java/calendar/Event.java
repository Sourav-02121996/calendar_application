package calendar;

import java.time.LocalDate;
import java.time.chrono.ChronoZonedDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

import utils.BooleanUtils;
import utils.TimeUtils;

/**
 * A Java class representing a calendar event. Implements the IEvent interface.
 */
public class Event implements IEvent {

  private static final Map<String, BiConsumer<IEvent, String>> SETTERS = new HashMap<>();

  protected String subject;
  protected ChronoZonedDateTime<LocalDate> startDateTime;
  protected ChronoZonedDateTime<LocalDate> endDateTime;
  protected boolean isAllDay;

  // optional fields
  protected String description;
  protected String location;
  protected boolean isPrivate;

  static {
    SETTERS.put("location", IEvent::setLocation);
    SETTERS.put("description", IEvent::setDescription);
    SETTERS.put("subject", IEvent::setSubject);
    SETTERS.put("startDateTime", IEvent::setStartDateTime);
    SETTERS.put("endDateTime", IEvent::setEndDateTime);
    SETTERS.put("private", (event, value) ->
        event.setIsPrivate(BooleanUtils.parseBoolean(value)));
  }

  /**
   * Get all editable properties.
   *
   * @return Set of property names
   */
  public static Set<String> getEditableProperties() {
    return new HashSet<>(SETTERS.keySet());
  }

  /**
   * Get properties that should be excluded when editing multiple events.
   *
   * @return Set of property names
   */
  public static Set<String> getExcludedProperties() {
    return new HashSet<>(Arrays.asList("startDateTime", "endDateTime"));
  }

  /**
   * Constructs an Event object.
   *
   * @param subject       subject name
   * @param startDateTime start date and time
   * @param endDateTime   end date and time
   * @param description   description (optional)
   * @param location      location (optional)
   * @param isPrivate     private boolean flag (optional)
   * @param isAllDay      all day event boolean flag
   * @throws IllegalArgumentException if any of the given parameters are invalid
   */
  protected Event(String subject, ChronoZonedDateTime<LocalDate> startDateTime,
      ChronoZonedDateTime<LocalDate> endDateTime, String description,
      String location, boolean isPrivate, boolean isAllDay) {
    if (subject == null || subject.trim().isEmpty()) {
      throw new IllegalArgumentException("Subject cannot be empty");
    }
    if (startDateTime == null) {
      throw new IllegalArgumentException("StartDateTime cannot be null");
    }
    if (endDateTime == null) {
      throw new IllegalArgumentException("EndDateTime cannot be null");
    }
    if (!endDateTime.isAfter(startDateTime)) {
      throw new IllegalArgumentException("EndDateTime must be after startDateTime");
    }

    this.subject = subject;
    this.startDateTime = startDateTime;
    this.endDateTime = endDateTime;
    this.isAllDay = isAllDay;
    this.description = description;
    this.location = location;
    this.isPrivate = isPrivate;
  }

  @Override
  public String getSubject() {
    return subject;
  }

  @Override
  public ChronoZonedDateTime<LocalDate> getStartDateTime() {
    return startDateTime;
  }

  @Override
  public ChronoZonedDateTime<LocalDate> getEndDateTime() {
    return endDateTime;
  }

  @Override
  public boolean isAllDay() {
    return isAllDay;
  }

  @Override
  public String getDescription() {
    return description;
  }

  @Override
  public String getLocation() {
    return location;
  }

  @Override
  public boolean isPrivate() {
    return isPrivate;
  }

  /**
   * Standalone events do not store references to previous events. Returns null.
   *
   * @return null
   */
  @Override
  public IEvent getPrevious() {
    return null;
  }

  /**
   * Calls the appropriate setter method based on the property name.
   *
   * @param property property name
   * @param newValue new value
   * @throws IllegalArgumentException if the given property name or value is invalid
   */
  @Override
  public void setNewProperty(String property, String newValue) throws IllegalArgumentException {
    BiConsumer<IEvent, String> setter = SETTERS.get(property);
    if (setter != null) {
      setter.accept(this, newValue);
    } else {
      throw new IllegalArgumentException("Cannot change '" + property + "' for a single event");
    }
  }

  @Override
  public void setSubject(String subject) throws IllegalArgumentException {
    if (subject == null || subject.trim().isEmpty()) {
      throw new IllegalArgumentException("Subject cannot be null or empty");
    }
    this.subject = subject;
  }

  /**
   * Set the event's start date time and updates the isAllDay flag.
   *
   * @param value new start date time as a string
   * @throws IllegalArgumentException if the given startDateTime is null or invalid
   */
  @Override
  public void setStartDateTime(String value) throws IllegalArgumentException {
    if (value == null || value.isEmpty()) {
      throw new IllegalArgumentException("StartDateTime cannot be null or empty");
    }
    ZoneId zone = this.startDateTime.getZone();
    ChronoZonedDateTime<LocalDate> startDateTime = TimeUtils.parseDateTimeString(value, zone);

    if (!startDateTime.isBefore(endDateTime)) {
      throw new IllegalArgumentException("StartDateTime must be before endDateTime");
    }

    this.startDateTime = startDateTime;
    this.isAllDay = TimeUtils.isMidnight(this.startDateTime, zone)
        && TimeUtils.isMidnight(endDateTime, zone);
  }

  /**
   * Set the event's end date time and updates the isAllDay flag.
   *
   * @param value new end date time as a string
   * @throws IllegalArgumentException if the given endDateTime is null or invalid
   */
  @Override
  public void setEndDateTime(String value) throws IllegalArgumentException {
    if (value == null || value.isEmpty()) {
      throw new IllegalArgumentException("EndDateTime cannot be null or empty");
    }
    ZoneId zone = this.endDateTime.getZone();
    ChronoZonedDateTime<LocalDate> endDateTime = TimeUtils.parseDateTimeString(value, zone);

    if (!endDateTime.isAfter(startDateTime)) {
      throw new IllegalArgumentException("EndDateTime must be after startDateTime");
    }

    this.endDateTime = endDateTime;
    this.isAllDay = TimeUtils.isMidnight(startDateTime, zone)
        && TimeUtils.isMidnight(this.endDateTime, zone);
  }

  /**
   * Set the event's description. Sets it to an empty string if the given description is null.
   *
   * @param description new description
   */
  @Override
  public void setDescription(String description) {
    this.description = description == null ? "" : description;
  }

  /**
   * Set the event's location. Sets it to an empty string if the given location is null.
   *
   * @param location new description
   */
  @Override
  public void setLocation(String location) {
    this.location = location == null ? "" : location;
  }

  @Override
  public void setIsPrivate(boolean isPrivate) {
    this.isPrivate = isPrivate;
  }

  @Override
  public boolean matches(String subject, ChronoZonedDateTime<LocalDate> startDateTime) {
    return this.subject.equals(subject) && this.startDateTime.equals(startDateTime);
  }

  /**
   * Checks if this event overlaps with the given time window. An overlap occurs if this event
   * finishes in the middle of the window, or starts before the window ends.
   *
   * @param startDateTime ChronoZonedDateTime object representing the start of the window
   * @param endDateTime   ChronoZonedDateTime object representing the end of the window
   * @return true if there is an overlap, false otherwise
   */
  @Override
  public boolean isOverlap(ChronoZonedDateTime<LocalDate> startDateTime,
      ChronoZonedDateTime<LocalDate> endDateTime) {
    return !this.endDateTime.isBefore(startDateTime) && !this.startDateTime.isAfter(endDateTime);
  }

  /**
   * Checks if the given date and time clashes with this event. If this event starts before and ends
   * after the time, there is a clash.
   *
   * @param dateTime ChronoZonedDateTime object
   * @return true if there is a clash, false otherwise
   */
  @Override
  public boolean clashesWith(ChronoZonedDateTime<LocalDate> dateTime) {
    return !this.startDateTime.isAfter(dateTime) && this.endDateTime.isAfter(dateTime);
  }

  @Override
  public boolean isOnSameDay(ChronoZonedDateTime<LocalDate> date) {
    return this.endDateTime.toLocalDate().isEqual(date.toLocalDate())
        || this.startDateTime.toLocalDate().isEqual(date.toLocalDate())
        || this.startDateTime.isBefore(date) && this.endDateTime.isAfter(date);
  }

  @Override
  public IEvent copy(long timeDifference, ZoneId timezone) {

    ChronoZonedDateTime<LocalDate> adjustedStartDateTime = startDateTime.withZoneSameLocal(timezone)
        .plus(timeDifference, ChronoUnit.MINUTES);
    ChronoZonedDateTime<LocalDate> adjustedEndDateTime = endDateTime.withZoneSameLocal(timezone)
        .plus(timeDifference, ChronoUnit.MINUTES);

    return new EventBuilder()
        .subject(this.subject)
        .startDateTime(adjustedStartDateTime)
        .endDateTime(adjustedEndDateTime)
        .description(this.description)
        .isPrivate(this.isPrivate)
        .isAllDay(this.isAllDay)
        .location(this.location)
        .build();
  }

  @Override
  public boolean conflictsWith(IEvent other) {
    if (this.isAllDay && other.isAllDay()) {
      LocalDate thisDate = this.startDateTime.toLocalDate();
      LocalDate otherDate = other.getStartDateTime().toLocalDate();
      return thisDate.equals(otherDate);
    }

    if (this.isAllDay) {
      LocalDate thisDate = this.startDateTime.toLocalDate();
      LocalDate otherStartDate = other.getStartDateTime().toLocalDate();
      LocalDate otherEndDate = other.getEndDateTime().toLocalDate();
      return !thisDate.isBefore(otherStartDate) && !thisDate.isAfter(otherEndDate);
    }

    if (other.isAllDay()) {
      LocalDate thisStartDate = this.startDateTime.toLocalDate();
      LocalDate thisEndDate = this.endDateTime.toLocalDate();
      LocalDate otherDate = other.getStartDateTime().toLocalDate();
      return !otherDate.isBefore(thisStartDate) && !otherDate.isAfter(thisEndDate);
    }

    return this.startDateTime.isBefore(other.getEndDateTime())
        && this.endDateTime.isAfter(other.getStartDateTime());
  }

  @Override
  public void updateTimezone(ZoneId timezone) {
    this.startDateTime = startDateTime.withZoneSameInstant(timezone);
    this.endDateTime = endDateTime.withZoneSameInstant(timezone);
  }

  /**
   * Compares this event with another event based on their start and end times.
   *
   * @param other the event to compare with this event
   * @return      a negative integer if this event starts before the other event or if start
   *              times are equal and this event ends before the other, zero if both events have
   *              identical start and end times, a positive integer if this event starts after
   *              the other event or if start times are equal and this event ends after the other
   */
  @Override
  public int compareTo(IEvent other) {
    int compareStartTimes = this.startDateTime.compareTo(other.getStartDateTime());
    if (compareStartTimes != 0) {
      return compareStartTimes;
    }

    // if start times are equal, compare end times
    return this.endDateTime.compareTo(other.getEndDateTime());
  }

  /**
   * Returns a string containing all the fields of the event.
   *
   * @return string containing all fields and values
   */
  @Override
  public String toString() {
    String format = "subject: %s, startDateTime: %s, endDateTime: %s, description: %s, "
        + "location: %s, isAllDay: %s, isPrivate: %s";
    return String.format(format, subject, startDateTime, endDateTime, description,
        location, isAllDay, isPrivate);
  }

  /**
   * Inner Java class representing a builder for the Event class. Extends the AbstractEventBuilder
   * class.
   */
  public static class EventBuilder extends AbstractEventBuilder<EventBuilder> {

    /**
     * Constructs an EventBuilder using default values for optional fields.
     */
    public EventBuilder() {
      super();
    }

    @Override
    protected EventBuilder getBuilder() {
      return this;
    }

    /**
     * Builds an Event object with the values set in the EventBuilder.
     *
     * @return Event object containing the values
     */
    public Event build() {
      return new Event(subject, startDateTime, endDateTime, description,
          location, isPrivate, isAllDay);
    }
  }
}
