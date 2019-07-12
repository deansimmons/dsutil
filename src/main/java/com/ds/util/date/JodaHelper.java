package com.ds.util.date;

import com.ds.util.collections.MapHelper;
import org.joda.time.LocalDateTime;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import static java.util.Map.entry;

/**
 * Convenience classes for converting Date Strings between formats using Joda.
 * <p>
 * This class also interprets "partial dates".
 */
public class JodaHelper
{
    private JodaHelper()
    {
        // static members only
    }

    public static final String STANDARD_DATE_FORMAT = "yyyy-MM-dd";
    public static final String FILE_DATE_TIME_FORMAT = "yyyy-MM-dd HH-mm-ss"; // colons aren't a great choice for filenames
    public static final String STANDARD_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String US_DATE_FORMAT = "MM/dd/yyyy";
    public static final String STANDARD_MILLISECOND_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";

    public static final Map<String, String> PARTIAL_TO_STANDARD_CONVERSIONS = MapHelper.asUnmodifiableMap(LinkedHashMap::new,
            entry("yy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm:ss"),
            entry("MM/dd/yy HH:mm:ss", "yyyy-MM-dd HH:mm:ss"),

            entry("yy-MM-dd HH:mm", "yyyy-MM-dd HH:mm"),
            entry("MM/dd/yy HH:mm", "yyyy-MM-dd HH:mm"),

            entry("yy-MM-dd HH", "yyyy-MM-dd HH"),
            entry("MM/dd/yy HH", "yyyy-MM-dd HH"),

            entry("yy-MM-dd", "yyyy-MM-dd"),
            entry("MM/dd/yy", "yyyy-MM-dd"),

            entry("yy-MM", "yyyy-MM"),
            entry("MM/yy", "yyyy-MM"),

            entry("yy", "yyyy"),
            entry("yyyy", "yyyy")
    );

    /**
     * Adds time to a STANDARD_DATE_FORMAT date string and returns the new time.
     *
     * @param date   Input date in STANDARD_DATE_FORMAT.
     * @param period Joda period for time addition. Periods below Day granularity won't work here.
     * @param amount Amount of time to add.
     * @return The new date string.
     */
    public static String addTime(String date, Period period, int amount)
    {
        return addTime(date, STANDARD_DATE_FORMAT, period, amount);
    }

    /**
     * Adds time to date string of the supplied format and returns a new date string in the same format.
     *
     * @param date   Input date/time.
     * @param format Format of the input and output date string.
     * @param period Joda period for time addition.
     * @param amount Amount of time to add.
     * @return The new date/time string.
     */
    public static String addTime(String date, String format, Period period, int amount)
    {
        DateTimeFormatter formatter = DateTimeFormat.forPattern(format);
        LocalDateTime dateTime = LocalDateTime.parse(date, formatter);
        LocalDateTime result = dateTime.plus(period.multipliedBy(amount));
        return result.toString(formatter);
    }

    /**
     * Converts a date string from one format to another.
     *
     * @param date      Input date/time.
     * @param inFormat  The input format.
     * @param outFormat The output format.
     * @return Date/time string in the new format.
     */
    public static String convertDate(String date, String inFormat, String outFormat)
    {
        date = padOneDigitYear(date);
        DateTimeFormatter formatter = DateTimeFormat.forPattern(inFormat);
        LocalDateTime dateTime = LocalDateTime.parse(date, formatter);
        return dateTime.toString(outFormat);
    }

    /**
     * Converts a date string from a Collection of possible input formats to a single output format.
     *
     * @param date      Input date/time.
     * @param inFormats The Collection of possible input formats. The first valid format found while iterating through the Collection will be used.
     * @param outFormat The output format.
     * @return Date/time string in the new format.
     * @throws IllegalArgumentException If no valid input formats are found.
     */
    public static String convertDate(String date, Collection<String> inFormats, String outFormat)
    {
        IllegalArgumentException noMatches = null;
        for (String inFormat : inFormats)
        {
            try
            {
                return convertDate(date, inFormat, outFormat);
            }
            catch (IllegalArgumentException e)
            {
                noMatches = e;
            }
        }

        assert noMatches != null;
        throw noMatches;
    }


