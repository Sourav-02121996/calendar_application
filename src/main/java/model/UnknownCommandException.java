package model;

/**
 * A Java Exception class that handles an unknown command sent to the application. Extends the
 * RuntimeException class.
 */
public class UnknownCommandException extends RuntimeException {

  /**
   * Constructs an UnknownCommandException object.
   *
   * @param message message string
   */
  public UnknownCommandException(String message) {
    super(message);
  }
}
