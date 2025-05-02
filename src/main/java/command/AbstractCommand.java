package command;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import model.IModel;
import view.Viewer;

/**
 * Abstract class for a command. Implements the Command interface.
 */
public abstract class AbstractCommand implements Command {

  protected static final String OPTIONAL_PARAMS = "(?:\\s+-d\\s+(?:\"([^\"]*)\"|([^\"\\s]+)))?"
      + "(\\s+-p)?(?:\\s+-l\\s+(?:\"([^\"]*)\"|([^\"\\s]+)))?(?:\\s+|$)";

  protected final Pattern pattern;

  /**
   * Constructor that takes a regex pattern.
   *
   * @param pattern regex pattern to match this command
   */
  protected AbstractCommand(Pattern pattern) {
    this.pattern = Pattern.compile(pattern.pattern(), Pattern.CASE_INSENSITIVE);
  }

  /**
   * Attempts to match the provided command string against the pattern. Executes the command if it
   * matches the pattern.
   *
   * @param model         IModel object
   * @param view          Viewer object
   * @param commandString command string
   * @return              {@code true}  if the command string matches and executes successfully,
   *                      {@code false} otherwise.
   * @throws Exception    if an error occurs during command execution.
   */
  @Override
  public boolean execute(IModel model, Viewer view, String commandString) throws Exception {
    Matcher matcher = pattern.matcher(commandString);
    if (!matcher.matches()) {
      return false;
    }

    executeMatched(model, view, matcher);
    return true;
  }


  /**
   * Execute the command with the matched pattern.
   *
   * @param model   IModel object
   * @param view    Viewer object
   * @param matcher regex matcher
   * @throws Exception if an error occurs
   */
  protected abstract void executeMatched(IModel model, Viewer view, Matcher matcher)
      throws Exception;
}