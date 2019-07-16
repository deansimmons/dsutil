package com.ds.util.collections;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Static method class with convenience methods for Map objects.
 */
public final class MapHelper
{
    private MapHelper()
    {
        // static member only class
    }

    /**
     * Creates and initializes a Map of <i>any</i> type.
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
     * Creates and initializes an unmodifiable Map of <i>any</i> type.
     * <p>
     * <b>WARNING</b> This method returns the unmodifiable container - therefore the return type will be the closest matching interface between the mapFactory and Collections' unmodifiable methods.
     * As an example a call to produce an unmodifiable TreeMap would not return a TreeMap, instead it would return a NavigableMap from Collections.unmodifiableNavigableMap(). See the return section for return types.
     *
     * @param mapFactory The map to populate.
     * @param entries    The data to populate the map from. Use Map.entry or similar to supply these values.
     * @param <M>        The generic for the mapFactory {@code Map<K,V>} and return type.
     * @param <K>        The key type for map.
     * @param <V>        The value type for map.
     * @return An unmodifiable Map with elements added. The actual type will be the interface most closely matching the mapFactory:
     * <ul>
     * <li>{@link NavigableMap}</li>
     * <li>{@link SortedMap}</li>
     * <li>{@link Map} (if nothing else)</li>
     * </ul>
     */
    @SafeVarargs
    public static <M extends Map<K, V>, K, V> M asUnmodifiableMap(Supplier<M> mapFactory, Map.Entry<? extends K, ? extends V>... entries)
    {
        return unmodify(asMap(mapFactory, entries));
    }

    /**
     * Creates a new Map with flipped key and value pairs from an original Map.
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
     * Creates a new unmodifiable Map with flipped key and value pairs from an original Map.
     * <p>
     * If the values of originalMap are not unique then the return is undefined.
     * <p>
     * <b>WARNING</b> This method returns the unmodifiable container - therefore the return type will be the closest matching interface between the mapFactory and Collections' unmodifiable methods.
     * As an example a call to flip to an unmodifiable TreeMap would not return a TreeMap, instead it would return a NavigableMap from Collections.unmodifiableNavigableMap(). See the return section for return types.
     *
     * @param mapFactory  The flipped map to populate.
     * @param originalMap Original Map to copy from.
     * @param <M>         The generic for the mapFactory {@code Map<V,K>} and return.
     * @param <K>         Original Map key generic. New Map value generic.
     * @param <V>         Original Map value generic. New Map key generic.
     * @return An unmodifiable Map with key V and value K. The actual type will be the interface most closely matching the mapFactory:
     * <ul>
     * <li>{@link NavigableMap}</li>
     * <li>{@link SortedMap}</li>
     * <li>{@link Map} (if nothing else)</li>
     * </ul>
     */
    public static <M extends Map<V, K>, K, V> M unmodifiableFlip(Supplier<M> mapFactory, Map<K, V> originalMap)
    {
        return unmodify(flip(mapFactory, originalMap));
    }

    /**
     * Creates a {@code Map.Entry} value that can contain null keys and values (unlike Java 9's {@code Map.entry()}).
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
