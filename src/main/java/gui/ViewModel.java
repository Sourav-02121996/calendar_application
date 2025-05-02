package gui;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

import calendar.ICalendar;
import calendar.Event;
import calendar.Calendar;
import calendar.IEvent;
import calendar.RepeatingEvent;
import model.IModel;
import utils.TimeUtils;

/**
 * A Java class representing a model for the calendar app that manages the view's state and contains
 * read-only operations relevant for a GUI. Implements the IViewModel interface.
 */
public class ViewModel implements IViewModel {

  private final IModel model;
  private IEvent selectedEvent;
  private Date selectedDate;

  /**
   * Constructs a ViewModel object.
   *
   * @param model IModel object
   */
  public ViewModel(IModel model) {
    this.model = model;
    this.selectedEvent = null;
  }

  /**
   * Get all existing calendars.
   *
   * @return List of ICalendar objects
   */
  @Override
  public List<ICalendar> getAllCalendars() {
    return model.getAllCalendars();
  }

  /**
   * Get the current calendar.
   *
   * @return ICalendar object
   */
  @Override
  public ICalendar getCurrentCalendar() {
    return model.getCurrentCalendar();
  }

  /**
   * Gets all events occurring on a given date. Ignores any IllegalArgumentExceptions assuming that
   * the GUI will be able to indicate that no events were found.
   *
   * @param date LocalDate object
   * @return List of IEvents
   */
  @Override
  public List<IEvent> getEventsOnDate(Date date) {
    ZoneId zoneId = model.getCurrentCalendar().getTimezone();
    try {
      return model.getEventsOnDate(TimeUtils.parseDateAtStartOfDay(date, zoneId));
    } catch (IllegalArgumentException ignored) {
      // ignore error if no events found
      return Collections.emptyList();
    }
  }

  /**
   * Sets the currently selected event.
   *
   * @param event IEvent object
   */
  @Override
  public void setSelectedEvent(IEvent event) {
    this.selectedEvent = event;
  }

  /**
   * Get the currently selected event.
   *
   * @return IEvent object
   */
  @Override
  public IEvent getSelectedEvent() {
    return this.selectedEvent;
  }

  /**
   * Sets the currently selected date.
   *
   * @param date LocalDate object
   */
  @Override
  public void setSelectedDate(Date date) {
    this.selectedDate = date;
  }

  /**
   * Get the currently selected date.
   *
   * @return LocalDate object
   */
  @Override
  public Date getSelectedDate() {
    return this.selectedDate;
  }

  /**
   * Get all editable properties of an event.
   *
   * @return array of property names
   */
  @Override
  public String[] getEditableEventProperties() {
    return Event.getEditableProperties().stream().sorted().toArray(String[]::new);
  }

  /**
   * Get all editable properties when editing multiple events.
   *
   * @return array of property name strings
   */
  @Override
  public String[] getEditableMultipleEventProperties() {
    Set<String> exclude = Event.getExcludedProperties();
    Set<String> eventProperties = Event.getEditableProperties();
    Set<String> repeatingEventProperties = RepeatingEvent.getEditableProperties();

    eventProperties.addAll(repeatingEventProperties);
    eventProperties.removeAll(exclude);
    return eventProperties.stream().sorted().toArray(String[]::new);
  }

  /**
   * Get all editable properties of a calendar.
   *
   * @return array of property name strings
   */
  @Override
  public String[] getEditableCalendarProperties() {
    return Calendar.getEditableProperties().toArray(String[]::new);
  }

  /**
   * Gets all timezones that can be used.
   *
   * @return array of timezone name strings
   */
  @Override
  public String[] getAvailableTimezones() {
    List<String> timezones = TimeUtils.getAllTimezones();
    Collections.sort(timezones);
    return timezones.toArray(new String[0]);
  }

  @Override
  public Date getCurrentDate() {
    ZoneId zone = getCurrentCalendar().getTimezone();
    ZonedDateTime currentTime = ZonedDateTime.now(zone);
    return Date.from(currentTime.toInstant());
  }
}
