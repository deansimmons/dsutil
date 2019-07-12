package com.ds.util.collections;

import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.*;

import static org.assertj.core.api.Assertions.*;

@RunWith(MockitoJUnitRunner.class)
public class TestCollectionHelper
{
    public TestCollectionHelper()
    {
        //LOG.info("ctor");
    }

    @BeforeClass
    public static void beforeClass()
    {

    }

    @AfterClass
    public static void afterClass()
    {

    }

    @Before
    public void before()
    {

    }

    @After
    public void after()
    {

    }

    @Test
    public void testCollectionCreation()
    {
        Set<Integer> set = CollectionHelper.asCollection(TreeSet::new, 5, 2, 7, 1, 8);
        assertThat(set).isInstanceOf(TreeSet.class).containsExactly(1, 2, 5, 7, 8);

        set = CollectionHelper.asCollection(LinkedHashSet::new, 5, 2, 7, 1, 8);
        assertThat(set).isInstanceOf(LinkedHashSet.class).containsExactly(5, 2, 7, 1, 8);

        List<Integer> list = CollectionHelper.asCollection(ArrayList::new, 5, 2, 7, 1, 8);
        assertThat(list).isInstanceOf(ArrayList.class).containsExactly(5, 2, 7, 1, 8);

        list = CollectionHelper.asFixedList(5, 2, 7, 1, 8);
        assertThat(list).isInstanceOf(List.class).containsExactly(5, 2, 7, 1, 8);
        list.set(1, 3);
        assertThat(list).isInstanceOf(List.class).containsExactly(5, 3, 7, 1, 8);
        List<Integer> finalList = list;
        Throwable thrown = catchThrowable(() -> finalList.add(4));
        assertThat(thrown).isInstanceOf(UnsupportedOperationException.class);

        NavigableSet<Integer> navigableSet = CollectionHelper.asUnmodifiableCollection(TreeSet::new, 5, 2, 7, 1, 8);
        assertThat(navigableSet).isInstanceOf(NavigableSet.class).containsExactly(1, 2, 5, 7, 8);
        thrown = catchThrowable(() -> navigableSet.add(4));
        assertThat(thrown).isInstanceOf(UnsupportedOperationException.class);

        list = CollectionHelper.asUnmodifiableCollection(ArrayList::new, 5, 2, 7, 1, 8);
        assertThat(list).isInstanceOf(List.class).containsExactly(5, 2, 7, 1, 8);
        List<Integer> finalList1 = list;
        thrown = catchThrowable(() -> finalList1.set(1, 4));
        assertThat(thrown).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    public void testImplode()
    {
        String imploded = CollectionHelper.implode(",", 1, 2, 3);
        assertThat(imploded).isEqualTo("1,2,3");

        List<Integer> intList = CollectionHelper.asFixedList(1, 2, 3);
        CollectionHelper.implode(",", intList);
        assertThat(imploded).isEqualTo("1,2,3");
    }

    private enum Color
    {
        RED,
        BLUE,
        GREEN
    }

    @Test
    public void testExplode()
    {
        Set<Color> colors = (Set<Color>) CollectionHelper.explodeToEnum(LinkedHashSet::new, Color.class, ",", "GREEN,RED");
        assertThat(colors).isInstanceOf(LinkedHashSet.class).containsExactly(Color.GREEN, Color.RED);

        String[] strings = CollectionHelper.explodeToStringArray(",", "GREEN,RED");
        assertThat(strings).containsExactly("GREEN", "RED");

        List<String> stringList = CollectionHelper.explodeToStringList(LinkedList::new, ",", "GREEN,RED");
        assertThat(stringList).isInstanceOf(LinkedList.class).containsExactly("GREEN", "RED");

        Set<String> stringSet = CollectionHelper.explodeToStringSet(TreeSet::new, ",", "GREEN,RED");
        assertThat(stringSet).isInstanceOf(TreeSet.class).containsExactly("GREEN", "RED");

        Integer[] intArr = (Integer[]) CollectionHelper.explodeToArray(Integer.class, ",", "2,3,5");
        assertThat(intArr).containsExactly(2, 3, 5);

        Set<Integer> intSet = (Set<Integer>) CollectionHelper.explode(LinkedHashSet::new, Integer.class, ",", "2,3,5");
        assertThat(intSet).isInstanceOf(LinkedHashSet.class).containsExactly(2, 3, 5);
    }

    @Test
    public void testFlattenCollections()
    {
        List<Object> complexList = CollectionHelper.asFixedList(
                1,
                2,
                3,
                CollectionHelper.asFixedList( // collection within collection
                        "4",
                        "5",
                        CollectionHelper.asFixedList( // and another collection within that
                                "6",
                                "7"
                        ),
                        "8"),
                "9", // will be remapped by custom remapper
                null // nulls are OK
        );
        @SuppressWarnings("unchecked")
        List<Object> flattenedList = (List<Object>) CollectionHelper.flattenCollections(complexList, value -> Objects.equals(value, "9") ? 9 : value);
        assertThat(flattenedList).isInstanceOf(List.class).containsExactly(1, 2, 3, "4", "5", "6", "7", "8", 9, null);

        @SuppressWarnings("unchecked")
        Collection<Object> singleton = (Collection<Object>) CollectionHelper.flattenCollections(1, null);
        assertThat(singleton).isInstanceOf(Collection.class).hasSize(1).containsExactly(1);

        @SuppressWarnings("unchecked")
        Set<Object> empty = (Set<Object>) CollectionHelper.flattenCollections(Collections.emptySet(), null);
        assertThat(empty).isInstanceOf(Set.class).isEmpty();

        @SuppressWarnings("unchecked")
        Collection<Object> nullSingleton = (Collection<Object>) CollectionHelper.flattenCollections(null, null);
        assertThat(nullSingleton).isInstanceOf(Collection.class).hasSize(1).contains((String) null);
    }

    @Test
    public void testEqualsNoOrder()
    {
        // only a can be fixed
        List<Integer> a = CollectionHelper.asUnmodifiableCollection(ArrayList::new, 2, 3, 5, 7, 11, 2, 11);

        // b will be destroyed
        List<Integer> b1 = CollectionHelper.asCollection(ArrayList::new, 2, 2, 3, 5, 7, 11, 11);
        List<Integer> b2 = CollectionHelper.asCollection(ArrayList::new, 2, 2, 3, 5, 7, 11, 13);

        boolean isEqual = CollectionHelper.equalsNoOrder(a, b1);
        assertThat(isEqual).isTrue();

        isEqual = CollectionHelper.equalsNoOrder(a, b2);
        assertThat(isEqual).isFalse();
    }
}
