package calendar;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.chrono.ChronoZonedDateTime;
import java.time.zone.ZoneRulesException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import model.ConflictException;
import utils.TimeUtils;

/**
 * A Java class representing a Calendar in the calendar application. Implements the ICalendar
 * interface. Contains operations for editing calendar properties, adding events, and editing events
 * in the calendar.
 */
public class Calendar implements ICalendar {

  private static final Map<String, BiConsumer<ICalendar, String>> SETTERS = new HashMap<>();

  private String name;
  private ZoneId timezone;
  private List<IEvent> events;

  static {
    SETTERS.put("name", ICalendar::setName);
    SETTERS.put("timezone", ICalendar::setTimezone);
  }

  public static Set<String> getEditableProperties() {
    return new HashSet<>(SETTERS.keySet());
  }

  /**
   * Constructs a Calendar object with the given name and timezone. Throws an error if any of the
   * parameters are invalid or if the given timezone is not a valid timezone.
   *
   * @param name     calendar name string
   * @param timezone timezone string
   * @throws IllegalArgumentException if any of the given arguments are invalid
   */
  public Calendar(String name, String timezone) throws IllegalArgumentException {
    if (name == null || name.trim().isEmpty() || timezone == null || timezone.isEmpty()) {
      throw new IllegalArgumentException("Name and time zone cannot be null or empty");
    }

    this.name = name;
    this.events = new LinkedList<>();

    try {
      this.timezone = TimeUtils.getZoneId(timezone);
    } catch (ZoneRulesException e) {
      throw new IllegalArgumentException("Invalid time zone: " + timezone);
    }
  }

