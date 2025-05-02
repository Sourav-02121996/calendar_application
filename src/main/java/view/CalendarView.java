package view;

import java.util.List;

import javax.swing.JOptionPane;

import calendar.IEvent;
import gui.CalendarFrame;
import gui.Features;
import gui.IViewModel;

/**
 * A Java class representing a Swing GUI for the calendar application. Implements the Viewer
 * interface.
 */
public class CalendarView implements Viewer {

  private final CalendarFrame calendarFrame;

  /**
   * Constructs a CalendarView object. Creates a CalendarFrame object which is the main frame in the
   * GUI.
   *
   * @param viewModel IViewModel object
   */
  public CalendarView(IViewModel viewModel) {
    calendarFrame = new CalendarFrame(viewModel);
  }

  @Override
  public void addFeatures(Features features) {
    calendarFrame.addFeatures(features);
  }

  @Override
  public void print(String message) {
    JOptionPane.showMessageDialog(null, message,
        "Success", JOptionPane.INFORMATION_MESSAGE);
  }

  @Override
  public void printError(String message) {
    JOptionPane.showMessageDialog(null, "Error: " + message,
        "Error", JOptionPane.ERROR_MESSAGE);
  }

  @Override
  public void printEvents(List<IEvent> events) {
    throw new UnsupportedOperationException("Not supported.");
  }
}
