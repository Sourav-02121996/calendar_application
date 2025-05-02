import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.SwingUtilities;

import controller.ViewController;
import model.IModel;
import controller.Listener;
import view.CalendarView;
import gui.IViewModel;
import gui.ViewModel;
import view.Viewer;
import model.Model;
import controller.Controller;
import view.View;

/**
 * Main class for the Calendar application.
 */
public class CalendarApp {

  // Updated regex pattern to allow full file paths ending with .txt
  private static final Pattern HEADLESS_MODE = Pattern.compile(
      "^\\s*--mode\\s+headless\\s+(.+\\.txt)\\s*$");
  private static final Pattern INTERACTIVE_MODE = Pattern.compile(
      "^\\s*--mode\\s+interactive\\s*$");
  private static final String USAGE = "Usage:"
      + "\n java CalendarApp --mode headless path-of-script file"
      + "\n java CalendarApp --mode interactive";

  /**
   * Initiates the execution of the event calendar program.
   *
   * @param args the command-line arguments
   */
  public static void main(String[] args) {
    IModel model = new Model();
    Viewer view;

    try {
      if (args.length == 0) {
        IViewModel viewModel = new ViewModel(model);
        ViewController controller = new ViewController(model);

        SwingUtilities.invokeLater(() -> controller.setView(
            new CalendarView(viewModel))
        );
      } else {
        Reader input = parseArgs(args);
        Appendable out = System.out;
        view = new View(out);
        if (input != null) {
          Listener controller = new Controller(input, model, view);
          controller.listen();
        }
      }
    } catch (IOException e) {
      System.err.println(e.getMessage());
    }
  }

  /**
   * Parse command-line arguments and return the appropriate input reader.
   *
   * @param args command-line arguments
   * @return Reader object for the controller or null if arguments are invalid
   * @throws FileNotFoundException if the file cannot be opened
   */
  private static Reader parseArgs(String[] args) throws FileNotFoundException {
    String argString = String.join(" ", args);
    Matcher headlessMatcher = HEADLESS_MODE.matcher(argString);
    Matcher interactiveMatcher = INTERACTIVE_MODE.matcher(argString);

    if (interactiveMatcher.matches()) {
      return new InputStreamReader(System.in);
    } else if (headlessMatcher.matches()) {
      String filename = headlessMatcher.group(1);
      File file = new File(filename);
      if (!file.exists()) {
        System.out.println("Error: File '" + filename + "' not found.");
        return null;
      }
      return new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8);
    } else {
      System.out.println(USAGE);
      return null;
    }
  }
}
