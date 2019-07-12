package com.ds.util.collections;

import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.text.ParseException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

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
        Map<Integer, String> map = MapHelper.asMap(LinkedHashMap::new,
                MapHelper.entry(2, "B"),
                MapHelper.entry(1, "A"),
                MapHelper.entry(3, "C")
        );
        assertThat(map).isInstanceOf(LinkedHashMap.class).containsExactly(MapHelper.entry(2, "B"), MapHelper.entry(1, "A"), MapHelper.entry(3, "C"));

        NavigableMap<Integer, String> navigableMap = MapHelper.asMap(TreeMap::new,
                MapHelper.entry(2, "B"),
                MapHelper.entry(1, "A"),
                MapHelper.entry(3, "C")
        );
        assertThat(navigableMap).isInstanceOf(NavigableMap.class).containsExactly(MapHelper.entry(1, "A"), MapHelper.entry(2, "B"), MapHelper.entry(3, "C"));
    }

    @Test
    public void testFlip()
    {
        Map<Integer, String> map = MapHelper.asMap(LinkedHashMap::new,
                MapHelper.entry(2, "B"),
                MapHelper.entry(1, "A"),
                MapHelper.entry(3, "C")
        );

        Map<String, Integer> flippedMap = MapHelper.flip(LinkedHashMap::new, map);
        assertThat(flippedMap).isInstanceOf(LinkedHashMap.class).containsExactly(MapHelper.entry("B", 2), MapHelper.entry("A", 1), MapHelper.entry("C", 3));

        NavigableMap<String, Integer> unmodFlippedMap = MapHelper.unmodifiableFlip(TreeMap::new, map);
        assertThat(unmodFlippedMap).containsExactly(MapHelper.entry("A", 1), MapHelper.entry("B", 2), MapHelper.entry("C", 3));
    }
}
