package utils;

/**
 * A Java utility class for boolean operations. Provides helper methods for parsing and handling
 * boolean values.
 */
public class BooleanUtils {

  /**
   * Parse a boolean flag from a string.
   *
   * @param property string value to parse
   * @return parsed boolean value
   * @throws IllegalArgumentException if the string is not true or false
   */
  public static boolean parseBoolean(String property) throws IllegalArgumentException {
    if (!property.equalsIgnoreCase("true")
        && !property.equalsIgnoreCase("false")) {
      throw new IllegalArgumentException("Invalid property: " + property);
    }
    return Boolean.parseBoolean(property);
  }
}
