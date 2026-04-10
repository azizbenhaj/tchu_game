package ch.epfl.tchu.net;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * @author Ahmed Aziz BEN HAJ (sciper: 310934) HMIDA et Aziz Laadher(sciper: 315196) / le 20 avril 2021
 *représente ce que l'on nomme parfois un serde (de serializer-deserializer)
 * @param <T> le type des éléments que le serde
 */
public interface Serde<T> {
    /**
     * methode de serialisation
     * @param t l'objet a serialiser
     * @return  la chaîne correspondante
     */
     String serialize(T t);

    /**
     *methode de deserialisation
     * @param s la chaine a deserialiser
     * @return l'objet correspondant
     */
     T deserialize(String s);

    /**
     * methode de (de)serialisation
     * @param serializer fonction de serialisation
     * @param deserializer fonction de deserialisation
     * @param <T> le type des éléments que le serde
     * @return  le serde correspondant
     */
     static <T> Serde<T> of(Function<T, String> serializer, Function<String, T> deserializer) {
        Preconditions.checkArgument(serializer!=null&&deserializer!=null);
        return new Serde<>() {
            @Override
            public String serialize(T object) {
                return serializer.apply(object);
            }
            @Override
            public T deserialize(String serialized) {
                return deserializer.apply(serialized);
            }
        };
    }


    /**
     * methode de (de)serialisation
     * @param liste la liste de toutes les valeurs d'un ensemble de valeurs énuméré
     * @param <T> le type des éléments que le serde
     * @return le serde correspondant
     */
     static <T> Serde<T> oneOf(List<T> liste) {
        Preconditions.checkArgument(liste!=null);
        Function<T, String> f = (T t) -> String.valueOf(liste.indexOf(t));
        Function<String, T> d = (String s) -> s.isEmpty() ? null : liste.get(Integer.parseInt(s));
        return Serde.of(f, d);
    }


    /**
     * methode de (de)serialisation
     * @param serde serde utilisé
     * @param separator separateur utilisé
     * @param <T> le type des éléments que le serde
     * @return le serde correspondant
     */
     static <T> Serde<List<T>> listOf(Serde<T> serde, String separator) {
        return new Serde<>() {
            @Override
            public String serialize(List<T> object) {
                List<String> st = new ArrayList<>();
                for (T t : object) {
                    st.add(serde.serialize(t));
                }
                return String.join(separator, st);
            }
            @Override
            public List<T> deserialize(String serialized) {
                if (serialized.isEmpty()) return List.of();
                String[] data = serialized.split(Pattern.quote(separator), -1);
                List<T> result = new ArrayList<>();
                for (String s : data) {
                    result.add(serde.deserialize(s));
                }
                return result;

            }
        };
    }

    /**
     * methode de (de)serialisation
     * @param serde serde utilisé
     * @param separator separateur utilisé
     * @param <T> le type des éléments que le serde
     * @return le serde correspondant
     */
     static <T extends Comparable<T>> Serde<SortedBag<T>> bagOf(Serde<T> serde, String separator) {
        return new Serde<>() {
            @Override
            public String serialize(SortedBag<T> object) {

                List<String>st =new ArrayList<>();
                for (T t : object) {
                    st.add(serde.serialize(t));
                }
                return String.join(separator,st);
            }

            @Override
            public SortedBag<T> deserialize(String serialized) {
                if (serialized.isEmpty()) return SortedBag.of();
                String[] data = serialized.split(Pattern.quote(separator), -1);
                SortedBag<T> result = SortedBag.of();
                for (String s : data) {
                    result = result.union(SortedBag.of(serde.deserialize(s)));
                }
                return result;
            }
        };
    }

}