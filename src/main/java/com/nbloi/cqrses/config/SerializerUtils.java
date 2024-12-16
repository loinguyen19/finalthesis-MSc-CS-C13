package com.nbloi.cqrses.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class SerializerUtils {

    private static final ObjectMapper objectMapper = JsonMapper.builder()
            .addModule(new ParameterNamesModule())
            .addModule(new Jdk8Module())
            .addModule(new JavaTimeModule())
            .build();

    public static <T> T deserializeFromJsonBytes(final String payload, final Class<T> valueType) {
        try {
            return objectMapper.readValue(payload, valueType);
        } catch (IOException e) {
            throw new RuntimeException(String.format("Failed to deserialize payload into type %s", valueType), e);
        }
    }
}
