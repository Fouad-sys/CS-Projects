package ch.epfl.javass.net;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Class containing various methods for serializing and deserializing integer,
 * long and string values.
 * 
 * @author Fouad Mahmoud (303076)
 * @author Max Germano (302702)
 *
 */
public final class StringSerializer {

    private static final int hexaBase = 16;
    
    private StringSerializer() {}

    /**
     * Method that turns a given integer into a base 16 unsigned String.
     * 
     * @param a
     *            the integer to be serialized
     * @return the serialized string
     */
    public static String serializeInt(int a) {
        return Integer.toUnsignedString(a, hexaBase);
    }

    /**
     * Turns a given string to its initial integer value.
     * 
     * @param stringToParse
     *            the string to be deserialized
     * @return the deserialized int
     */
    public static int deserializeInt(String stringToParse) {
        return Integer.parseUnsignedInt(stringToParse, hexaBase);
    }

    /**
     * Turns a long value into an unsigned base 16 string.
     * 
     * @param a
     *            the long value to be serialized
     * @return the serialized string
     */
    public static String serializeLong(long a) {
        return Long.toUnsignedString(a, hexaBase);
    }

    /**
     * Turns a string value into its initial long value.
     * 
     * @param stringToParse
     *            the string to deserialized
     * @return the deserialized long
     */
    public static long deserializeLong(String stringToParse) {
        return Long.parseUnsignedLong(stringToParse, hexaBase);
    }

    /**
     * Turns a string into its ASCII version using the base 64 encoding scheme.
     * 
     * @param a
     *            string to be serialized
     * @return serialized string
     */
    public static String serializeString(String a) {

        return Base64.getEncoder()
                .encodeToString(a.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Turns a string back into its initial UTF-8 version.
     * 
     * @param a
     *            string to be deserialized
     * @return deserialized string
     */
    public static String deserializeString(String a) {

        return new String(Base64.getDecoder().decode(a),
                StandardCharsets.UTF_8);
    }

    /**
     * Combines several strings and seperates them with the given seperation
     * character.
     * 
     * @param separationChar
     *            string version of character to seperate the given strings with
     * @param strings
     *            strings to combine
     * @return string version of combined strings seperated by the
     *         seperationChar
     */
    public static String combine(String separationChar, String... strings) {
        return String.join(separationChar, strings);
    }

    /**
     * Splits a string into an array containing the elements seperated by the
     * seperation character.
     * 
     * @param stringToSplit
     *            the string to split
     * @param separationChar
     *            the string version of the character to be seperate with
     * @return a string array containing the elements seperated by the
     *         seperationChar
     */
    public static String[] split(String stringToSplit, String separationChar) {
        return stringToSplit.split(separationChar);
    }

}