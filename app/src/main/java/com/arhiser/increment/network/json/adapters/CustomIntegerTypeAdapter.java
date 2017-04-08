package com.arhiser.increment.network.json.adapters;

import com.crashlytics.android.Crashlytics;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

/**
 * TypeAdapter to ensure that we always get the consistent result when deserializing Integer field
 */
public class CustomIntegerTypeAdapter extends TypeAdapter<Integer> {
    @Override
    public void write(JsonWriter out, Integer value) throws IOException {
        if (value == null) {
            out.nullValue();
            return;
        }
        out.value(value);
    }

    @Override
    public Integer read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return Integer.valueOf(0);
        }
        String stringValue = in.nextString();
        try {
            Integer value = Integer.valueOf(stringValue);
            return value;
        } catch (NumberFormatException e) {
            Crashlytics.logException(e);
            return Integer.valueOf(-1);
        }

    }
}
