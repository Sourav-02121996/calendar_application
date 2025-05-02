package model;

/**
 * A Java Exception class for representing events conflicts in the calendar. Extends the
 * RuntimeException class.
 */
public class ConflictException extends RuntimeException {

  /**
   * Constructs a ConflictException object.
   *
   * @param message message string
   */
  public ConflictException(String message) {
    super(message);
  }
}
