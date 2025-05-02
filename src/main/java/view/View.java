package view;

import java.io.IOException;
import java.util.List;

import calendar.IEvent;
import gui.Features;

/**
 * Java class representing the application's command line interface. Implements the Viewer
 * interface.
 */
public class View implements Viewer {

  private final Appendable out;

  /**
   * Constructs a View object.
   *
   * @param out appendable object
   */
  public View(Appendable out) {
    this.out = out;
  }

  @Override
  public void print(String message) {
    try {
      out.append(message.trim()).append("\n");
    } catch (IOException e) {
      System.err.println(e.getMessage());
    }
  }

  @Override
  public void addFeatures(Features features) {
    throw new UnsupportedOperationException("Not supported.");
  }

  @Override
  public void printError(String message) {
    try {
      out.append("Error: ").append(message.trim()).append("\n");
    } catch (IOException e) {
      System.err.println(e.getMessage());
    }
  }

  @Override
  public void printEvents(List<IEvent> events) throws IOException {
    StringBuilder result = new StringBuilder();
    IFormatter<IEvent> formatter = new EventFormatter();

    for (IEvent event : events) {
      result.append(formatter.formatString(event)).append("\n");
    }
    out.append(result.toString().trim()).append("\n");
  }
}
