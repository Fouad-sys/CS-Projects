package ch.epfl.javass.bits;

import ch.epfl.javass.Preconditions;

/**
 * Class containing methods used for numbers of type long
 * 
 * @author Szabina Horvath-Mikulas (226459)
 * @author Fouad Mahmoud (303076)
 */
public final class Bits64 {

    /**
     * Default Bits64 Private Constructor
     */
    private Bits64() {
    }

    /**
     * returns a long mask where the bits from start included to start + size
     * excluded are 1s and the rest are 0s
     * 
     * @param start
     *            (int) : starting position of mask (should be positive and
     *            inferior or equal to the the maximum long size)
     * @param size
     *            (int) : size of mask (should be positive and inferior or equal
     *            to the maximum long size)
     * @return (long) : mask from start of length size
     * @throws IllegalArgumentException
     *             if start or size aren't positive or inferior or equal to the
     *             maximum long size
     */
    public static long mask(int start, int size) {

        Preconditions.checkArgument(0 <= start && start <= Long.SIZE
                && 0 <= size && (start + size) <= Long.SIZE);

        return size == Long.SIZE ? ~0L : ((1L << size) - 1L) << start;
    }

    /**
     * extracts part of given binary number from start included to start + size
     * excluded
     * 
     * @param bits
     *            (long) : binary number to extract from
     * @param start
     *            (int) : starting position of extraction (must be positive and
     *            less than or equal to the maximum long size
     * @param size
     *            (int) : size of extraction (must be positive and less than or
     *            equal to the maximum long size)
     * @return (long) : extracted version of given binary number
     * @throws IllegalArgumentException
     *             if the parameters don't fulfill the conditions required by
     *             the method mask
     */
    public static long extract(long bits, int start, int size) {

        return (bits & mask(start, size)) >>> start;
    }

    /**
     * returns a packed version where each of the components takes length size
     * and is in the given order (LSBs for v1 and so on)
     * 
     * @param v1,
     *            v2 (long) : components to pack (musn't exceed their size
     *            length)
     * @param s1,
     *            s2 (int) : size taken by their corresponding component in
     *            packed number (must be between 0 and 63 included)
     * @return (long) : new packed number with the given components
     * @throws IllegalArgumentException
     *             if one of the components isn't in the boundaries of the
     *             private method check
     */
    public static long pack(long v1, int s1, long v2, int s2) {
        Preconditions.checkArgument((s1 + s2) <= Long.SIZE);

        check(v1, s1);
        check(v2, s2);

        long packedValue = v2 << s1;

        return packedValue | v1;
    }

    /**
     * checks if size is between 1 and 63 included and that v doesn't take more
     * bits than its size
     * 
     * @param v
     *            (long) : value to be checked
     * @param s
     *            (int) : size to be evaluated
     * @throws IllegalArgumentException
     *             if any of the arguments don't fulfill the given conditions
     */
    private static void check(long v, int s) {

        Preconditions.checkArgument(
                s >= 1 && s < Long.SIZE && extract(v, 0, s) == v);
    }

}