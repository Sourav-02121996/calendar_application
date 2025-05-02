package view;

/**
 * An interface for formatting objects of type T into different string representations.
 *
 * @param <T> the type of object this formatter can format.
 */
public interface IFormatter<T> {

  /**
   * Returns a string containing the object's fields separated by commas.
   *
   * @param object object to be parsed
   * @return string containing comma separated values
   */
  String formatCsv(T object);

  /**
   * Returns a human-readable string containing the object's fields.
   *
   * @param object object to be parsed
   * @return string containing event values
   */
  String formatString(T object);
}
