package view;

import java.time.format.DateTimeFormatter;

import calendar.IEvent;

/**
 * A Java class representing a formatter for implementations of the IEvent interface.
 */
public class EventFormatter implements IFormatter<IEvent> {

  @Override
  public String formatCsv(IEvent event) {
    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");

    return String.join(",",
        event.getSubject(),
        event.getStartDateTime().format(dateFormatter),
        event.getStartDateTime().format(timeFormatter),
        event.getEndDateTime().format(dateFormatter),
        event.getEndDateTime().format(timeFormatter),
        event.isAllDay() ? "True" : "False",
        event.getDescription() != null ? event.getDescription() : "",
        event.getLocation() != null ? event.getLocation() : "",
        event.isPrivate() ? "True" : "False"
    );
  }

  @Override
  public String formatString(IEvent event) {
    String result = String.format("* subject: %s, startDateTime: %s, endDateTime: %s",
        event.getSubject(), event.getStartDateTime(), event.getEndDateTime());
    String location = event.getLocation();
    if (!location.isEmpty()) {
      result += ", location: " + location;
    }
    return result;
  }
}
