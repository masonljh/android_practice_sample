package com.antdev.covidbot.network.dto;

import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Converter;

final class CustomResponseBodyConverterFactory<T> implements Converter<ResponseBody, T> {
    private final Gson mGson;
    private final TypeAdapter<T> mAdapter;

    public CustomResponseBodyConverterFactory(Gson gson, TypeAdapter<T> adapter) {
        mGson = gson;
        mAdapter = adapter;
    }

    @Nullable
    @Override
    public T convert(ResponseBody value) throws IOException {
        try {
            JsonReader jsonReader = mGson.newJsonReader(value.charStream());
            T result = mAdapter.read(jsonReader);
            if (jsonReader.peek() != JsonToken.END_DOCUMENT) {
                throw new JsonIOException("JSON document was not fully consumed.");
            }
            return result;
        } catch (IOException e) {
            if (value.contentLength() == 0) {
                return null;
            }

            throw e;
        } finally {
            value.close();
        }
    }
}
