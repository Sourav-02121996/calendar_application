package gui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.chrono.ChronoZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import calendar.Event;
import calendar.ICalendar;
import calendar.IEvent;
import mock.MockModel;
import model.IModel;
import model.Model;
import utils.TimeUtils;

/**
 * A JUnit test class for testing the ViewModel class. Uses the MockModel class.
 */
public class ViewModelTest {

  private static ZoneId ZONE_ID;
  private IViewModel viewModel;
  private StringBuilder log;

  @BeforeClass
  public static void setUpBeforeClass() {
    ZONE_ID = ZoneId.of("US/Eastern");
  }

  @Before
  public void setUp() {
    log = new StringBuilder();
    int uniqueCode = 0;
    IModel model = new MockModel(log, uniqueCode);
    viewModel = new ViewModel(model);
  }

  @Test
  public void testGetAllCalendars() {
    List<ICalendar> calendars = viewModel.getAllCalendars();
    assertEquals(1, calendars.size());

    String expected = "Get all calendars";
    assertEquals(expected, log.toString());
  }

  @Test
  public void testGetCurrentCalendar() {
    ICalendar calendar = viewModel.getCurrentCalendar();
    assertNotNull(calendar);
    assertEquals("test", calendar.getName());
    assertEquals(ZoneId.of("US/Eastern"), calendar.getTimezone());
  }

  @Test
  public void testGetEventsOnDate() {
    ChronoZonedDateTime<LocalDate> dateTime =
        TimeUtils.parseDateTimeString("2025-01-01T10:00", ZONE_ID);
    viewModel.getEventsOnDate(Date.from(dateTime.toInstant()));

    String expected = "Input: 2025-01-01T00:00-05:00[US/Eastern]";
    assertEquals(expected, log.toString());
  }

  @Test
  public void testGetEventsOnDateRealModel() {
    IModel realModel = new Model();
    IViewModel realViewModel = new ViewModel(realModel);
    ChronoZonedDateTime<LocalDate> dateTime =
        TimeUtils.parseDateTimeString("2025-01-01T10:00", ZONE_ID);
    IEvent event = new Event.EventBuilder()
        .subject("test")
        .startDateTime(dateTime)
        .endDateTime(dateTime.plus(1, ChronoUnit.HOURS))
        .build();

    realModel.addEvents(List.of(event));
    List<IEvent> events = realViewModel.getEventsOnDate(Date.from(dateTime.toInstant()));

    assertEquals(1, events.size());
    assertEquals(event, events.get(0));
  }

  @Test
  public void testGetEventsOnDateRealModelEmpty() {
    IModel realModel = new Model();
    IViewModel realViewModel = new ViewModel(realModel);
    ChronoZonedDateTime<LocalDate> dateTime =
        TimeUtils.parseDateTimeString("2025-01-01T10:00", ZONE_ID);

    List<IEvent> events = realViewModel.getEventsOnDate(Date.from(dateTime.toInstant()));

    assertEquals(0, events.size());
  }

  @Test
  public void testSetSelectedEvent() {
    ChronoZonedDateTime<LocalDate> dateTime =
        TimeUtils.parseDateTimeString("2025-01-01T10:00", ZONE_ID);
    IEvent event = new Event.EventBuilder()
        .subject("test")
        .startDateTime(dateTime)
        .endDateTime(dateTime.plus(1, ChronoUnit.HOURS))
        .build();
    viewModel.setSelectedEvent(event);

    assertEquals(event, viewModel.getSelectedEvent());
  }

  @Test
  public void testSetNullSelectedEvent() {
    viewModel.setSelectedEvent(null);
    assertNull(viewModel.getSelectedEvent());
  }

  @Test
  public void testGetSelectedEventNull() {
    assertNull(viewModel.getSelectedEvent());
  }

  @Test
  public void testGetSelectedDate() {
    assertNull(viewModel.getSelectedDate());
  }

  @Test
  public void testSetSelectedDate() {
    ChronoZonedDateTime<LocalDate> dateTime =
        TimeUtils.parseDateTimeString("2025-01-01T10:00", ZONE_ID);
    viewModel.setSelectedDate(Date.from(dateTime.toInstant()));
    assertEquals(TimeUtils.asDate(dateTime), viewModel.getSelectedDate());
  }

  @Test
  public void testGetEditableEventProperties() {
    String[] properties = viewModel.getEditableEventProperties();
    assertNotNull(properties);
    assertNotEquals(0, properties.length);
  }

  @Test
  public void testGetEditableMultipleEventProperties() {
    String[] properties = viewModel.getEditableMultipleEventProperties();
    assertNotNull(properties);
    assertNotEquals(0, properties.length);
  }

  @Test
  public void testEditableCalendarProperties() {
    String[] properties = viewModel.getEditableCalendarProperties();
    assertNotNull(properties);
    assertNotEquals(0, properties.length);
  }

  @Test
  public void testGetAvailableTimezones() {
    String[] availableTimezones = viewModel.getAvailableTimezones();
    assertNotNull(availableTimezones);
    assertEquals("Africa/Abidjan", availableTimezones[0]);
    assertNotEquals(0, availableTimezones.length);
  }

  @Test
  public void testGetCurrentDate() {
    Date date = Calendar.getInstance().getTime();
    assertEquals(date.toString(), viewModel.getCurrentDate().toString());
  }
}