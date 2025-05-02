package controller;

import java.util.ArrayList;
import java.util.List;

import command.Command;
import command.CopyEventCommand;
import command.CopyEventsInRangeCommand;
import command.CopyEventsOnDateCommand;
import command.CreateAllDayEventCommand;
import command.CreateAllDayRepeatingEventsUntilEndCommand;
import command.CreateCalendarCommand;
import command.CreateEventCommand;
import command.CreateNAllDayRepeatingEventsCommand;
import command.CreateNRepeatingEventsCommand;
import command.CreateRepeatingEventsUntilEndCommand;
import command.EditAllEventsWithSubjectCommand;
import command.EditCalendarCommand;
import command.EditEventsFromDateCommand;
import command.EditSingleEventCommand;
import command.ExportCalendarCommand;
import command.ImportCalendarCommand;
import command.PrintEventsInRangeCommand;
import command.PrintEventsOnCommand;
import command.ShowStatusCommand;
import command.UseCalendarCommand;

import model.IModel;
import model.UnknownCommandException;
import view.Viewer;

/**
 * An abstract Java class containing common operations and values for controllers used in this
 * application. Implements the Listener interface.
 */
public abstract class AbstractController implements Listener {

  protected IModel model;
  protected Viewer view;

  protected List<Command> commands;

  /**
   * Constructs an AbstractController object.
   *
   * @param model IModel object
   */
  protected AbstractController(IModel model) {
    this.model = model;
    this.commands = initializeCommands();
  }

  /**
   * Initializes and returns a list of all available command instances.
   *
   * @return A list of Command objects
   */
  private List<Command> initializeCommands() {
    List<Command> commands = new ArrayList<>();
    commands.add(new CreateAllDayEventCommand());
    commands.add(new CreateAllDayRepeatingEventsUntilEndCommand());
    commands.add(new CreateNAllDayRepeatingEventsCommand());
    commands.add(new CreateEventCommand());
    commands.add(new CreateNAllDayRepeatingEventsCommand());
    commands.add(new CreateNRepeatingEventsCommand());
    commands.add(new CreateRepeatingEventsUntilEndCommand());
    commands.add(new EditAllEventsWithSubjectCommand());
    commands.add(new EditSingleEventCommand());
    commands.add(new EditEventsFromDateCommand());
    commands.add(new ExportCalendarCommand());
    commands.add(new PrintEventsInRangeCommand());
    commands.add(new PrintEventsOnCommand());
    commands.add(new ShowStatusCommand());
    commands.add(new CreateCalendarCommand());
    commands.add(new EditCalendarCommand());
    commands.add(new UseCalendarCommand());
    commands.add(new CopyEventCommand());
    commands.add(new CopyEventsInRangeCommand());
    commands.add(new CopyEventsOnDateCommand());
    commands.add(new ImportCalendarCommand());
    return commands;
  }

  /**
   * Processes a given command string by matching it against available commands.
   *
   * @param commandString The user input representing a command.
   * @throws Exception throws an UnknownCommandException if no command matches.
   */
  @Override
  public void handleCommand(String commandString) throws Exception {
    for (Command command : this.commands) {
      if (command.execute(model, view, commandString)) {
        return;
      }
    }
    throw new UnknownCommandException(commandString);
  }

  /**
   * Sets the Viewer to be used by the Listener.
   *
   * @param viewer Viewer object
   */
  @Override
  public void setView(Viewer viewer) {
    this.view = viewer;
  }
}
