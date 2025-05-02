package controller;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import model.IModel;
import model.UnknownCommandException;
import view.Viewer;

/**
 * A Java class representing a Listener. Handles command line inputs and calls the relevant methods
 * from the Model.
 */
public class Controller extends AbstractController implements Listener {

  private final Readable in;

  /**
   * Constructs a controller instance. Calls the AbstractController constructor.
   *
   * @param in    Readable object
   * @param model IModel instance
   * @param view  Viewer instance
   */
  public Controller(Readable in, IModel model, Viewer view) {
    super(model);
    this.in = in;
    setView(view);
  }

  /**
   * Listen for commands in the controller's InputStream.
   */
  @Override
  public void listen() {
    try (Scanner scanner = new Scanner(this.in)) {
      while (scanner.hasNextLine()) {
        String input = scanner.nextLine();
        if (input.equals("exit")) {
          break;
        } else {
          handleCommand(input);
        }
      }
    } catch (UnknownCommandException unknown) {
      printUsage(unknown.getMessage());
    } catch (Exception e) {
      view.printError(e.getMessage());
    }
  }

  /**
   * Prints the available commands when an unknown command is encountered.
   *
   * @param commandString The unrecognized command string.
   */
  private void printUsage(String commandString) {
    view.print("Unknown command: " + commandString);

    InputStream inputStream = getClass().getResourceAsStream("/availableCommands.txt");
    assert inputStream != null;

    Scanner scanner = new Scanner(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
    while (scanner.hasNextLine()) {
      view.print(scanner.nextLine());
    }
  }
}
