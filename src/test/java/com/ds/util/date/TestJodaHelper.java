package com.ds.util.date;

import com.ds.util.collections.CollectionHelper;
import com.ds.util.collections.MapHelper;
import org.joda.time.Period;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.ds.util.collections.MapHelper.entry;
import static org.assertj.core.api.Assertions.*;

@RunWith(MockitoJUnitRunner.class)
public class TestJodaHelper
{
    public TestJodaHelper()
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
    public void testPartial()
    {
        String date;
        String interpretedDate;

        date = "2006-02-01";
        interpretedDate = JodaHelper.interpretPartialTime(date, JodaHelper.TimeGranularity.DAY, JodaHelper.TimeBoundary.LOWER);
        assertThat(interpretedDate).isEqualTo("2006-02-01");
        interpretedDate = JodaHelper.interpretPartialTime(date, JodaHelper.TimeGranularity.DAY, JodaHelper.TimeBoundary.UPPER);
        assertThat(interpretedDate).isEqualTo("2006-02-01");
        interpretedDate = JodaHelper.interpretPartialTime(date, JodaHelper.TimeGranularity.DAY, JodaHelper.TimeBoundary.UPPER_EXCLUDED);
        assertThat(interpretedDate).isEqualTo("2006-02-02");

        date = "2006-02";
        interpretedDate = JodaHelper.interpretPartialTime(date, JodaHelper.TimeGranularity.DAY, JodaHelper.TimeBoundary.LOWER);
        assertThat(interpretedDate).isEqualTo("2006-02-01");
        interpretedDate = JodaHelper.interpretPartialTime(date, JodaHelper.TimeGranularity.DAY, JodaHelper.TimeBoundary.UPPER);
        assertThat(interpretedDate).isEqualTo("2006-02-28");
        interpretedDate = JodaHelper.interpretPartialTime(date, JodaHelper.TimeGranularity.DAY, JodaHelper.TimeBoundary.UPPER_EXCLUDED);
        assertThat(interpretedDate).isEqualTo("2006-03-01");

        date = "2006";
        interpretedDate = JodaHelper.interpretPartialTime(date, JodaHelper.TimeGranularity.DAY, JodaHelper.TimeBoundary.LOWER);
        assertThat(interpretedDate).isEqualTo("2006-01-01");
        interpretedDate = JodaHelper.interpretPartialTime(date, JodaHelper.TimeGranularity.DAY, JodaHelper.TimeBoundary.UPPER);
        assertThat(interpretedDate).isEqualTo("2006-12-31");
        interpretedDate = JodaHelper.interpretPartialTime(date, JodaHelper.TimeGranularity.DAY, JodaHelper.TimeBoundary.UPPER_EXCLUDED);
        assertThat(interpretedDate).isEqualTo("2007-01-01");
    }

    // order is important. One format can mask another and Joda has a liberal acceptance for input formats
    Map<String, String> FORMATS = MapHelper.asUnmodifiableMap(LinkedHashMap::new,
            entry("yy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm:ss"),
            entry("MM/dd/yy HH:mm:ss", "yyyy-MM-dd HH:mm:ss"),

            entry("yy-MM-dd", "yyyy-MM-dd"),
            entry("MM/dd/yy", "yyyy-MM-dd")
    );

    @Test
    public void testConvertDate()
    {
        String newDate;

        // single digit date test as well
        newDate = JodaHelper.convertDate("1-2-3", "yy-MM-dd", JodaHelper.US_DATE_FORMAT);
        assertThat(newDate).isEqualTo("02/03/2001");

        newDate = JodaHelper.convertDate("03/04/2017", CollectionHelper.asFixedList("MM/dd/yy", JodaHelper.US_DATE_FORMAT), JodaHelper.STANDARD_DATE_FORMAT);
        assertThat(newDate).isEqualTo("2017-03-04");

        newDate = JodaHelper.convertDate("03/04/2017", FORMATS);
        assertThat(newDate).isEqualTo("2017-03-04");

        newDate = JodaHelper.convertDate("03/04/17", FORMATS);
        assertThat(newDate).isEqualTo("2017-03-04");

        newDate = JodaHelper.convertDate("03/04/2017 1:3:14", FORMATS);
        assertThat(newDate).isEqualTo("2017-03-04 01:03:14");

        newDate = JodaHelper.convertDate("03/04/17 1:3:14", FORMATS);
        assertThat(newDate).isEqualTo("2017-03-04 01:03:14");


    }

    @Test
    public void testAddTime()
    {
        String newDate;

        newDate = JodaHelper.addTime("2016-03-31", Period.months(1), -1);
        assertThat(newDate).isEqualTo("2016-02-29");

        newDate = JodaHelper.addTime("3/31/2016", JodaHelper.US_DATE_FORMAT, Period.months(1), -1);
        assertThat(newDate).isEqualTo("02/29/2016");
    }

    @Test
    public void testValidateDate()
    {
        JodaHelper.validateDate("2016-01-12");

        Throwable throwable = catchThrowable(() -> JodaHelper.validateDate("2016-13-12"));
        assertThat(throwable).isInstanceOf(Throwable.class);

        JodaHelper.validateDate("01/12/2016", JodaHelper.US_DATE_FORMAT);

        throwable = catchThrowable(() -> JodaHelper.validateDate("2016-13-12", JodaHelper.US_DATE_FORMAT));
        assertThat(throwable).isInstanceOf(Throwable.class);
    }
}
