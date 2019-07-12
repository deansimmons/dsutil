package com.ds.util.collections;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Static method class with convenience methods for Collection objects.
 */
public class CollectionHelper
{
    private CollectionHelper()
    {
        // static member only class
    }

    /**
     * Creates or uses a Collection and populates that Collection with elements.
     * <p>
     * Example - This command creates a LinkedHashSet of the first three prime numbers.
     * <p>
     * {@code Set<Integer> intSet = CollectionHelper.asCollection(LinkedHashSet::new, 2, 3, 5);}
     *
     * @param collectionFactory The type of collection to create or use.
     * @param elements          The elements to add.
     * @param <C>               The type of Collection to operate on.
     * @param <E>               The type of elements to add.
     * @return The collection with elements added.
     */
    @SafeVarargs
    public static <C extends Collection<E>, E> C asCollection(Supplier<C> collectionFactory, E... elements)
    {
        return Stream.of(elements).collect(Collectors.toCollection(collectionFactory));
    }

    /**
     * Creates or uses a Collection, populates that Collection with elements, and wraps the collection in an appropriate unmodifiable container.
     * <p>
     * Depending on the collection type the unmodifiable container will be in order:
     * - NavigableSet
     * - SortedSet
     * - Set
     * - List
     * - Collection (if nothing else).
     * <p>
     * Example - This command creates an Unmodifiable Set of the first three prime numbers.
     * <p>
     * {@code Set<Integer> intSet = CollectionHelper.asUnmodifiableCollection(LinkedHashSet::new, 2, 3, 5);}
     *
     * @param collectionFactory The type of collection to create or use.
     * @param elements          The elements to add.
     * @param <C>               The type of Collection to operate on.
     * @param <E>               The type of elements to add.
     * @return The collection with elements added.
     */
    @SafeVarargs
    public static <C extends Collection<E>, E> C asUnmodifiableCollection(Supplier<C> collectionFactory, E... elements)
    {
        C collection = asCollection(collectionFactory, elements);

        if (collection instanceof Set)
        {
            if (collection instanceof NavigableSet)
            {
                @SuppressWarnings("unchecked")
                C set = (C) Collections.unmodifiableNavigableSet((NavigableSet<E>) collection);
                return set;
            }
            else if (collection instanceof SortedSet)
            {
                @SuppressWarnings("unchecked")
                C set = (C) Collections.unmodifiableSortedSet((SortedSet<E>) collection);
                return set;
            }
            else
            {
                @SuppressWarnings("unchecked")
                C set = (C) Collections.unmodifiableSet((Set<E>) collection);
                return set;
            }
        }
        else if (collection instanceof List)
        {
            @SuppressWarnings("unchecked")
            C list = (C) Collections.unmodifiableList((List<E>) collection);
            return list;
        }
        else
        {
            @SuppressWarnings("unchecked")
            C c = (C) Collections.unmodifiableCollection(collection);
            return c;
        }
    }

    /**
     * Provides an easy way to create a populated List.
     * <p>
     * List will either be an empty list or an array backed list. The elements can be changed, but not added or removed.
     *
     * @param elements The elements to add.
     * @param <E>      The type of elements.
     * @return An empty list or array backed list.
     */
    @SafeVarargs
    public static <E> List<E> asFixedList(E... elements)
    {
        switch (elements.length)
        {
            case 0:
                return Collections.emptyList();
            /* Collections.singletonList is totally immutable, we want to be able to change the elements.
            case 1:
                return Collections.singletonList(elements[0]);*/
            default:
                return Arrays.asList(elements);
        }
    }

    /**
     * Combines all elements in a Collection to a String. The order will be the natural iteration order of the Collection (if there is one).
     *
     * @param glue   Delimiter placed between elements in the return string.
     * @param pieces Collection to implode.
     * @param <E>    Element generic.
     * @return String with the elements in pieces delimited by glue.
     */
    public static <E> String implode(String glue, Collection<E> pieces)
    {
        if (pieces == null)
        {
            return null;
        }

        StringBuilder builder = new StringBuilder();
        boolean firstPiece = true;

        for (E piece : pieces)
        {
            if (firstPiece)
            {
                firstPiece = false;
            }
            else
            {
                builder.append(glue);
            }

            builder.append(piece);
        }

        return builder.toString();
    }