  /**
   * Creates a default Calendar with the System timezone. The calendar name is set to "default".
   */
  public Calendar() {
    this.name = "default";
    this.timezone = ZoneId.systemDefault();

    this.events = new LinkedList<>();
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public ZoneId getTimezone() {
    return this.timezone;
  }

  @Override
  public void setName(String value) throws IllegalArgumentException {
    if (value == null || value.trim().isEmpty()) {
      throw new IllegalArgumentException("Calendar name cannot be null or empty");
    }
    this.name = value;
  }

  @Override
  public void setTimezone(String value) throws IllegalArgumentException {
    if (value == null || value.trim().isEmpty()) {
      throw new IllegalArgumentException("Time zone cannot be null or empty");
    }
    try {
      this.timezone = TimeUtils.getZoneId(value);
      updateAllEvents();
    } catch (ZoneRulesException e) {
      throw new IllegalArgumentException("Invalid time zone: " + value);
    }
  }

  @Override
  public void edit(String property, String newValue) throws IllegalArgumentException {
    BiConsumer<ICalendar, String> setter = SETTERS.get(property);
    if (setter != null) {
      setter.accept(this, newValue);
    } else {
      throw new IllegalArgumentException("Invalid property: " + property);
    }
  }

  @Override
  public List<IEvent> getEvents() {
    return events;
  }

  /**
   * Returns a string containing the calendar name and timezone.
   *
   * @return formatted string
   */
  @Override
  public String toString() {
    String format = "name: %s, timezone: %s";
    return String.format(format, name, timezone);
  }

  @Override
  public void addEvents(List<IEvent> newEvents) throws ConflictException {
    // copy the list in case we need to rollback if a conflict is detected
    List<IEvent> originalEvents = new LinkedList<>(events);

    try {
      for (IEvent newEvent : newEvents) {
        // Check for conflicts with existing events first
        for (IEvent existingEvent : events) {
          if (existingEvent.conflictsWith(newEvent)) {
            throw new ConflictException("Event conflicts with an existing event");
          }
        }

        int insertPosition = findInsertPosition(newEvent);
        events.add(insertPosition, newEvent);
      }
    } catch (ConflictException e) {
      // Restore original list on conflict
      events = new LinkedList<>(originalEvents);
      throw e;
    }
  }

  /**
   * Finds the position to insert a new event while maintaining sort order.
   *
   * @param newEvent The event to insert
   * @return The index where the event should be inserted
   */
  private int findInsertPosition(IEvent newEvent) {
    for (int i = 0; i < events.size(); i++) {
      if (events.get(i).compareTo(newEvent) >= 1) {
        return i;
      }
    }
    return events.size();
  }

  /**
   * Edit a single event in the calendar. Searches for the event using the given subject, start date
   * and time, and end date and time.
   *
   * @param property field to be updated
   * @param subject  subject name
   * @param start    event start date and time as a string
   * @param end      event end date and time as a string
   * @param newValue new value of field
   * @throws IllegalArgumentException if an exception occurs
   * @throws ConflictException        if a conflict occurs
   */
  @Override
  public void editSingleEvent(String property, String subject, String start, String end,
      String newValue) throws IllegalArgumentException, ConflictException {
    if (property.equals("repeatDays") || property.equals("repeatNumber")
        || property.equals("repeatEndDateTime")) {
      throw new IllegalArgumentException("Repeat rules cannot be changed for a single event");
    }

    ChronoZonedDateTime<LocalDate> startDateTime = TimeUtils.parseDateTimeString(start, timezone);
    IEvent target = events.stream()
        .filter(event -> event.matches(subject, startDateTime))
        .findFirst()
        .orElse(null);

    if (target == null) {
      throw new IllegalArgumentException("Event not found: " + subject);
    }

    IEvent newEvent = new Event.EventBuilder()
        .subject(target.getSubject())
        .startDateTime(target.getStartDateTime())
        .endDateTime(target.getEndDateTime())
        .description(target.getDescription())
        .isPrivate(target.isPrivate())
        .isAllDay(target.isAllDay())
        .location(target.getLocation())
        .build();

    newEvent.setNewProperty(property, newValue);
    // Check for conflicts with other events if changing time properties
    if (property.equals("startDateTime") || property.equals("endDateTime")) {
      for (IEvent otherEvent : events) {
        if (otherEvent.matches(subject, startDateTime)) {
          continue;
        }

        if (newEvent.conflictsWith(otherEvent)) {
          throw new ConflictException("Event conflicts with an existing event");
        }
      }
    }

    // no conflicts were found, apply the change
    target.setNewProperty(property, newValue);
  }

  @Override
  public void editEventsBySubject(String property, String subject, String newValue)
      throws ConflictException, IllegalArgumentException {
    if (property.equals("startDateTime") || property.equals("endDateTime")) {
      throw new ConflictException("Editing the start or end times of multiple events at once will "
          + "create a conflict.");
    }

    List<IEvent> eventsToEdit = getEvents()
        .stream()
        .filter(event -> event.getSubject().equals(subject))
        .collect(Collectors.toList());

    if (eventsToEdit.isEmpty()) {
      throw new IllegalArgumentException("Event not found: " + subject);
    }
    removeAndAddEvents(eventsToEdit, property, newValue);
  }

  @Override
  public void editEventsFromStartDateTime(String property, String subject, String start,
      String newValue) throws IllegalArgumentException, ConflictException {
    if (property.equals("startDateTime") || property.equals("endDateTime")) {
      throw new ConflictException("Editing the start or end times of multiple events at once will "
          + "create a conflict.");
    }

    ChronoZonedDateTime<LocalDate> startDateTime = TimeUtils.parseDateTimeString(start, timezone);
    List<IEvent> eventsToEdit = getEvents()
        .stream()
        .filter(event -> event.getSubject().equals(subject)
            && !event.getStartDateTime().isBefore(startDateTime))
        .collect(Collectors.toList());

    if (eventsToEdit.isEmpty()) {
      throw new IllegalArgumentException("Event not found: " + subject);
    }

    removeAndAddEvents(eventsToEdit, property, newValue);
  }

  private void removeAndAddEvents(List<IEvent> events, String property, String newValue) {
    boolean editRepeatField = property.equals("repeatDays") || property.equals("repeatNumber")
        || property.equals("repeatEndDateTime");

    List<IEvent> eventsToAdd = new LinkedList<>();
    Set<IEvent> eventsToRemove = new HashSet<>();

    for (IEvent event : events) {
      if (event.isRepeating() && editRepeatField) {
        IRepeatingEvent repeatingEvent = (IRepeatingEvent) event;

        if (eventsToRemove.contains(repeatingEvent)) {
          // skip events that we already know about
          continue;
        }
        eventsToRemove.addAll(getFollowingEvents(repeatingEvent));
        repeatingEvent.setNewProperty(property, newValue);
        eventsToAdd.addAll(repeatingEvent.editRepeat(property));
      } else if (!editRepeatField) {
        event.setNewProperty(property, newValue);
      }
    }

    this.events.removeAll(eventsToRemove);
    try {
      this.addEvents(eventsToAdd);
    } catch (ConflictException e) {
      // add back all the events that were removed
      this.addEvents(new ArrayList<>(eventsToRemove));
      throw e;
    }
  }

  /**
   * Get all events that are part of the same event series as the base event that come after it.
   *
   * @param base base repeating event
   */
  private List<IEvent> getFollowingEvents(IEvent base) {
    List<IEvent> events = getEvents();
    List<IEvent> followingEvents = new LinkedList<>();

    // find index of last repeating event
    IEvent last = base;
    int index = events.indexOf(base);
    while (index < events.size()) {
      if (last.equals(events.get(index).getPrevious())) {
        last = events.get(index);
      }
      index++;
    }

    // now last is the last repeating event
    while (!last.equals(base)) {
      followingEvents.add(last);
      last = last.getPrevious();
    }

    // also remove the base event
    followingEvents.add(last);
    return followingEvents;
  }

  /**
   * Updates the timezones of all events in the calendar.
   */
  private void updateAllEvents() {
    for (IEvent event : events) {
      event.updateTimezone(timezone);
    }
  }
}
