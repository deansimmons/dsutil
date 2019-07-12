package com.ds.util.jackson;

import com.fasterxml.jackson.core.type.TypeReference;
import org.joda.time.*;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.util.Objects;

import static org.assertj.core.api.Assertions.*;

@RunWith(MockitoJUnitRunner.class)
public class TestJacksonHelper
{
    public TestJacksonHelper()
    {
        //LOG.info("ctor");
    }

    static class JodaTypes
    {
        public Instant instant;
        public DateTime dateTime;
        public LocalDate localDate;
        public LocalTime localTime;
        public LocalDateTime localDateTime;

        public JodaTypes()
        {
        }

        public JodaTypes(String instant, String dateTime, String localDate, String localTime, String localDateTime)
        {
            this.instant = Instant.parse(instant);
            this.dateTime = DateTime.parse(dateTime);
            this.localDate = LocalDate.parse(localDate);
            this.localTime = LocalTime.parse(localTime);
            this.localDateTime = LocalDateTime.parse(localDateTime, JacksonHelper.DATE_TIME_FORMATTER);
        }

        @Override
        public int hashCode()
        {
            // not needed - supposed to override hashCode with equals
            return super.hashCode();
        }

        @Override
        public boolean equals(Object obj)
        {
            if (obj == this)
            {
                return true;
            }

            if (!(obj instanceof JodaTypes))
            {
                return false;
            }

            JodaTypes other = (JodaTypes) obj;
            return Objects.equals(this.instant, other.instant) && Objects.equals(this.dateTime, other.dateTime) && Objects.equals(this.localDate, other.localDate) && Objects.equals(this.localTime, other.localTime) && Objects.equals(this.localDateTime, other.localDateTime);
        }
    }

    private static final JodaTypes JODA_TYPES = new JodaTypes(
            "2017-07-06T04:37:30Z",
            "2016-06-05T21:37:30.855-07:00",
            "2016-02-03", "13:04:55",
            "2016-02-03 13:04:55");

    private static final JodaTypes NULL_JODA_TYPES = new JodaTypes();

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
    public void testJodaDateTypes() throws IOException
    {
        String json = JacksonHelper.PRETTY_DEFAULT_JSON_MAPPER.writeValueAsString(JODA_TYPES);
        JodaTypes fromJson = JacksonHelper.PRETTY_DEFAULT_JSON_MAPPER.readValue(json, new TypeReference<JodaTypes>()
        {
        });
        assertThat(fromJson).isEqualTo(JODA_TYPES);

        json = JacksonHelper.PRETTY_DEFAULT_JSON_MAPPER.writeValueAsString(NULL_JODA_TYPES);
        fromJson = JacksonHelper.PRETTY_DEFAULT_JSON_MAPPER.readValue(json, new TypeReference<JodaTypes>()
        {
        });
        assertThat(fromJson).isEqualTo(NULL_JODA_TYPES);
    }
}