    /**
     * Combines all elements in an Array to a String. The order will be the Array's order.
     *
     * @param glue   Delimiter placed between elements in the return string.
     * @param pieces Array to implode.
     * @param <E>    Element generic.
     * @return String with the elements in pieces delimited by glue.
     */
    @SafeVarargs
    public static <E> String implode(String glue, E... pieces)
    {
        return implode(glue, Arrays.asList(pieces));
    }

    /**
     * Breaks String into tokens by delimiter and places results into a Collection.
     *
     * @param collectionFactory The type of collection to create or use.
     * @param elementType       Element object for the type of elements to create. The type must have a String c'tor.
     * @param delimiter         String used to tokenize the input String.
     * @param string            Input String.
     * @param <C>               The type of Collection to operate on.
     * @param <E>               Element generic.
     * @return collection populated with tokens from string.
     * @throws IllegalArgumentException If elementType doesn't have a String c'tor.
     */
    public static <C extends Collection<E>, E> Collection<E> explode(Supplier<C> collectionFactory, Class<E> elementType, String delimiter, String string)
    {
        if (string == null)
        {
            return null;
        }

        Collection<E> collection = collectionFactory.get();

        String[] tokens = string.split(Pattern.quote(delimiter));

        for (String token : tokens)
        {
            E element;

            Constructor<E> eCtor;
            try
            {
                eCtor = elementType.getDeclaredConstructor(String.class);
            }
            catch (ReflectiveOperationException e)
            {
                // We should not end up here as long as elementType has c'tor with String parameter. Runtime exception being thrown.
                throw new IllegalArgumentException(String.format("%s should have String c'tor.", elementType.toString()), e);
            }

            try
            {
                element = eCtor.newInstance(token);
            }
            catch (ReflectiveOperationException e)
            {
                throw new IllegalArgumentException(String.format("Can't convert (%s) to %s.", token, elementType.getSimpleName()), e);
            }

            collection.add(element);
        }

        return collection;
    }

    /**
     * Breaks a String into an enum Collection.
     *
     * @param collectionFactory The type of collection to create or use.
     * @param elementType       Enum element type.
     * @param delimiter         String used to tokenize the input String
     * @param string            Input String.
     * @param <C>               The type of Collection to operate on.
     * @param <E>               Element generic.
     * @return collection populated with tokens from string.
     */
    public static <C extends Collection<E>, E extends Enum<E>> Collection<E> explodeToEnum(Supplier<C> collectionFactory, Class<E> elementType, String delimiter, String string)
    {
        if (string == null)
        {
            return null;
        }

        Collection<E> collection = collectionFactory.get();

        String[] tokens = string.split(Pattern.quote(delimiter));

        for (String token : tokens)
        {
            E element = E.valueOf(elementType, token);

            collection.add(element);
        }

        return collection;
    }

    /**
     * Convenience method for explode without as much typing.
     * <p>
     * If you want to explode a String to a {@code Set<String>}, then this method can be used.
     * <p>
     * If set is not empty then new tokens will be appended.
     *
     * @param setFactory The type of set to create or use.
     * @param delimiter  String used to tokenize the input String.
     * @param string     Input String.
     * @param <S>        The type of Set to operate on.
     * @return set populated with tokens from string.
     */
    public static <S extends Set<String>> Set<String> explodeToStringSet(Supplier<S> setFactory, String delimiter, String string)
    {
        return (Set<String>) explode(setFactory, String.class, delimiter, string);
    }

    /**
     * Convenience method for explode without as much typing.
     * <p>
     * If you want to explode a String to a {@code List<String>}, then this method can be used.
     * <p>
     * If list is not empty then new tokens will be appended.
     *
     * @param listFactory The type of list to create or use.
     * @param delimiter   String used to tokenize the input String.
     * @param string      Input String.
     * @param <L>         The type of List to operate on.
     * @return list populated with tokens from string.
     */
    public static <L extends List<String>> List<String> explodeToStringList(Supplier<L> listFactory, String delimiter, String string)
    {
        return (List<String>) explode(listFactory, String.class, delimiter, string);
    }

