package utils;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.chrono.ChronoZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

import view.EventFormatter;
import view.IFormatter;

import calendar.Event;
import calendar.IEvent;

/**
 * A Java class containing util methods for importing and exporting data.
 */
public class ImportExportUtils {

  private static final String[] HEADERS = {"subject", "startDate", "startTime", "endDate",
      "endTime", "allDayEvent", "description", "location", "private"};

  /**
   * Exports the calendar as a csv file that can be imported to Google Calendar app.
   *
   * @param filename the desired name of the output CSV file (without extension)
   * @return the filepath of the generated csv file
   * @throws IOException if an error occurs when writing the CSV file
   */
  public static String exportCalendar(String filename, List<IEvent> events) throws IOException {
    String filepath = getPath(filename);

    Writer writer = new FileWriter(filepath);
    writer.append(String.join(",", HEADERS)).append("\n");

    IFormatter<IEvent> formatter = new EventFormatter();
    for (IEvent event : events) {
      writer.append(formatter.formatCsv(event)).append("\n");
    }

    writer.close();
    return filepath;
  }

  /**
   * Parses the given file and creates an IEvent object for each row. Returns a list of all events
   *
   * @param filePath import filepath
   * @param zone     current timezone
   * @return List of IEvents
   * @throws IOException if an error occurs when reading the file
   */
  public static List<IEvent> importCalendar(String filePath, ZoneId zone) throws IOException {
    Reader reader = getFileReader(filePath);
    try (Scanner scanner = new Scanner(reader)) {
      if (scanner.hasNextLine()) {
        // skip header row
        scanner.nextLine();
      }

      List<IEvent> events = new ArrayList<>();
      while (scanner.hasNextLine()) {
        IEvent event = buildEvent(scanner.nextLine(), zone);
        events.add(event);
      }

      return events;
    } catch (Exception e) {
      throw new IOException(e);
    }
  }

  private static IEvent buildEvent(String row, ZoneId zone) {
    String pattern = "MM/dd/yyyy'T'hh:mm a";
    String[] parts = row.split(",");

    String subject = parts[0];

    String startDate = parts[1];
    String startTime = parts[2];
    ChronoZonedDateTime<LocalDate> startDateTime = TimeUtils.parseDateTimeStringWithPattern(
        String.format("%sT%s", startDate, startTime), zone, pattern);

    String endDate = parts[3];
    String endTime = parts[4];
    ChronoZonedDateTime<LocalDate> endDateTime = TimeUtils.parseDateTimeStringWithPattern(
        String.format("%sT%s", endDate, endTime), zone, pattern);

    boolean isAllDay = parts[5].equals("True");
    String description = parts[6];
    String location = parts[7];
    boolean isPrivate = parts[8].equals("True");

    return new Event.EventBuilder()
        .subject(subject)
        .startDateTime(startDateTime)
        .endDateTime(endDateTime)
        .isAllDay(isAllDay)
        .description(description)
        .location(location)
        .isPrivate(isPrivate)
        .build();
  }

  private static Reader getFileReader(String filepath) throws IOException {
    InputStream inputStream = new FileInputStream(filepath);
    return new InputStreamReader(inputStream, StandardCharsets.UTF_8);
  }

  private static String getPath(String filepath) {
    Path path = Path.of(filepath);
    if (!path.isAbsolute()) {
      path = Path.of(System.getProperty("user.home"), "Downloads", filepath);
    }
    return path.toString();
  }
}
