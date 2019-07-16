package com.ds.util.collections;

import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

import static com.ds.util.collections.MapHelper.entry;
import static com.ds.util.collections.MapHelper.*;
import static org.assertj.core.api.Assertions.*;

@RunWith(MockitoJUnitRunner.class)
public class TestMapHelper
{
    public TestMapHelper()
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
    public void testMapCreation()
    {
        Map<Integer, String> map = asMap(LinkedHashMap::new,
                entry(2, "B"),
                entry(1, "A"),
                entry(3, "C")
        );
        assertThat(map).isInstanceOf(LinkedHashMap.class).containsExactly(
                entry(2, "B"),
                entry(1, "A"),
                entry(3, "C"));

        NavigableMap<Integer, String> navigableMap = asMap(TreeMap::new,
                entry(2, "B"),
                entry(1, "A"),
                entry(3, "C")
        );
        assertThat(navigableMap).isInstanceOf(NavigableMap.class).containsExactly(entry(1, "A"), entry(2, "B"), entry(3, "C"));
    }

    @Test
    public void testFlip()
    {
        Map<Integer, String> map = asMap(LinkedHashMap::new,
                entry(2, "B"),
                entry(1, "A"),
                entry(3, "C")
        );

        Map<String, Integer> flippedMap = flip(LinkedHashMap::new, map);
        assertThat(flippedMap).isInstanceOf(LinkedHashMap.class).containsExactly(entry("B", 2), entry("A", 1), entry("C", 3));

        NavigableMap<String, Integer> unmodFlippedMap = unmodifiableFlip(TreeMap::new, map);
        assertThat(unmodFlippedMap).containsExactly(entry("A", 1), entry("B", 2), entry("C", 3));
    }
}
