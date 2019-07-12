package com.ds.util.collections;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Static method class with convenience methods for Map objects.
 */
public class MapHelper
{
    private MapHelper()
    {
        // static member only class
    }

    /**
     * Initializes a Map from a {@link Map.Entry} list.
     *
     * @param mapFactory The map to populate.
     * @param entries    The data to populate the map from. Use Map.entry or similar to supply these values.
     * @param <M>        The generic for the mapFactory {@code Map<K,V>} and return type.
     * @param <K>        The key type for map.
     * @param <V>        The value type for map.
     * @return Map populated from elements.
     */
    @SafeVarargs
    public static <M extends Map<K, V>, K, V> M asMap(Supplier<M> mapFactory, Map.Entry<? extends K, ? extends V>... entries)
    {
        return Stream.of(entries).collect(mapFactory, (m, entry) -> m.put(entry.getKey(), entry.getValue()), Map::putAll);
    }

    /**
     * Initializes an unmodifiable Map from {@link Map.Entry} list.
     * <p>
     * Depending on the Map type the unmodifiable container will be in order:
     * - NavigableMap
     * - SortedMap
     * - Map (if nothing else).
     *
     * @param mapFactory The map to populate.
     * @param entries    The data to populate the map from. Use Map.entry or similar to supply these values.
     * @param <M>        The generic for the mapFactory {@code Map<K,V>} and return type.
     * @param <K>        The key type for map.
     * @param <V>        The value type for map.
     * @return Unmodifiable Map populated from elements.
     */
    @SafeVarargs
    public static <M extends Map<K, V>, K, V> M asUnmodifiableMap(Supplier<M> mapFactory, Map.Entry<? extends K, ? extends V>... entries)
    {
        return unmodify(asMap(mapFactory, entries));
    }

    /**
     * Method for flipping a {@code Map<K, V>} into a new {@code Map<V, K>} (keys and values are swapped).
     * <p>
     * If the values of originalMap are not unique then the return is undefined.
     *
     * @param mapFactory  The flipped map to populate.
     * @param originalMap Original Map to copy from.
     * @param <M>         The generic for the mapFactory {@code Map<V,K>} and return.
     * @param <K>         Original Map key generic. New Map value generic.
     * @param <V>         Original Map value generic. New Map key generic.
     * @return A Map with key V and value K.
     */
    public static <M extends Map<V, K>, K, V> M flip(Supplier<M> mapFactory, Map<K, V> originalMap)
    {
        if (originalMap == null)
        {
            return null;
        }

        return originalMap.keySet().stream().map(key -> entry(originalMap.get(key), key)).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (v, v2) -> v2, mapFactory));
    }

    /**
     * Method for flipping a {@code Map<K, V>} into a new unmodifiable {@code Map<V, K>} (keys and values are swapped).
     * <p>
     * If the values of originalMap are not unique then the return is undefined.
     *
     * Depending on the Map type the unmodifiable container will be in order:
     * - NavigableMap
     * - SortedMap
     * - Map (if nothing else).
     *
     * @param mapFactory  The flipped map to populate.
     * @param originalMap Original Map to copy from.
     * @param <M>         The generic for the mapFactory {@code Map<V,K>} and return.
     * @param <K>         Original Map key generic. New Map value generic.
     * @param <V>         Original Map value generic. New Map key generic.
     * @return A Map with key V and value K.
     */
    public static <M extends Map<V, K>, K, V> M unmodifiableFlip(Supplier<M> mapFactory, Map<K, V> originalMap)
    {
        return unmodify(flip(mapFactory, originalMap));
    }

    /**
     * Convenience method for creating a {@link Map.Entry}.
     * <p>
     * Map.entry doesn't allow null values (or keys), and it is also available only on jdk 9 or later.
     *
     * @param key   Key for the entry.
     * @param value Value for the entry.
     * @param <K>   Key type.
     * @param <V>   Value type.
     * @return The new entry.
     */
    public static <K, V> Map.Entry<K, V> entry(K key, V value)
    {
        return new AbstractMap.SimpleEntry<>(key, value);
    }

    private static <M extends Map<K, V>, K, V> M unmodify(Map<K, V> map)
    {
        if (map instanceof NavigableMap)
        {
            @SuppressWarnings("unchecked")
            M m = (M) Collections.unmodifiableNavigableMap((NavigableMap<K, V>) map);
            return m;
        }
        else if (map instanceof SortedMap)
        {
            @SuppressWarnings("unchecked")
            M m = (M) Collections.unmodifiableSortedMap((SortedMap<K, V>) map);
            return m;
        }
        else
        {
            @SuppressWarnings("unchecked")
            M m = (M) Collections.unmodifiableMap(map);
            return m;
        }
    }
}
