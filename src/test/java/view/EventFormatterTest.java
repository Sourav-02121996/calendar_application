package view;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.chrono.ChronoZonedDateTime;
import java.time.temporal.ChronoUnit;

import calendar.Event;
import calendar.IEvent;

/**
 * A JUnit test class for testing the EventFormatter class.
 */
public class EventFormatterTest {

  @Test
  public void testPrintStringWithLocation() {
    String subject = "test";
    ChronoZonedDateTime<LocalDate> startDateTime = ZonedDateTime.now();
    ChronoZonedDateTime<LocalDate> endDateTime = startDateTime
        .plus(1, ChronoUnit.HOURS);
    String location = "location";
    IEvent event = new Event.EventBuilder()
        .subject(subject)
        .startDateTime(startDateTime)
        .endDateTime(endDateTime)
        .location(location)
        .build();

    IFormatter<IEvent> formatter = new EventFormatter();

    String expected = String.format("* subject: %s, startDateTime: %s, endDateTime: %s, "
        + "location: %s", subject, startDateTime, endDateTime, location);
    assertEquals(expected, formatter.formatString(event));
  }

  @Test
  public void testPrintStringWithoutLocation() {
    String subject = "test";
    ChronoZonedDateTime<LocalDate> startDateTime = ZonedDateTime.now();
    ChronoZonedDateTime<LocalDate> endDateTime =
        startDateTime.plus(1, ChronoUnit.HOURS);
    IEvent event = new Event.EventBuilder()
        .subject(subject)
        .startDateTime(startDateTime)
        .endDateTime(endDateTime)
        .build();

    IFormatter<IEvent> formatter = new EventFormatter();

    String expected = String.format("* subject: %s, startDateTime: %s, endDateTime: %s",
        subject, startDateTime, endDateTime);
    assertEquals(expected, formatter.formatString(event));
  }
}