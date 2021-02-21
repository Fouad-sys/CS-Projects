package ch.epfl.javass.bits;

import ch.epfl.javass.Preconditions;

/**
 * Class containing methods used for numbers of type int
 * 
 * @author Szabina Horvath-Mikulas (226459)
 * @author Fouad Mahmoud (303076)
 */
public final class Bits32 {

    /**
     * Default Bits32 Private Constructor
     */
    private Bits32() {
    }

    /**
     * returns an integer mask where the bits from start included to start +
     * size excluded are 1s and the rest are 0s
     * 
     * @param start
     *            (int) : starting position of mask (should be positive and
     *            inferior or equal to the the maximum integer size)
     * @param size
     *            (int) : size of mask (should be positive and inferior or equal
     *            to the maximum integer size)
     * @return (int) : mask from start of length size
     * @throws IllegalArgumentException
     *             if start or size aren't positive or inferior or equal to the
     *             maximum integer size
     */
    public static int mask(int start, int size) {

        Preconditions.checkArgument(0 <= start && start <= Integer.SIZE
                && 0 <= size && (start + size) <= Integer.SIZE);

        return size == Integer.SIZE ? ~0 : ((1 << size) - 1) << start;
    }

    /**
     * extracts part of given binary number from start included to start + size
     * excluded
     * 
     * @param bits
     *            (int) : binary number to extract from
     * @param start
     *            (int) : starting position of extraction (must be positive and
     *            less than or equal to the maximum integer size
     * @param size
     *            (int) : size of extraction (must be positive and less than or
     *            equal to the maximum integer size)
     * @return (int) : extracted version of given binary number
     * @throws IllegalArgumentException
     *             if the parameters don't fulfill the conditions required by
     *             the method mask
     */
    public static int extract(int bits, int start, int size) {

        return (bits & mask(start, size)) >>> start;
    }

    /**
     * returns a packed version where each of the components takes length size
     * and is in the given order (LSBs for v1 and so on)
     * 
     * @param v1,
     *            v2 (int) : components to pack (musn't exceed their size
     *            length)
     * @param s1,
     *            s2 (int) : size taken by their corresponding component in
     *            packed number (must be between 0 and 31 included)
     * @return (int) : new packed number with the given components
     * @throws IllegalArgumentException
     *             if one of the components isn't in the boundaries of the
     *             private method check
     */
    public static int pack(int v1, int s1, int v2, int s2) {

        int[] values = { v1, v2 };
        int[] places = { s1, s2 };

        return multiPack(values, places);
    }

    /**
     * returns a packed version where each of the components takes length size
     * and is in the given order (LSBs for v1 and so on)
     * 
     * @param v1,
     *            v2, v3 (int) : components to pack (musn't exceed their size
     *            length)
     * @param s1,
     *            s2, s3 (int) : size taken by their corresponding component in
     *            packed number (must be between 0 and 31 included)
     * @return (int) : new packed number with the given components
     * @throws IllegalArgumentException
     *             if one of the components isn't in the boundaries of the
     *             private method check
     */
    public static int pack(int v1, int s1, int v2, int s2, int v3, int s3) {

        int[] values = { v1, v2, v3 };
        int[] places = { s1, s2, s3 };

        return multiPack(values, places);
    }

    /**
     * returns a packed version where each of the components takes length size
     * and is in the given order (LSBs for v1 and so on)
     * 
     * @param v1,
     *            v2, v3, v4, v5, v6, v7 (int) : components to pack (musn't
     *            exceed their size length)
     * @param s1,
     *            s2, s3, s4, s5, s6, s7 (int) : size taken by their
     *            corresponding component in packed number (must be between 0
     *            and 31 included)
     * @return (int) : new packed number with the given components
     * @throws IllegalArgumentException
     *             if one of the components isn't in the boundaries of the
     *             private method check
     */
    public static int pack(int v1, int s1, int v2, int s2, int v3, int s3,
            int v4, int s4, int v5, int s5, int v6, int s6, int v7, int s7) {

        int[] values = { v1, v2, v3, v4, v5, v6, v7 };
        int[] places = { s1, s2, s3, s4, s5, s6, s7 };

        return multiPack(values, places);
    }

    /**
     * new method that packs a given number of components into one binary number
     * 
     * @param values
     *            (int) : table consisting of he various elements to pack
     * @param places
     *            (int) : table consisting of the corresponding sizes of each
     *            element to pack
     * @return (int) : new packed number with the given components
     * @throws IllegalArgumentException
     *             if one of the components isn't in the boundaries of the
     *             private method check
     */
    private static int multiPack(int values[], int places[]) {

        int placeSum = 0;
        for (int i = 0; i < places.length; i++) {
            placeSum += places[i];
        }
        Preconditions.checkArgument(placeSum <= Integer.SIZE);

        int shift = 0;
        int packedValue = values[0];
        for (int i = 0; i < values.length; i++) {
            check(values[i], places[i]);
            packedValue = packedValue | (values[i] << shift);
            shift += places[i];
        }

        return packedValue;
    }

    /**
     * checks if size is between 1 and 31 included and that v doesn't take more
     * bits than its size
     * 
     * @param v
     *            (int) : value to be checked
     * @param s
     *            (int) : size to be evaluated
     * @throws IllegalArgumentException
     *             if any of the arguments don't fulfill the given conditions
     */
    private static void check(int v, int s) {

        Preconditions.checkArgument(
                s >= 1 && s < Integer.SIZE && extract(v, 0, s) == v);
    }
}