    /**
     * Converts a date using a Map for possible input and output formats.
     * <p>
     * The keys to the map represent the possible input format, and the corresponding value represents the output format for that key.
     *
     * The formats map is iterated over until a matching key input format is found. Order is important, and an earlier key can mask a later one.
     *
     * @param date    Input date/time String.
     * @param formats Map containing the input formats (keys) and output formats (values). This map is iterated over until
     *                the first matching input is found. A LinkedHashMap would be a good choice for ordering.
     * @return Date/time String in the output format determined by formats Map.
     * @throws IllegalArgumentException If no match is found.
     */
    public static String convertDate(String date, Map<String, String> formats) throws IllegalArgumentException
    {
        IllegalArgumentException noMatches = null;

        for (String inFormat : formats.keySet())
        {
            String outFormat = formats.get(inFormat);
            try
            {
                String convertedDate = convertDate(date, inFormat, outFormat);
                if (Objects.equals(convertedDate, INVALID_DATE))
                {
                    throw new IllegalArgumentException(String.format("Unparseable date: \"%s\"", date));
                }
                return convertedDate;
            }
            catch (IllegalArgumentException e)
            {
                noMatches = e;
            }
        }

        throw noMatches;
    }

    /**
     * Simple method to determine that a date follows the STANDARD_DATE_FORMAT.
     * <p>
     * Throws an exception if the date doesn't follow the format.
     *
     * @param date The date to check.
     */
    public static void validateDate(String date)
    {
        validateDate(date, STANDARD_DATE_FORMAT);
    }

    /**
     * Simple method to determine that a date follows a given format.
     * <p>
     * Throws an exception if the date doesn't follow the format.
     *
     * @param date The date to check.
     * @param format The expected date format.
     */
    public static void validateDate(String date, String format)
    {
        DateTimeFormatter formatter = DateTimeFormat.forPattern(format);
        LocalDateTime.parse(date, formatter);
    }

    private static final Map<Period, String[]> PARTIAL_DATES = MapHelper.asUnmodifiableMap(LinkedHashMap::new,
            entry(Period.days(1), new String[]{"yyyy-MM-dd", "yyyy-MM-dd"}),
            entry(Period.months(1), new String[]{"yyyy-MM", "yyyy-MM-01"}),
            entry(Period.years(1), new String[]{"yyyy", "yyyy-01-01"})
    );

    // todo wait for this to be needed
    //"yyyy-MM-dd HH:mm:ss"

    /**
     * Controls how to partial dates are interpreted.
     */
    public enum TimeBoundary
    {
        /**
         * Interpret as the lower boundary. For DAY granularity a year String "2018" would be interpreted as "2018-01-01".
         */
        LOWER,

        /**
         * Interpret as the upper boundary. For DAY granularity a year String "2018" would be interpreted as "2018-12-31".
         */
        UPPER,

        /**
         * Interpret as the upper boundary that is just outside of the partial date for the given granularity. For DAY granularity a year String "2018" would be interpreted as "2019-01-01".
         */
        UPPER_EXCLUDED
    }

    /**
     * Controls how to partial dates are interpreted.
     * <p>
     * todo: for now DAY is the only thing implemented. Once we need to interpret full time strings, then SECONDS will be added (and others as needed).
     */
    public enum TimeGranularity
    {
        /**
         * Interpret partial date strings to the day. Year or year and month strings will be turned into full year month and day strings.
         */
        DAY

        // todo MONTH, SECONDS, MILLISECONDS?
    }

    private static final String INVALID_DATE = "0001-01-01";

