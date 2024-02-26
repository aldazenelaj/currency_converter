package com.currency.converter.app.utilities;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;

import java.io.IOException;
import java.util.Map;


public class JsonPropertySourceFactory implements PropertySourceFactory {

    @Override
    public PropertySource<?> createPropertySource(
            String name, EncodedResource resource)
            throws IOException {
        return new MapPropertySource("json-property", (Map<String, Object>) new ObjectMapper().readValue(
                resource.getInputStream(), Map.class).get("api"));
    }
}