    /**
     * Convenience method for explode without as much typing.
     * <p>
     * If you want to explode a String to a String Array, then this method can be used.
     *
     * @param delimiter String used to tokenize the input String
     * @param string    Input String.
     * @return String[] containing the tokens from the input String delimited by delimiter.
     */
    public static String[] explodeToStringArray(String delimiter, String string)
    {
        return (String[]) explodeToArray(String.class, delimiter, string);
    }

    /**
     * Convenience method for explode without as much typing.
     * <p>
     * If you want to explode a String to a generic Array, then this method can be used.
     *
     * @param arrayType Class object for the type of Array to create.
     * @param delimiter String used to tokenize the input String.
     * @param string    Input String.
     * @param <T>       Array type generic.
     * @return Object[] containing the tokens from the input String delimited by delimiter. The Object[] can be downcast to arrayType.
     * @throws IllegalArgumentException If arrayType doesn't have a String c'tor.
     */
    public static <T> Object[] explodeToArray(Class<T> arrayType, String delimiter, String string)
    {
        if (string == null)
        {
            return null;
        }

        Collection<T> array = explode(ArrayList::new, arrayType, delimiter, string);
        return array.toArray((Object[]) Array.newInstance(arrayType, array.size()));
    }

    /**
     * Convenience method for testing if two lists have the same members, but non necessarily the same order.
     * <p>
     * WARNING: This method is destructive to b, expecting b to be modifiable.
     *
     * @param a   The first List.
     * @param b   The second List. Must be modifiable and OK to alter.
     * @param <E> The list Type,
     * @return true if equivalent.
     */
    public static <E> boolean equalsNoOrder(List<E> a, List<E> b)
    {
        if (a.size() != b.size())
        {
            return false;
        }

        for (E e : a)
        {
            if (!b.remove(e))
            {
                return false;
            }
        }

        return b.isEmpty();
    }

    /**
     * Convenience method for transforming a collection that may contain sub-collections into a "flattened" collection without sub-collections.
     * <p>
     * The values in the sub-collections are added to the flattened collection as they are encountered while iterating through the original collection.
     *
     * @param value         The original collection or a non-collection value to be turned into a singleton Collection.
     * @param valueRemapper Optional custom method to remap values as they are added to the flattened Collection.
     * @return A flattened collection. If value is not a collection (or null) then a singleton collection is returned.
     * If value is a List then a List is returned. If value is a Set then a LinkedHashSet is returned.
     */
    public static Collection<?> flattenCollections(Object value, Function<Object, Object> valueRemapper)
    {
        Collection<?> values;
        boolean asSet = false;
        if (value == null)
        {
            values = Collections.singleton(null);
        }
        else if (value instanceof Collection<?>)
        {
            values = (Collection<?>) value;
            asSet = values instanceof Set<?>;
        }
        else if (value.getClass().isArray())
        {
            values = Arrays.asList((Object[]) value);
        }
        else
        {
            values = Collections.singleton(value);
        }

        if (values.isEmpty()) // nothing to flatten
        {
            return values;
        }
        else if (values.size() == 1)
        {
            Object singleValue = values.iterator().next();
            if (singleValue == null || !(singleValue instanceof Collection<?>) && !values.getClass().isArray()) // nothing to flatten
            {
                if (valueRemapper != null)
                {
                    singleValue = valueRemapper.apply(singleValue);
                }
                return Collections.singleton(singleValue);
            }
            else if (singleValue instanceof Set<?>)
            {
                asSet = true;
            }
        }

        Collection<Object> flattenedCollection = asSet ? new LinkedHashSet<>() : new ArrayList<>();
        for (Object o : values)
        {
            if (o != null && (o instanceof Collection<?> || o.getClass().isArray()))
            {
                flattenedCollection.addAll(flattenCollections(o, valueRemapper));
            }
            else
            {
                if (valueRemapper != null)
                {
                    o = valueRemapper.apply(o);
                }
                flattenedCollection.add(o);
            }
        }

        return flattenedCollection;
    }

}
