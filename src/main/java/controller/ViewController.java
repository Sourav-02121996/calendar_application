package controller;

import java.util.HashMap;
import java.util.Map;

import model.IModel;
import gui.Features;
import view.Viewer;

/**
 * A Java class that represents a controller that handles events occurring in a Swing UI. Extends
 * the AbstractController class. Implements the ActionListener interface.
 */
public class ViewController extends AbstractController implements Features {

  /**
   * Constructs a ViewController object.
   *
   * @param model IModel object
   */
  public ViewController(IModel model) {
    super(model);
  }

  @Override
  public void setView(Viewer view) {
    super.setView(view);
    view.addFeatures(this);
  }

  /**
   * Calls handleCommand to execute the given command. Calls the view to display an error message if
   * an exception is thrown.
   *
   * @param command command string
   * @return true if the action is successful, false if an error occurs
   */
  private boolean doAction(String command) {
    try {
      handleCommand(command);
      return true;
    } catch (Exception e) {
      view.printError(e.getMessage());
      return false;
    }
  }

  /**
   * The ViewController does not need to read in commands from an input source.
   */
  @Override
  public void listen() {
    throw new UnsupportedOperationException("Not supported.");
  }

  @Override
  public boolean addCalendar(String name, String timezone) {
    if (name == null || name.trim().isEmpty()) {
      view.printError("Please enter a calendar name.");
      return false;
    }
    String command = String.format("create calendar --name \"%s\" --timezone %s", name, timezone);
    return doAction(command);
  }

  @Override
  public boolean editCalendar(String name, String property, String newValue) {
    if (property.equals("name") && (newValue == null || newValue.trim().isEmpty())) {
      view.printError("Please enter a new name.");
      return false;
    }

    String command = String.format("edit calendar --name %s --property %s \"%s\"",
        name, property, newValue);
    return doAction(command);
  }

  @Override
  public boolean useCalendar(String name) {
    String command = String.format("use calendar --name \"%s\"", name);
    return doAction(command);
  }

  @Override
  public boolean addEvent(Map<String, String> inputs) {
    String subject = inputs.get("subject");
    String startDate = inputs.get("startDate");
    String startTime = inputs.get("startTime");
    String endDate = inputs.get("endDate");
    String endTime = inputs.get("endTime");
    String isAllDay = inputs.get("isAllDay");
    String isRepeating = inputs.get("isRepeating");

    if (subject == null || subject.trim().isEmpty()) {
      view.printError("Please enter a subject.");
      return false;
    }

    String startDateTime = String.format("%sT%s", startDate, startTime);
    String endDateTime = String.format("%sT%s", endDate, endTime);

    String command;
    if (isRepeating.equals("true")) {
      String repeatNumberType = inputs.get("repeatNumberType");
      String repeatUntilType = inputs.get("repeatUntilType");
      if (repeatNumberType.equals("false") && repeatUntilType.equals("false")) {
        view.printError("Please choose a repeat type");
        return false;
      }

      String repeatDays = getRepeatDays(inputs);
      if (repeatDays.isEmpty()) {
        view.printError("Please select at least one repeat day.");
        return false;
      }

      if (repeatNumberType.equals("true")) {
        String repeatNumber = inputs.get("repeatNumber");
        if (repeatNumber == null || repeatNumber.trim().isEmpty()) {
          view.printError("Please enter a repeat number greater than 0");
          return false;
        }

        if (isAllDay.equals("true")) {
          // repeating all day events n times
          command = String.format("create event \"%s\" on %s repeats %s for %s times", subject,
              startDate, repeatDays, repeatNumber);
        } else {
          // repeating events n times
          command = String.format("create event \"%s\" from %s to %s repeats %s for %s times",
              subject, startDateTime, endDateTime, repeatDays, repeatNumber);
        }
      } else {
        String repeatEndDate = inputs.get("repeatEndDate");
        String repeatEndTime = inputs.get("repeatEndTime");

        String repeatEndDateTime = String.format("%sT%s", repeatEndDate, repeatEndTime);
        if (isAllDay.equals("true")) {
          // repeating all day events until end date
          command = String.format("create event \"%s\" on %s repeats %s until %s", subject,
              startDate, repeatDays, repeatEndDateTime);
        } else {
          // repeating events until end date
          command = String.format("create event \"%s\" from %s to %s repeats %s until %s", subject,
              startDateTime, endDateTime, repeatDays, repeatEndDateTime);
        }
      }
    } else {
      if (isAllDay.equals("true")) {
        // single all day event
        command = String.format("create event \"%s\" on %s", subject, startDateTime);
      } else {
        // single event
        command = String.format("create event \"%s\" from %s to %s", subject, startDateTime,
            endDateTime);
      }
    }

    command += addOptionalParams(inputs);
    return doAction(command);
  }

