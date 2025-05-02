package model;

import java.time.LocalDate;
import java.time.chrono.ChronoZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import calendar.Calendar;
import calendar.ICalendar;
import calendar.IEvent;
import utils.TimeUtils;

/**
 * Java class representing the model behind the calendar application. Manages the multiple calendars
 * and also contains methods for querying and copying events between calendars.
 */
public class Model implements IModel {

  private final Map<String, ICalendar> calendarMap;
  private ICalendar current;

  /**
   * Constructs a model object. Creates a default calendar using the System timezone.
   */
  public Model() {
    this.calendarMap = new HashMap<>();
    this.current = new Calendar();
    calendarMap.put("default", current);
  }

  /**
   * Get the calendar that is currently in use.
   *
   * @return ICalendar object
   */
  @Override
  public ICalendar getCurrentCalendar() throws IllegalStateException {
    return current;
  }

  /**
   * Creates and adds a new calendar. Stores the calendar in the calendar map along with a new
   * linked list for its events.
   *
   * @param calendar ICalendar object
   * @throws IllegalArgumentException if the calendar already exists
   */
  @Override
  public void addCalendar(ICalendar calendar) throws IllegalArgumentException {
    String name = calendar.getName();

    if (calendarMap.containsKey(name)) {
      throw new IllegalArgumentException("Calendar already exists");
    }
    this.calendarMap.put(name, calendar);
  }

  /**
   * Get all existing calendars.
   *
   * @return List of ICalendar objects
   */
  @Override
  public List<ICalendar> getAllCalendars() {
    return new ArrayList<>(this.calendarMap.values());
  }

  /**
   * Edits an existing calendar.
   *
   * @param name     string value of the calendar's name
   * @param property property name
   * @param newValue new value as a string
   * @throws IllegalArgumentException if the given property is invalid.
   */
  @Override
  public void editCalendar(String name, String property, String newValue)
      throws IllegalArgumentException {
    ICalendar calendar = getCalendar(name);
    calendar.edit(property, newValue);
  }

  /**
   * Change the current calendar to another existing calendar.
   *
   * @param name name of the new calendar
   * @throws IllegalArgumentException if the name doesn't match with an existing calendar.
   */
  @Override
  public void useCalendar(String name) throws IllegalArgumentException {
    this.current = getCalendar(name);
  }

  /**
   * Add events to the current calendar.
   *
   * @param events List of IEvent objects
   * @throws IllegalStateException if there is no current calendar
   * @throws ConflictException     if there is a conflict
   */
  @Override
  public void addEvents(List<IEvent> events) throws IllegalStateException, ConflictException {
    getCurrentCalendar().addEvents(events);
  }

  /**
   * Get all events that occur on the given day. Included events start on the given date, end on the
   * given date, or are ongoing during the day.
   *
   * @param date ChronoZonedDateTime object representing a date
   * @return list of matching events
   * @throws IllegalArgumentException if an error occurs or if no results are found
   */
  @Override
  public List<IEvent> getEventsOnDate(ChronoZonedDateTime<LocalDate> date)
      throws IllegalArgumentException {
    List<IEvent> result = getEvents().stream()
        .filter(event -> event.isOnSameDay(date))
        .collect(Collectors.toList());

    if (result.isEmpty()) {
      throw new IllegalArgumentException("No events found on the given date");
    }
    return result;
  }

  /**
   * Get all events that occur in the given time range.
   *
   * @param startDateTime start of time range
   * @param endDateTime   end of time range
   * @return list of matching events
   * @throws IllegalArgumentException if the given dates are invalid
   */
  @Override
  public List<IEvent> getEventsInRange(ChronoZonedDateTime<LocalDate> startDateTime,
      ChronoZonedDateTime<LocalDate> endDateTime)
      throws IllegalArgumentException {
    if (!endDateTime.isAfter(startDateTime)) {
      throw new IllegalArgumentException("Range end date must be after range start date");
    }

    List<IEvent> result = getEvents().stream()
        .filter(event -> event.isOverlap(startDateTime, endDateTime))
        .collect(Collectors.toList());

    if (result.isEmpty()) {
      throw new IllegalArgumentException("No events found in given date range");
    }
    return result;
  }

  /**
   * Get the status on a given date and time.
   *
   * @param dateTimeString date and time as a string
   * @return string value of status
   */
  @Override
  public String getStatus(String dateTimeString) {
    ChronoZonedDateTime<LocalDate> dateTime = TimeUtils.parseDateTimeString(dateTimeString,
        getCurrentCalendar().getTimezone());
    List<IEvent> events = getEvents();
    if (events.stream().anyMatch(event -> event.clashesWith(dateTime))) {
      return Status.BUSY.getValue();
    }
    return Status.AVAILABLE.getValue();
  }

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
  @Override
  public void editEventsFromStartDateTime(String property, String subject, String start,
      String newValue) throws ConflictException, IllegalArgumentException {
    getCurrentCalendar().editEventsFromStartDateTime(property, subject, start, newValue);
  }

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
  @Override
  public void editSingleEvent(String property, String subject, String start, String end,
      String newValue) throws IllegalArgumentException, ConflictException {
    getCurrentCalendar().editSingleEvent(property, subject, start, end, newValue);
  }

