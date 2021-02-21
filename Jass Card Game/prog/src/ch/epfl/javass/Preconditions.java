package ch.epfl.javass;

/**
 * Class containing methods to verify conditions
 * 
 * @author Szabina Horvath-Mikulas (226459)
 * @author Fouad Mahmoud (303076)
 */
public final class Preconditions {

    /**
     * Default Precondition Private Constructor
     */
    private Preconditions() {
    }

    /**
     * checks if the argument is true and therefore fulfills the given condition
     * 
     * @param b
     *            (boolean) : parameter to be checked
     * @throws IllegalArgumentException
     *             if the parameter is false
     */
    public static void checkArgument(boolean b) {
        if (!b) {
            throw new IllegalArgumentException();
        }
    }

    /**
     * checks if the given state is satisfied
     * 
     * @param given
     *            state to be checked
     * @throws IllegalStateException
     *             if the state is not fulfilled
     */
    public static void checkState(boolean b) {
        if (b) {
            throw new IllegalStateException();
        }
    }

    /**
     * checks if index is in the given bounds (between 0 and size included)
     * 
     * @param index.(int)
     *            : index to be checked
     * @param size
     *            (int) : bound of index
     * @return (int) : index if it is in the given bounds
     * @throws IndexOutOfBoundsException
     *             if the index isn't between 0 and size included
     */
    public static int checkIndex(int index, int size) {
        if (index < 0 || size <= index) {
            throw new IndexOutOfBoundsException();
        } else {
            return index;
        }
    }

}