    /**
     * Method to turn a partial date/time String into a full date/time String according to the supplied granularity and boundary.
     * <p>
     * NOTE: currently only DAY granularity and partial dates in the STANDARD_DATE_FORMAT are supported.
     *
     * @param date        date The input partial date.
     * @param granularity The time granularity used for interpretations.
     * @param boundary    Used to determine whether to interpret partials as the lower end of the granularity or the upper end.
     * @return An interpreted date that fulfills the granularity stipulation using the boundary.
     */
    public static String interpretPartialDate(String date, TimeGranularity granularity, TimeBoundary boundary)
    {
        if (date == null)
        {
            return null;
        }

        Map<Period, String[]> partials;
        String finalOutFormat;
        Period finalTimeGranularity;

        switch (granularity)
        {
            case DAY:
                partials = PARTIAL_DATES;
                finalOutFormat = STANDARD_DATE_FORMAT;
                finalTimeGranularity = Period.days(1);
                break;
            default:
                throw new IllegalArgumentException(String.format("Unsupported time granularity (%s).", granularity));
        }

        IllegalArgumentException noMatches = null;
        for (Period partialGranularity : partials.keySet())
        {
            String[] formatArr = partials.get(partialGranularity);
            String inFormat = formatArr[0];
            String outFormat = formatArr[1];

            String lowerDate;
            try
            {
                lowerDate = convertDate(date, inFormat, outFormat);
            }
            catch (IllegalArgumentException e)
            {
                noMatches = e;
                continue;
            }

            if (Objects.equals(lowerDate, INVALID_DATE))
            {
                throw new IllegalArgumentException(String.format("Unparseable date: \"%s\"", date));
            }

            if (boundary == TimeBoundary.LOWER)
            {
                return lowerDate;
            }

            String upperExcludedDate = addTime(lowerDate, finalOutFormat, partialGranularity, 1);
            if (boundary == TimeBoundary.UPPER_EXCLUDED)
            {
                return upperExcludedDate;
            }

            // boundary == TimeBoundary.UPPER
            return addTime(upperExcludedDate, finalOutFormat, finalTimeGranularity, -1);
        }

        throw noMatches;
    }

    /**
     * Adds a preceding 0 to date Strings where the year has only 1 digit.
     *
     * Date parsers don't seem to work too well with 1 character years.
     *
     * @param date The date string with the year first with a - format (Y-M-D), or year last with a / format (M/D/Y).
     * @return A date string with a preceding 0 if the year has only one digit.
     */
    private static String padOneDigitYear(String date)
    {
        if (date.contains("-")) // assuming Y-M-D format. year is supposed to be first
        {
            int delimIdx = date.indexOf('-');
            String year = padYear(date.substring(0, delimIdx));
            date = String.format("%s%s", year, date.substring(delimIdx));
        }
        if (date.contains("/")) // assuming M/D/Y format. year is supposed to be last
        {
            int yearIdx = date.lastIndexOf('/') + 1;
            if (yearIdx < date.length()) // at least one character beyond the end of the last '/'
            {
                if (yearIdx + 1 == date.length() || !Character.isDigit(date.charAt(yearIdx + 1))) // exactly one char beyond the last '/' or the next char after that is a ' '.
                {
                    String year = padYear(date.substring(yearIdx, yearIdx + 1));
                    StringBuilder dateBuilder = new StringBuilder();
                    dateBuilder.append(date, 0, yearIdx);
                    dateBuilder.append(year);
                    if (yearIdx + 1 < date.length())
                    {
                        dateBuilder.append(date.substring(yearIdx + 1));
                    }
                    date = dateBuilder.toString();
                }
            }
        }
        else
        {
            date = padYear(date);
        }

        return date;
    }

    private static String padYear(String year)
    {
        if (year.length() == 1) // we just want to add a preceding 0 to years with one digit
        {
            try
            {
                Integer.parseInt(year);
                year = String.format("0%s", year);
            }
            catch (NumberFormatException e)
            {
                // nothing we can process
            }
        }

        return year;
    }
}
