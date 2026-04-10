package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * Serde
 *
 * @param <T> parameter
 * @author OMAR MEZGHANI (311542)
 * @author MOHAMED CHARFI (311171)
 */
public interface Serde<T> {
    /**
     * serialization of t
     *
     * @param t parameter
     * @return a string encryption of t
     */
    String serialize(T t);

    /**
     * deserialization of a string
     *
     * @param s string encryption
     * @return the deserialized object
     */
    T deserialize(String s);

    /**
     * a static method to create a serde
     *
     * @param fs  the serialize function
     * @param fd  the deserialize function
     * @param <T> the parameter
     * @return the serde with parameter T and both the serialize and deserialize functions
     */
    static <T> Serde<T> of(Function<T, String> fs, Function<String, T> fd) {

        return new Serde<>() {
            @Override
            public String serialize(T t) {
                return fs.apply(t);
            }

            @Override
            public T deserialize(String s) {
                return fd.apply(s);
            }
        };
    }

    /**
     * creates the serde of enum
     *
     * @param l   the list of enum
     * @param <T> parameter
     * @return the serde of enum
     */

    static <T> Serde<T> oneOf(List<T> l) {
        return of(t -> {
                    if (t == null) {
                        return "";
                    } else {
                        return String.valueOf(l.indexOf(t));
                    }
                }
                , s -> {
                    if (s.isEmpty()) {
                        return null;
                    } else {
                        return l.get(Integer.parseInt(s));
                    }
                }
        );
    }

    /**
     * creates the serde of a list
     *
     * @param serde     the serde of the elements of the list
     * @param delimiter the delimiter
     * @param <T>       parameter
     * @return the serde of a list
     */
    static <T> Serde<List<T>> listOf(Serde<T> serde, String delimiter) {
        return new Serde<>() {

            @Override
            public String serialize(List<T> ts) {
                StringJoiner sj = new StringJoiner(delimiter);
                for (T t : ts) {
                    sj.add(serde.serialize(t));
                }
                return sj.toString();
            }

            @Override
            public List<T> deserialize(String s) {
                List<T> result = new ArrayList<>();
                if (!s.isEmpty()) {
                    String[] a = s.split(Pattern.quote(delimiter), -1);
                    for (String element : a) {
                        result.add(serde.deserialize(element));
                    }
                }
                return result;
            }
        };
    }

    /**
     * creates the serde of a SortedBag
     *
     * @param serde     the serde of the elements of the SortedBag
     * @param delimiter the delimiter
     * @param <T>       parameter
     * @return a serde of a SortedBag
     */

    static <T extends Comparable<T>> Serde<SortedBag<T>> bagOf(Serde<T> serde, String delimiter) {
        return new Serde<>() {

            Serde<List<T>> listSerde = listOf(serde, delimiter);

            @Override
            public String serialize(SortedBag<T> ts) {
                List<T> list = ts.toList();
                return listSerde.serialize(list);
            }

            @Override
            public SortedBag<T> deserialize(String s) {
                return SortedBag.of(listSerde.deserialize(s));
            }
        };

    }
}
