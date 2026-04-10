package ch.epfl.tchu;

/**
 * Preconditions
 *
 * @author OMAR MEZGHANI (311542)
 * @author MOHAMED CHARFI (311171)
 */
public final class Preconditions {
    /**
     * private constructor without parameters
     */

    private Preconditions() {
    }

    /**
     * checks the argument
     *
     * @param shouldBeTrue a boolean parameter
     * @throws IllegalArgumentException if the parameter is false
     */

    public static void checkArgument(boolean shouldBeTrue) {
        if (!shouldBeTrue) {
            throw new IllegalArgumentException();
        }

    }
}
