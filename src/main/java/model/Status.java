package model;

/**
 * Enum class representing the schedule status in a calendar.
 */
public enum Status {
  BUSY("Busy"),
  AVAILABLE("Available");

  private final String value;

  /**
   * Construct a status enum object.
   *
   * @param value status value
   */
  Status(String value) {
    this.value = value;
  }

  /**
   * Get the enum value.
   *
   * @return value string
   */
  public String getValue() {
    return value;
  }
}
