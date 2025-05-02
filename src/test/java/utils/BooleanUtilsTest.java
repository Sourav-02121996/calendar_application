package utils;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

/**
 * A JUnit test class for testing the BooleanUtils class.
 */
public class BooleanUtilsTest {

  @Test(expected = IllegalArgumentException.class)
  public void testParseBooleanInvalid() {
    BooleanUtils.parseBoolean("invalid");
  }

  @Test
  public void testParseBooleanTrue() {
    assertTrue(BooleanUtils.parseBoolean("true"));
  }

  @Test
  public void testParseBooleanFalse() {
    assertFalse(BooleanUtils.parseBoolean("false"));
  }

  @Test
  public void testParseBooleanTrueInsensitive() {
    assertTrue(BooleanUtils.parseBoolean("TRUE"));
  }

  @Test
  public void testParseBooleanFalseInsensitive() {
    assertFalse(BooleanUtils.parseBoolean("FALSE"));
  }
}