#OVERVIEW

This project contains basic convenience utilities for Collection operations, Jackson JSON parsing, and date/time String conversions using Joda.

Use case samples can be found in the test code, or look at the javadocs for more information.

The project was built using JDK 11, but is backwards compatible to JDK 8.

##Collection Operations

These operations are split into two static method classes. `CollectionHelper` and `MapHelper`.

###CollectionHelper

`CollectionHelper` methods are:

* `asCollection` and `asUnmodifiableCollection`: Creates and initializes a Collection of _any_ subtype. 
Example: 
```
Set<Integer> set = CollectionHelper.asCollection(TreeSet::new, 5, 2, 7, 1, 8);
```
* `asFixedList`: Creates and initializes an array backed List. 
Example: 
```
List<Integer> intList = CollectionHelper.asFixedList(1, 2, 3);
```
* `implode`: Creates a delimited String from from a Collection or value list. 
Example: 
```
List<Integer> intList = CollectionHelper.asFixedList(1, 2, 3);
String imploded = CollectionHelper.implode(",", intList);
assert(imploded.equals("1,2,3");
```
* `explode`, `explodeToEnum`, `explodeToStringSet`, `explodeToStringList`, `explodeToArray`, `explodeToStringArray`:
Creates a Collection or array from a delimited String. 
Example: 
```
Set<Integer> intSet = CollectionHelper.explode(LinkedHashSet::new, Integer.class, ",", "2,3,5");
```
* `equalsNoOrder`: Takes two Lists and checks for equivalence regardless of order. Destroys the second list in the process.
* `flattenCollections`: Takes a Collection with possible sub-collections and flattens it into a Collection without sub-collections. 
Also turns a non-Collection single element into a singleton.

###MapHelper methods

`MapHelper` methods are:

* `asMap` and `asUnmodifiableMap` Creates and initializes a Map of _any_ type. 
Example: 
```
import static com.ds.util.collections.MapHelper.entry;
...
Map<Integer, String> map = asMap(LinkedHashMap::new,
        entry(2, "B"),
        entry(1, "A"),
        entry(3, "C")
);
```
* `flip` and `unmodifiableFlip` Creates a new Map with flipped key and value pairs from an original Map.
Example:
```
import static com.ds.util.collections.MapHelper.entry;
...
Map<Integer, String> map = asMap(LinkedHashMap::new,
        entry(2, "B"),
        entry(1, "A"),
        entry(3, "C")
);
Map<String, Integer> flippedMap = flip(LinkedHashMap::new, map);
```
* `entry` Creates a `Map.Entry` value that can contain null keys and values (unlike Java 9's `Map.entry()`).
Example:
```
import static com.ds.util.collections.MapHelper.entry;
...
Map.Entry<Integer, String> entry = entry(2, "B");
```

##JacksonHelper

`JacksonHelper` provides convenient access to two statically loaded `ObjectMappers`.
`JacksonHelper.DEFAULT` is for default formatting and `JacksonHelper.PRETTY_DEFAULT` is for "pretty" human readability with newlines.

The `ObjectMappers` also contain serialization and de-serialization for the 5 Joda date/time objects: `Instant`, `DateTime`, `LocalDate`, `LocalTime`, `LocalDateTime`.

Finally, the `JacksonHelper` contains two static convenience methods for stringification: `stringify` and `prettyStringify`.

## Date/Time String Conversions

`JodaHelper` contains methods for manipulating date/time Strings through Joda:

* `addTime`: Creates a new date String with time added from an original date String.
Example:
```
String newDate = JodaHelper.addTime("2016-03-31", Period.months(1), -1); // subtract 1 month.
assert(newDate.equals("2016-02-29"));
```
* `converDate`: Creates a new date String in a different format than an original date String.
Example:
```
String newDate = JodaHelper.convertDate("2001-02-03", JodaHelper.STANDARD_DATE_FORMAT, JodaHelper.US_DATE_FORMAT);
assertThat(newDate).isEqualTo("02/03/2001");
```
* `interpretPartialTime`: Creates a full date String from a partial date String. 
The interpretation depends on upper and lower "boundaries" so that a range for the the partial date can be expressed.

__NOTE: This method currently only works with the standard date format "yyyy-MM-dd". 
Times and other formats have not been implemented yet.__

Example:
```
String partialDate = "2006-02";
String lowerDate = JodaHelper.interpretPartialTime(partialDate, JodaHelper.TimeGranularity.DAY, JodaHelper.TimeBoundary.LOWER);
assert(lowerDate.equals("2006-02-01"));
String upperInclusiveDate = JodaHelper.interpretPartialTime(partialDate, JodaHelper.TimeGranularity.DAY, JodaHelper.TimeBoundary.UPPER);
assert(upperInclusiveDate.equals("2006-02-28"));
String upperExclusiveDate = JodaHelper.interpretPartialTime(partialDate, JodaHelper.TimeGranularity.DAY, JodaHelper.TimeBoundary.UPPER_EXCLUDED);
assert(upperExclusiveDate.equals("2006-03-01"));
```
