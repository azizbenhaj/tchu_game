package ch.epfl.tchu;

/**
 * chaque méthode doit vérifier, autant que possible, ses préconditions et lève une exception
 */
public final class Preconditions {
    /**
     *classe préconditions permet de verifier si les arguments sont satisfaits avant l'appel d'une méthode
     */
    private Preconditions() {}
    public static void checkArgument(boolean shouldBeTrue) {
        if(!shouldBeTrue)throw new IllegalArgumentException();
    }
}
