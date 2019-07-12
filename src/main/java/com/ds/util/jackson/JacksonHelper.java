package com.ds.util.jackson;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.joda.time.*;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.IOException;
import java.text.SimpleDateFormat;

/**
 * Convenience classes for JSON transformations through Jackson.
 */
public class JacksonHelper
{
    /**
     * Default Jackson Object mapper.
     */
    public static final ObjectMapper DEFAULT_JSON_MAPPER = getObjectMapper(Config.DEFAULT);

    /**
     * Pretty printed with newlines and indentation for human readability. Useful for toString overrides.
     */
    public static final ObjectMapper PRETTY_DEFAULT_JSON_MAPPER = getObjectMapper(Config.PRETTY_DEFAULT);

    /**
     * Expected format for LocalTime fields.
     */
    public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormat.forPattern("HH:mm:ss");

    /**
     * Expected format for LocalDate fields.
     */
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormat.forPattern("yyyy-MM-dd");

    /**
     * Expected format for LocalDateTime fields.
     */
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");

    public enum Config
    {
        DEFAULT, PRETTY_DEFAULT
    }

    private JacksonHelper()
    {
        // static members only
    }

    private static ObjectMapper getObjectMapper(Config config)
    {
        ObjectMapper mapper = new ObjectMapper();

        JavaTimeModule timeModule = new JavaTimeModule();
        timeModule.addSerializer(Instant.class, new InstantSerializer());
        timeModule.addSerializer(DateTime.class, new DateTimeSerializer());
        timeModule.addSerializer(LocalDate.class, new LocalDateSerializer());
        timeModule.addSerializer(LocalTime.class, new LocalTimeSerializer());
        timeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer());
        timeModule.addDeserializer(Instant.class, new InstantDeserializer());
        timeModule.addDeserializer(DateTime.class, new DateTimeDeserializer());
        timeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer());
        timeModule.addDeserializer(LocalTime.class, new LocalTimeDeserializer());
        timeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer());
        mapper.registerModule(timeModule);

        // not sure if SimpleModule is needed for non time cases?
        SimpleModule module = new SimpleModule();
        mapper.registerModule(module);

        if (config == Config.PRETTY_DEFAULT)
        {
            mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        }

        mapper.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);

        mapper.getFactory().configure(JsonGenerator.Feature.ESCAPE_NON_ASCII, true);

        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
        mapper.setDateFormat(outputFormat);

        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        mapper.setSerializationInclusion(JsonInclude.Include.ALWAYS);

        return mapper;
    }

    private static class InstantSerializer extends JsonSerializer<Instant>
    {
        @Override
        public void serialize(Instant value, JsonGenerator jgen, SerializerProvider provider)
                throws IOException
        {
            jgen.writeString(value.toString());
        }
    }

    private static class DateTimeSerializer extends JsonSerializer<DateTime>
    {
        @Override
        public void serialize(DateTime value, JsonGenerator jgen, SerializerProvider provider)
                throws IOException
        {
            jgen.writeString(value.toString());
        }
    }

    private static class LocalDateSerializer extends JsonSerializer<LocalDate>
    {
        @Override
        public void serialize(LocalDate value, JsonGenerator jgen, SerializerProvider provider)
                throws IOException
        {
            jgen.writeString(value.toString(DATE_FORMATTER));
        }
    }

    private static class LocalTimeSerializer extends JsonSerializer<LocalTime>
    {
        @Override
        public void serialize(LocalTime value, JsonGenerator jgen, SerializerProvider provider)
                throws IOException
        {
            jgen.writeString(value.toString(TIME_FORMATTER));
        }
    }

    private static class LocalDateTimeSerializer extends JsonSerializer<LocalDateTime>
    {
        @Override
        public void serialize(LocalDateTime value, JsonGenerator jgen, SerializerProvider provider)
                throws IOException
        {
            jgen.writeString(value.toString(DATE_TIME_FORMATTER));
        }
    }

    private static class InstantDeserializer extends JsonDeserializer<Instant>
    {
        @Override
        public Instant deserialize(JsonParser jp, DeserializationContext ctxt)
                throws IOException
        {
            JsonNode node = jp.getCodec().readTree(jp);
            return Instant.parse(node.asText());
        }
    }

    private static class DateTimeDeserializer extends JsonDeserializer<DateTime>
    {
        @Override
        public DateTime deserialize(JsonParser jp, DeserializationContext ctxt)
                throws IOException
        {
            JsonNode node = jp.getCodec().readTree(jp);
            return DateTime.parse(node.asText());
        }
    }

    private static class LocalDateDeserializer extends JsonDeserializer<LocalDate>
    {
        @Override
        public LocalDate deserialize(JsonParser jp, DeserializationContext ctxt)
                throws IOException
        {
            JsonNode node = jp.getCodec().readTree(jp);
            return LocalDate.parse(node.asText());
        }
    }

    private static class LocalTimeDeserializer extends JsonDeserializer<LocalTime>
    {
        @Override
        public LocalTime deserialize(JsonParser jp, DeserializationContext ctxt)
                throws IOException
        {
            JsonNode node = jp.getCodec().readTree(jp);
            return LocalTime.parse(node.asText(), TIME_FORMATTER);
        }
    }

    private static class LocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime>
    {
        @Override
        public LocalDateTime deserialize(JsonParser jp, DeserializationContext ctxt)
                throws IOException
        {
            JsonNode node = jp.getCodec().readTree(jp);
            return LocalDateTime.parse(node.asText(), DATE_TIME_FORMATTER);
        }
    }

    /**
     * Method for easy String conversion of an Object using PRETTY_SET_AS_OBJECT_JSON_MAPPER.
     * <p>
     * Useful for a quick toString() implementation.
     *
     * @param obj The Object to convert to a JSON String.
     * @return Human readable JSON string representation of Object.
     */
    static public String prettyStringify(Object obj)
    {
        return stringify(obj, JacksonHelper.PRETTY_DEFAULT_JSON_MAPPER);
    }

    /**
     * Method for easy String conversion of an Object.
     * <p>
     * Useful for a quick toString() implementation.
     *
     * @param obj    The Object to convert to a JSON String.
     * @param mapper The ObjectMapper to use for stringification.
     * @return JSON string representation of Object.
     */
    static public String stringify(Object obj, ObjectMapper mapper)
    {
        try
        {
            return mapper.writeValueAsString(obj);
        }
        catch (JsonProcessingException e)
        {
            throw new RuntimeException(e);
        }
    }
}