  @Override
  public boolean editEvent(Map<String, String> inputs) {
    String subject = inputs.get("subject");
    String property = inputs.get("property");
    String startDate = inputs.get("startDate");
    String startTime = inputs.get("startTime");
    String endDate = inputs.get("endDate");
    String endTime = inputs.get("endTime");

    String newValue;
    switch (property) {
      case "private":
        newValue = inputs.get("isPrivate");
        break;
      case "startDateTime":
      case "endDateTime":
        String newDate = inputs.get("newDate");
        String newTime = inputs.get("newTime");
        newValue = String.format("%sT%s", newDate, newTime);
        break;
      default:
        newValue = inputs.get("newValue");
    }

    String startDateTime = String.format("%sT%s", startDate, startTime);
    String endDateTime = String.format("%sT%s", endDate, endTime);

    String command = String.format("edit event %s \"%s\" from %s to %s with \"%s\"",
        property, subject, startDateTime, endDateTime, newValue);
    return doAction(command);
  }

  @Override
  public boolean editEvents(Map<String, String> inputs) {
    String editBySubject = inputs.get("editBySubject");
    String editByDate = inputs.get("editByDate");

    if (editBySubject.equals("false") && editByDate.equals("false")) {
      view.printError("Please choose an edit method");
      return false;
    }
    String subject = inputs.get("subject");
    String property = inputs.get("property");

    if (subject == null || subject.trim().isEmpty()) {
      view.printError("Please enter a subject");
      return false;
    }

    String newValue;
    switch (property) {
      case "private":
        newValue = inputs.get("isPrivate");
        break;
      case "repeatDays":
        newValue = getRepeatDays(inputs);
        break;
      case "repeatEndDateTime":
        String newDate = inputs.get("newDate");
        String newTime = inputs.get("newTime");
        newValue = String.format("%sT%s", newDate, newTime);
        break;
      default:
        newValue = inputs.get("newValue");
    }

    String command;
    if (editBySubject.equals("true")) {
      // edit events by subject
      command = String.format("edit events %s \"%s\" \"%s\"", property, subject, newValue);
    } else {
      // edit events from startDateTime
      String startDate = inputs.get("startDate");
      String startTime = inputs.get("startTime");
      String startDateTime = String.format("%sT%s", startDate, startTime);

      command = String.format("edit events %s \"%s\" from %s with \"%s\"", property, subject,
          startDateTime, newValue);
    }
    return doAction(command);
  }

  @Override
  public boolean exportCalendar(String path) {
    if (path == null || path.trim().isEmpty()) {
      view.printError("Please choose a target path.");
      return false;
    }
    String command = String.format("export cal %s", path);
    return doAction(command);
  }

  @Override
  public boolean importCalendar(String path) {
    if (path == null || path.trim().isEmpty()) {
      view.printError("Please choose a file to import.");
      return false;
    }
    String command = String.format("import cal %s", path);
    return doAction(command);
  }

  private String getRepeatDays(Map<String, String> inputs) {
    Map<String, Character> charMap = new HashMap<>();
    charMap.put("repeatMonday", 'M');
    charMap.put("repeatTuesday", 'T');
    charMap.put("repeatWednesday", 'W');
    charMap.put("repeatThursday", 'R');
    charMap.put("repeatFriday", 'F');
    charMap.put("repeatSaturday", 'S');
    charMap.put("repeatSunday", 'U');

    StringBuilder result = new StringBuilder();
    for (Map.Entry<String, Character> entry : charMap.entrySet()) {
      if (inputs.get(entry.getKey()).equals("true")) {
        result.append(entry.getValue());
      }
    }
    return result.toString();
  }

  private String addOptionalParams(Map<String, String> inputs) {
    String isPrivate = inputs.get("isPrivate");
    String description = inputs.get("description");
    String location = inputs.get("location");

    String command = "";
    if (!description.trim().isEmpty()) {
      command += String.format(" -d \"%s\"", description);
    }
    if (isPrivate.equals("true")) {
      command += " -p";
    }
    if (!location.trim().isEmpty()) {
      command += String.format(" -l \"%s\"", location);
    }
    return command;
  }
}
