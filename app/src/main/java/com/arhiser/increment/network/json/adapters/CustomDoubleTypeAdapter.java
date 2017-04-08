package com.arhiser.increment.network.json.adapters;

import com.crashlytics.android.Crashlytics;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

/**
 * TypeAdapter to ensure that we always get the consistent result when deserializing Double field
 */
public class CustomDoubleTypeAdapter extends TypeAdapter<Double>{
    @Override
    public void write(JsonWriter out, Double value) throws IOException {
        if (value == null) {
            out.nullValue();
            return;
        }
        out.value(value);
    }

    @Override
    public Double read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return Double.valueOf(0.0);
        }
        String stringValue = in.nextString();
        try {
            Double value = Double.valueOf(stringValue);
            return value;
        } catch (NumberFormatException e) {
            Crashlytics.logException(e);
            return Double.valueOf(-1.0);
        }
    }
}