  /**
   * Edit all events that have the same subject.
   *
   * @param property field to be updated
   * @param subject  event subject
   * @param newValue new value as a string
   * @throws ConflictException        if a conflict occurs
   * @throws IllegalArgumentException if an argument is invalid
   */
  @Override
  public void editEventsBySubject(String property, String subject, String newValue)
      throws ConflictException, IllegalArgumentException {
    getCurrentCalendar().editEventsBySubject(property, subject, newValue);
  }

  /**
   * Copy an event with the given subject and start date and time to the target calendar at the
   * given start date and time.
   *
   * @param subject          Event subject name
   * @param targetCalendar   target calendar name
   * @param start            start date and time as a string
   * @param newCalendarStart new start date and time as a string
   * @throws IllegalArgumentException if an argument is invalid
   * @throws DateTimeParseException   if an error occurs when parsing the time
   */
  @Override
  public void copyEvent(String subject, String targetCalendar, String start,
      String newCalendarStart) throws IllegalArgumentException, DateTimeParseException {
    if (current == null) {
      throw new IllegalArgumentException("No current calendar");
    }

    // parse times
    ChronoZonedDateTime<LocalDate> startDateTime =
        TimeUtils.parseDateTimeString(start, current.getTimezone());
    ChronoZonedDateTime<LocalDate> newCalendarStartDateTime =
        TimeUtils.parseDateTimeString(newCalendarStart, current.getTimezone());

    // get events in current calendar and find target event
    IEvent event = getEvents().stream()
        .filter(e -> e.matches(subject, startDateTime))
        .findFirst()
        .orElse(null);
    if (event == null) {
      throw new IllegalArgumentException("Event not found");
    }

    ICalendar target = getCalendar(targetCalendar);
    // calculate difference in minutes between this event's start and the new start
    long timeDifference = TimeUtils.getDifferenceInMinutes(event.getStartDateTime(),
        newCalendarStartDateTime);
    IEvent copy = event.copy(timeDifference, target.getTimezone());

    target.addEvents(List.of(copy));
  }

  /**
   * Copy all events from the current calendar to the target calendar at a new time that occur in
   * the given time range.
   *
   * @param start            start date and time of the range as a string
   * @param end              end date and time of the range as string
   * @param calendarName     target calendar name
   * @param newCalendarStart new start date and time as a string
   * @throws IllegalArgumentException if an argument is invalid
   * @throws ConflictException        if a conflict occurs
   */
  @Override
  public void copyEventsInRange(String start, String end, String calendarName,
      String newCalendarStart) throws IllegalArgumentException, ConflictException {
    if (current == null) {
      throw new IllegalArgumentException("No current calendar");
    }

    // parse times
    ChronoZonedDateTime<LocalDate> startDate = TimeUtils.parseDateString(start,
        current.getTimezone());
    ChronoZonedDateTime<LocalDate> endDate = TimeUtils.parseDateString(end,
        current.getTimezone());
    ChronoZonedDateTime<LocalDate> newCalendarStartDate =
        TimeUtils.parseDateString(newCalendarStart, current.getTimezone());

    ICalendar target = getCalendar(calendarName);
    List<IEvent> events = getEventsInRange(startDate, endDate);

    copyAndAddEvents(target, events, newCalendarStartDate);
  }

  /**
   * Copy all events that occur on the given date to the target calendar at the new start date.
   *
   * @param dateString      date and time as a string
   * @param newCalendarDate date and time as a string
   * @param calendarName    target calendar name
   * @throws IllegalArgumentException if an argument is invalid
   * @throws ConflictException        if a conflict occurs
   */
  @Override
  public void copyEventsOnDate(String dateString, String newCalendarDate, String calendarName)
      throws IllegalArgumentException, ConflictException {
    if (current == null) {
      throw new IllegalArgumentException("No current calendar");
    }

    // parse times
    ChronoZonedDateTime<LocalDate> date = TimeUtils.parseDateString(dateString,
        current.getTimezone());
    ChronoZonedDateTime<LocalDate> newCalendarStartDate =
        TimeUtils.parseDateString(newCalendarDate, current.getTimezone());

    ICalendar target = getCalendar(calendarName);
    List<IEvent> events = getEventsOnDate(date);

    copyAndAddEvents(target, events, newCalendarStartDate);
  }

  private void copyAndAddEvents(ICalendar target, List<IEvent> events,
      ChronoZonedDateTime<LocalDate> newStartDateTime) throws ConflictException {
    // get difference in days converted to minutes, between start date of first event
    // and the new start date
    long difference = TimeUtils.getDifferenceInDays(events.get(0).getStartDateTime(),
        newStartDateTime);

    int conflicts = 0;
    for (IEvent event : events) {
      IEvent copy = event.copy(difference, target.getTimezone());
      try {
        target.addEvents(List.of(copy));
      } catch (ConflictException ignored) {
        conflicts++;
      }
    }

    if (conflicts > 0) {
      throw new ConflictException("Skipped " + conflicts + " conflicting event"
          + (conflicts > 1 ? "s" : ""));
    }
  }

  private ICalendar getCalendar(String name) throws IllegalArgumentException {
    ICalendar calendar = calendarMap.getOrDefault(name, null);

    if (calendar == null) {
      throw new IllegalArgumentException("Calendar not found");
    }
    return calendar;
  }

  private List<IEvent> getEvents() throws IllegalStateException {
    if (this.current == null) {
      throw new IllegalStateException("No calendar in use");
    }
    return this.current.getEvents();
  }
}



















