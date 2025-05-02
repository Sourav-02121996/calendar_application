package mock;

import java.util.List;

import calendar.IEvent;
import gui.Features;
import view.Viewer;

/**
 * A Java class representing a mock view.
 */
public class MockView implements Viewer {

  private final StringBuilder log;

  /**
   * Constructs a MockView object.
   *
   * @param log StringBuilder object.
   */
  public MockView(StringBuilder log) {
    this.log = log;
  }

  @Override
  public void print(String message) {
    log.append(message).append(System.lineSeparator());
  }

  @Override
  public void addFeatures(Features features) {
    // do nothing
  }

  @Override
  public void printError(String message) {
    log.append(message).append(System.lineSeparator());
  }

  @Override
  public void printEvents(List<IEvent> events) {
    log.append("Events added").append(System.lineSeparator());
  }
}
