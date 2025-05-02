package view;

import static org.junit.Assert.assertEquals;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.chrono.ChronoZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.io.IOException;

import org.junit.Test;

import calendar.Event;
import calendar.IEvent;
import utils.TimeUtils;

/**
 * A JUnit class for testing the View class.
 */
public class ViewTest {

  @Test
  public void testPrint() throws IOException {
    Appendable out = new StringBuilder();
    View view = new View(out);
    String message = "Created event";
    view.print(message);

    assertEquals(message + "\n", out.toString());
  }

  @Test
  public void testPrintEvents() throws Exception {
    Appendable output = new StringBuilder();
    Viewer view = new View(output);

    ZoneId zoneId = ZoneId.of("US/Eastern");
    ChronoZonedDateTime<LocalDate> dateTime =
        TimeUtils.parseDateTimeString("2025-01-01T10:00", zoneId);
    IEvent event1 = new Event.EventBuilder()
        .subject("test 1")
        .startDateTime(dateTime)
        .endDateTime(dateTime.plus(1, ChronoUnit.HOURS))
        .location("testLocation")
        .description("testDescription")
        .isPrivate(true)
        .build();

    IEvent event2 = new Event.EventBuilder()
        .subject("test 2")
        .startDateTime(dateTime.plus(1, ChronoUnit.DAYS))
        .endDateTime(dateTime.plus(2, ChronoUnit.DAYS))
        .location("location")
        .description("testDescription")
        .isAllDay(true)
        .build();

    view.printEvents(List.of(event1, event2));

    String expected = "* subject: test 1, startDateTime: 2025-01-01T10:00-05:00[US/Eastern], "
        + "endDateTime: 2025-01-01T11:00-05:00[US/Eastern], location: testLocation\n"
        + "* subject: test 2, startDateTime: 2025-01-02T10:00-05:00[US/Eastern], "
        + "endDateTime: 2025-01-03T10:00-05:00[US/Eastern], location: location\n";

    assertEquals(expected, output.toString());
  }
}