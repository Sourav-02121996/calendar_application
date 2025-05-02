package calendar;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.chrono.ChronoZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import utils.TimeUtils;

/**
 * A Java class representing a base event for a repeating event series. Extends the Event class.
 */
public class RepeatingEvent extends Event implements IRepeatingEvent {

  private static final Map<String, BiConsumer<IRepeatingEvent, String>> SETTERS = new HashMap<>();

  private int repeatNumber;
  private Set<DayOfWeek> repeatDays;
  private ChronoZonedDateTime<LocalDate> repeatEndDateTime;
  private final IEvent previous;

  static {
    SETTERS.put("repeatNumber", IRepeatingEvent::setRepeatNumber);
    SETTERS.put("repeatDays", IRepeatingEvent::setRepeatDays);
    SETTERS.put("repeatEndDateTime", IRepeatingEvent::setRepeatEndDateTime);
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
   * Constructs a RepeatingEvent object.
   *
   * @param subject           subject name
   * @param startDateTime     start date and time
   * @param endDateTime       end date and time
   * @param description       description (optional)
   * @param location          location (optional)
   * @param isPrivate         private boolean flag (optional)
   * @param repeatNumber      repeat number
   * @param days              repeat days
   * @param repeatEndDateTime repeat end date and time
   * @param isAllDay          all day event boolean flag
   * @param previous          previous repeating event in series
   * @throws IllegalArgumentException if any of the given parameters are invalid
   */
  private RepeatingEvent(String subject, ChronoZonedDateTime<LocalDate> startDateTime,
      ChronoZonedDateTime<LocalDate> endDateTime, String description, String location,
      boolean isPrivate, int repeatNumber, Set<DayOfWeek> days,
      ChronoZonedDateTime<LocalDate> repeatEndDateTime, boolean isAllDay, IEvent previous)
      throws IllegalArgumentException {
    // call the Event constructor
    super(subject, startDateTime, endDateTime, description, location, isPrivate, isAllDay);

    if (repeatEndDateTime.equals(TimeUtils.getMaximumTime()) && repeatNumber <= 0) {
      throw new IllegalArgumentException("Repeat number must be greater than 0");
    }
    if (!isAllDay && !endDateTime.toLocalDate().isEqual(startDateTime.toLocalDate())) {
      throw new IllegalArgumentException("Repeating events can only span one day");
    }
    if (!repeatEndDateTime.equals(TimeUtils.getMaximumTime())
        && repeatEndDateTime.toLocalDate().isBefore(startDateTime.toLocalDate())) {
      throw new IllegalArgumentException("Repeat end date must be after the start date");
    }

    this.repeatNumber = repeatNumber;
    this.repeatEndDateTime = repeatEndDateTime;
    this.repeatDays = days;
    this.previous = previous;
  }

  /**
   * Get the previous repeating event in the series. If this is the first event it returns null.
   *
   * @return previous IEvent object or null
   */
  @Override
  public IEvent getPrevious() {
    return previous;
  }

  /**
   * Get the repeat end date time, if it exists.
   *
   * @return the event's repeat end date time, or null
   */
  @Override
  public ChronoZonedDateTime<LocalDate> getRepeatEndDateTime() {
    return repeatEndDateTime;
  }

  @Override
  public int getRepeatNumber() {
    return repeatNumber;
  }

  @Override
  public Set<DayOfWeek> getRepeatDays() {
    return repeatDays;
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
    BiConsumer<IRepeatingEvent, String> setter = SETTERS.get(property);
    if (setter != null) {
      setter.accept(this, newValue);
    } else {
      super.setNewProperty(property, newValue);
    }
  }

  @Override
  public void setRepeatNumber(String value) throws IllegalArgumentException {
    int number = Integer.parseInt(value);
    if (number <= 0) {
      throw new IllegalArgumentException("Repeat number must be a positive integer");
    }
    this.repeatNumber = number;
  }

  @Override
  public void setRepeatDays(String value) {
    this.repeatDays = TimeUtils.getDaysOfWeek(value.toCharArray());
  }

  @Override
  public void setRepeatEndDateTime(String value) {
    ZoneId zone = this.repeatEndDateTime.getZone();
    this.repeatEndDateTime = TimeUtils.parseDateTimeString(value, zone);
  }

  @Override
  public List<IEvent> editRepeat(String property) throws IllegalArgumentException {
    switch (property) {
      case "repeatDays":
        if (repeatNumber != 0) {
          return this.repeatNTimes();
        } else {
          return this.repeatUntilEndDate();
        }
      case "repeatEndDateTime":
        return this.repeatUntilEndDate();
      case "repeatNumber":
        return this.repeatNTimes();
      default:
        throw new IllegalStateException("Illegal state");
    }
  }

  @Override
  public List<IEvent> repeatUntilEndDate() throws IllegalArgumentException {
    List<IEvent> repeatingEvents = new ArrayList<>();

    // get repeat end date
    LocalDate repeatEndDate = this.repeatEndDateTime.toLocalDate();

    // need to add an extra day to the repeatEndDate to include the end date in the loop
    LocalDate startDate = this.startDateTime.toLocalDate();
    List<LocalDate> dates = startDate
        .datesUntil(repeatEndDate.plusDays(1))
        .collect(Collectors.toList());

    IRepeatingEvent previous = this;
    for (int i = 0; i < dates.size(); i++) {
      LocalDate date = dates.get(i);
      if (this.repeatDays.contains(date.getDayOfWeek())) {
        ChronoZonedDateTime<LocalDate> newStartDateTime = this.startDateTime.plus(i,
            ChronoUnit.DAYS);
        ChronoZonedDateTime<LocalDate> newEndDateTime = this.endDateTime.plus(i, ChronoUnit.DAYS);

        IRepeatingEvent repeat = new RepeatingEventBuilder()
            .subject(this.getSubject())
            .startDateTime(newStartDateTime)
            .endDateTime(newEndDateTime)
            .description(this.description)
            .location(this.location)
            .isPrivate(this.isPrivate)
            .isAllDay(this.isAllDay)
            .previous(previous)
            .repeatEndDateTime(this.repeatEndDateTime)
            .repeatDays(this.repeatDays)
            .build();

        repeatingEvents.add(repeat);
        previous = repeat;
      }
    }
    return repeatingEvents;
  }

  /**
   * Creates events on the given days N times. The base event is included so this returns N+1
   * events.
   *
   * @return List of IEvent objects containing N+1 events
   * @throws IllegalArgumentException if the given startDateTime doesn't match the given repeat
   *                                  days, or if endDateTime is not after the startDateTime
   */
  @Override
  public List<IEvent> repeatNTimes() throws IllegalArgumentException {
    List<IEvent> repeatingEvents = new ArrayList<>();

    IRepeatingEvent previous = this;
    int index = 0;
    while (repeatingEvents.size() <= repeatNumber) {
      ChronoZonedDateTime<LocalDate> newStartDateTime = this.startDateTime.plus(index,
          ChronoUnit.DAYS);
      ChronoZonedDateTime<LocalDate> newEndDateTime = this.endDateTime.plus(index, ChronoUnit.DAYS);

      if (this.repeatDays.contains(newStartDateTime.toLocalDate().getDayOfWeek())) {
        IRepeatingEvent repeat = new RepeatingEventBuilder()
            .subject(this.getSubject())
            .startDateTime(newStartDateTime)
            .endDateTime(newEndDateTime)
            .description(this.description)
            .location(this.location)
            .isPrivate(this.isPrivate)
            .isAllDay(this.isAllDay)
            .previous(previous)
            .repeatNumber(this.repeatNumber)
            .repeatDays(this.repeatDays)
            .build();

        repeatingEvents.add(repeat);
        previous = repeat;
      }
      index++;
    }
    return repeatingEvents;
  }

  @Override
  public IRepeatingEvent copy(long timeDifference, ZoneId timezone)
      throws IllegalArgumentException {
    IEvent copy = super.copy(timeDifference, timezone);

    ChronoZonedDateTime<LocalDate> adjustedRepeatEndDateTime = null;
    if (this.repeatEndDateTime != null) {
      ZoneOffset currentOffset = startDateTime.getOffset();
      ZoneOffset targetOffset = timezone.getRules().getOffset(Instant.now());
      long offsetDifference = Math.abs(
          currentOffset.getTotalSeconds() - targetOffset.getTotalSeconds()) / 60L;

      if (offsetDifference < Math.abs(timeDifference)) {
        // ignore time zone difference if the offset is smaller than the difference between the
        // new start date time and the current start date time
        adjustedRepeatEndDateTime = repeatEndDateTime.withZoneSameLocal(timezone)
            .plus(timeDifference, ChronoUnit.MINUTES);
      } else {
        // if time zone difference is greater, then apply the time zone difference as well
        adjustedRepeatEndDateTime = repeatEndDateTime.withZoneSameInstant(timezone)
            .plus(timeDifference, ChronoUnit.MINUTES);
      }
    }

    return new RepeatingEvent.RepeatingEventBuilder()
        .subject(this.subject)
        .startDateTime(copy.getStartDateTime())
        .endDateTime(copy.getEndDateTime())
        .description(this.description)
        .isPrivate(this.isPrivate)
        .isAllDay(this.isAllDay)
        .location(this.location)
        .repeatNumber(this.repeatNumber)
        .repeatDays(this.repeatDays)
        .repeatEndDateTime(adjustedRepeatEndDateTime)
        .previous(this.previous)
        .build();
  }

  @Override
  public void updateTimezone(ZoneId timezone) {
    this.startDateTime = startDateTime.withZoneSameInstant(timezone);
    this.endDateTime = endDateTime.withZoneSameInstant(timezone);

    this.repeatEndDateTime =
        repeatEndDateTime != null ? repeatEndDateTime.withZoneSameInstant(timezone) : null;
  }

  /**
   * Returns a string containing all the fields of the repeating event.
   *
   * @return string containing all fields and values
   */
  @Override
  public String toString() {
    String format = "subject: %s, startDateTime: %s, endDateTime: %s, description: %s, "
        + "location: %s, isAllDay: %s, isPrivate: %s, repeatNumber: %s, repeatEndDateTime: %s";
    return String.format(format, subject, startDateTime, endDateTime, description,
        location, isAllDay, isPrivate, repeatNumber, repeatEndDateTime);
  }

  /**
   * Checks whether the event is an instance of repeatingEvent class or not.
   *
   * @return true if it is an instance of Repeating event.
   */
  @Override
  public boolean isRepeating() {
    return true;
  }

  /**
   * Inner Java class representing a builder for the RepeatingEvent class.
   */
  public static class RepeatingEventBuilder extends AbstractEventBuilder<RepeatingEventBuilder> {

    private int repeatNumber;
    private Set<DayOfWeek> days;
    private ChronoZonedDateTime<LocalDate> repeatEndDateTime;
    private IEvent previous;

    /**
     * A builder class for creating Repeating events.
     */
    public RepeatingEventBuilder() {
      super();
      this.repeatEndDateTime = TimeUtils.getMaximumTime();
      this.repeatNumber = 0;
      this.previous = null;
    }

    public RepeatingEventBuilder repeatNumber(int repeatNumber) {
      this.repeatNumber = repeatNumber;
      return getBuilder();
    }

    public RepeatingEventBuilder repeatDays(Set<DayOfWeek> days) {
      this.days = days;
      return getBuilder();
    }

    public RepeatingEventBuilder repeatEndDateTime(
        ChronoZonedDateTime<LocalDate> repeatEndDateTime) {
      this.repeatEndDateTime = repeatEndDateTime;
      return getBuilder();
    }

    public RepeatingEventBuilder previous(IEvent previous) {
      this.previous = previous;
      return getBuilder();
    }

    @Override
    protected RepeatingEventBuilder getBuilder() {
      return this;
    }

    /**
     * Builds a RepeatingEvent object with the values set in the RepeatingEventBuilder.
     *
     * @return RepeatingEvent object containing the values
     */
    public RepeatingEvent build() {
      return new RepeatingEvent(subject, startDateTime, endDateTime, description, location,
          isPrivate, repeatNumber, days, repeatEndDateTime, isAllDay, previous);
    }
  }
}
