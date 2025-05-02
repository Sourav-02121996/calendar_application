package mock;

import java.time.LocalDate;
import java.time.chrono.ChronoZonedDateTime;
import java.util.List;

import calendar.Calendar;
import calendar.ICalendar;
import calendar.IEvent;
import model.IModel;

/**
 * A Java class representing a mock implementation of the IModel interface.
 */
public class MockModel implements IModel {

  private final StringBuilder log;
  private final int uniqueCode;

  /**
   * Constructs a MockModel object.
   *
   * @param log        StringBuilder object
   * @param uniqueCode unique int value
   */
  public MockModel(StringBuilder log, int uniqueCode) {
    this.log = log;
    this.uniqueCode = uniqueCode;
  }

  @Override
  public void addEvents(List<IEvent> newEvents) {
    log.append("Input: ").append(newEvents.toString());
  }

  @Override
  public List<IEvent> getEventsOnDate(ChronoZonedDateTime<LocalDate> date) {
    log.append("Input: ").append(date.toString());
    return List.of();
  }

  @Override
  public List<IEvent> getEventsInRange(ChronoZonedDateTime<LocalDate> startDateTime,
      ChronoZonedDateTime<LocalDate> endDateTime)
      throws IllegalArgumentException {
    log.append("Input: ").append(startDateTime.toString()).append(" - ")
        .append(endDateTime.toString());
    return List.of();
  }

  @Override
  public String getStatus(String dateTime) {
    log.append("Input: ").append(dateTime);
    return String.valueOf(uniqueCode);
  }

  @Override
  public void editEventsFromStartDateTime(String property, String subject, String start,
      String newValue) {
    log.append("Input: ").append(property).append(" - ").append(subject).append(" - ")
        .append(start).append(" - ").append(newValue);
  }

  @Override
  public void editSingleEvent(String property, String subject, String startDateTime,
      String endDateTime, String newValue) {
    log.append("Input: ").append(property).append(" - ").append(subject).append(" - ")
        .append(startDateTime).append(" - ").append(endDateTime).append(" - ")
        .append(newValue);
  }

  @Override
  public void editEventsBySubject(String property, String subject, String newValue) {
    log.append("Input: ").append(property).append(" - ").append(subject).append(" - ")
        .append(newValue);
  }

  @Override
  public void addCalendar(ICalendar calendar) throws IllegalArgumentException {
    log.append("Input: ").append(calendar.toString());
  }

  @Override
  public void editCalendar(String name, String property, String newValue) {
    log.append("Input: ").append(name).append(" - ").append(property).append(" - ")
        .append(newValue);
  }

  @Override
  public void useCalendar(String name) throws IllegalArgumentException {
    log.append("Input: ").append(name);
  }

  @Override
  public ICalendar getCurrentCalendar() {
    return new Calendar("test", "US/Eastern");
  }

  @Override
  public List<ICalendar> getAllCalendars() {
    log.append("Get all calendars");
    return List.of(getCurrentCalendar());
  }

  @Override
  public void copyEvent(String eventName, String targetCalendar, String startDateTime,
      String newCalendarStartDateTime) {
    log.append("Input: ").append(eventName).append(" - ").append(targetCalendar).append(" - ")
        .append(startDateTime).append(" - ").append(newCalendarStartDateTime);
  }

  @Override
  public void copyEventsInRange(String startDate, String endDate, String calendarName,
      String newCalendarStartDate) {
    log.append("Input: ").append(startDate).append(" - ").append(endDate).append(" - ")
        .append(calendarName).append(" - ").append(newCalendarStartDate);
  }

  @Override
  public void copyEventsOnDate(String start, String newCalendarStart, String target) {
    log.append("Input: ").append(start).append(" - ").append(newCalendarStart)
        .append(" - ").append(target);
  }
}
