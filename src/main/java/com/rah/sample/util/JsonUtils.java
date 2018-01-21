package com.rah.sample.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public final class JsonUtils {
    private static ObjectMapper objectMapper = new ObjectMapper();

    private JsonUtils() {
    }

    public static <T> T readValue(Response response, Class<T> clazz) throws IOException {
        ByteArrayInputStream inputStream = (ByteArrayInputStream)response.body().asInputStream();
        if(inputStream.available() > 0) {
            return objectMapper.readValue(inputStream, clazz);
        }
        return null;
    }
}
