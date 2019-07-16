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
public final class CollectionHelper
{
    private CollectionHelper()
    {
        // static member only class
    }

    /**
     * Creates and initializes <i>any</i> Collection type.
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
     * Creates and initializes <i>any</i> unmodifiable Collection type.
     * <p>
     * <b>WARNING</b> This method returns the unmodifiable container - therefore the return type will be the closest matching interface between the collectionFactory and Collections' unmodifiable methods.
     * As an example a call to produce an unmodifiable TreeSet would not return a TreeSet, instead it would return a NavigableSet from Collections.unmodifiableNavigableSet(). See the return section for return types.
     * <p>
     * Example - This command creates an Unmodifiable Set of the first three prime numbers.
     * <p>
     * {@code Set<Integer> intSet = CollectionHelper.asUnmodifiableCollection(LinkedHashSet::new, 2, 3, 5);}
     *
     * @param collectionFactory The type of collection to create or use.
     * @param elements          The elements to add.
     * @param <C>               The type of Collection to operate on.
     * @param <E>               The type of elements to add.
     * @return An unmodifiable Collection with elements added. The actual type will be the interface most closely matching the collectionFactory:
     * <ul>
     * <li>{@link NavigableSet}</li>
     * <li>{@link SortedSet}</li>
     * <li>{@link Set}</li>
     * <li>{@link List}</li>
     * <li>{@link Collection} (if nothing else)</li>
     * </ul>
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
     * Creates and initializes an array backed List.
     * <p>
     * The elements can be changed, but not added or removed.
     *
     * @param elements The elements to add.
     * @param <E>      The type of elements.
     * @return An empty list or array backed list.
     */
    @SafeVarargs
    public static <E> List<E> asFixedList(E... elements)
    {
        return elements.length != 0 ? Arrays.asList(elements) : Collections.emptyList();
    }

    /**
     * Creates a delimited String from from a Collection.
     * <p>
     * The order will be the natural iteration order of the Collection.
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
     * Creates a delimited String from from a value list. .
     * <p>
     * The order will be the elements order.
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
     * Creates a Collection of <i>any</i> type from a delimited String.
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
    public static <C extends Collection<E>, E> C explode(Supplier<C> collectionFactory, Class<E> elementType, String delimiter, String string)
    {
        if (string == null)
        {
            return null;
        }

        C collection = collectionFactory.get();

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
     * Creates an enum Collection of <i>any</i> type from a delimited String.
     * <p>
     * The tokens in the delimited String are expected to relate to the enum through Enum.valueOf().
     *
     * @param collectionFactory The type of collection to create or use.
     * @param elementType       Enum element type.
     * @param delimiter         String used to tokenize the input String
     * @param string            Input String.
     * @param <C>               The type of Collection to operate on.
     * @param <E>               Element generic.
     * @return collection populated with tokens from string.
     */
    public static <C extends Collection<E>, E extends Enum<E>> C explodeToEnum(Supplier<C> collectionFactory, Class<E> elementType, String delimiter, String string)
    {
        if (string == null)
        {
            return null;
        }

        C collection = collectionFactory.get();

        String[] tokens = string.split(Pattern.quote(delimiter));

        for (String token : tokens)
        {
            E element = E.valueOf(elementType, token);

            collection.add(element);
        }

        return collection;
    }

    /**
     * Creates a String Set from a delimited String.
     *
     * @param setFactory The type of set to create or use.
     * @param delimiter  String used to tokenize the input String.
     * @param string     Input String.
     * @param <S>        The type of Set to operate on.
     * @return set populated with tokens from string.
     */
    public static <S extends Set<String>> Set<String> explodeToStringSet(Supplier<S> setFactory, String delimiter, String string)
    {
        return explode(setFactory, String.class, delimiter, string);
    }

    /**
     * Creates a String List from a delimited String.
     *
     * @param listFactory The type of list to create or use.
     * @param delimiter   String used to tokenize the input String.
     * @param string      Input String.
     * @param <L>         The type of List to operate on.
     * @return list populated with tokens from string.
     */
    public static <L extends List<String>> List<String> explodeToStringList(Supplier<L> listFactory, String delimiter, String string)
    {
        return explode(listFactory, String.class, delimiter, string);
    }

    /**
     * Creates an String array from a delimited String.
     *
     * @param delimiter String used to tokenize the input String
     * @param string    Input String.
     * @return String[] containing the tokens from the input String delimited by delimiter.
     */
    public static String[] explodeToStringArray(String delimiter, String string)
    {
        return explodeToArray(String.class, delimiter, string);
    }

    /**
     * Creates an array of <i>any</i> type from a delimited String.
     *
     * @param arrayType Class object for the type of Array to create.
     * @param delimiter String used to tokenize the input String.
     * @param string    Input String.
     * @param <T>       Array type generic.
     * @return Object[] containing the tokens from the input String delimited by delimiter. The Object[] can be downcast to arrayType.
     * @throws IllegalArgumentException If arrayType doesn't have a String c'tor.
     */
    public static <T> T[] explodeToArray(Class<T> arrayType, String delimiter, String string)
    {
        if (string == null)
        {
            return null;
        }

        List<T> list = explode(ArrayList::new, arrayType, delimiter, string);
        @SuppressWarnings("unchecked")
        T[] array = (T[]) Array.newInstance(arrayType, list.size());
        return list.toArray(array);
    }

    /**
     * Tests if two lists have the same members - regardless of order.
     * <p>
     * <b>WARNING:</b> This method is destructive to b, expecting b to be modifiable.
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
     * Takes a Collection with possible sub-collections and flattens it into a Collection without sub-collections.
     * Also turns a non-Collection single element into a singleton.
     * <p>
     * The values in the sub-collections are processed depth-first.
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
