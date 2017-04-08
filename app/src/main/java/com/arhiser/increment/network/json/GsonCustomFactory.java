package com.arhiser.increment.network.json;

import com.arhiser.increment.network.json.adapters.CustomDoubleTypeAdapter;
import com.arhiser.increment.network.json.adapters.CustomIntegerTypeAdapter;
import com.arhiser.increment.network.json.adapters.CustomStringTypeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

public class GsonCustomFactory {
	public static Gson getCustomGson() {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Integer.class, new CustomIntegerTypeAdapter());
        builder.registerTypeAdapter(Double.class, new CustomDoubleTypeAdapter());
        builder.registerTypeAdapter(String.class, new CustomStringTypeAdapter());
        builder.registerTypeAdapter(Date.class, new DateSerializer());
        builder.excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC);
        return builder.create();
    }


    private static final String[] DATE_FORMATS = new String[] {
            "yyyy-MM-dd'T'HH:mm:ss",
            "yyyy-MM-dd"
    };


    private static class DateSerializer implements JsonDeserializer<Date> {

        @Override
        public Date deserialize(JsonElement jsonElement, Type typeOF,
                                JsonDeserializationContext context) throws JsonParseException {
            for (String format : DATE_FORMATS) {
                try {
                    return new SimpleDateFormat(format, Locale.US).parse(jsonElement.getAsString());
                } catch (ParseException e) {
                }
            }
            throw new JsonParseException("Unparseable date: \"" + jsonElement.getAsString()
                    + "\". Supported formats: " + Arrays.toString(DATE_FORMATS));
        }
    }
}
