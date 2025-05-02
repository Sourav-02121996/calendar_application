package gui;

import java.awt.Dimension;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;

import java.util.List;

import calendar.ICalendar;

/**
 * A Java class representing a JComboBox for selecting a calendar from a list of names. Extends the
 * JComboBox class.
 */
public class SelectCalendarComboBox extends JComboBox<String> {

  /**
   * Constructs a SelectCalendarComboBox with the list of calendars.
   *
   * @param calendars list of calendars
   */
  public SelectCalendarComboBox(List<ICalendar> calendars) {
    super();
    String[] calendarOptions = calendars
        .stream()
        .map(ICalendar::getName)
        .toArray(String[]::new);
    setModel(new DefaultComboBoxModel<>(calendarOptions));
    setPreferredSize(new Dimension(150, getPreferredSize().height));
  }
}
