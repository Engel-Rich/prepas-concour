package com.mutrix.prepa.cors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

public final class MetadataMapper {

    private MetadataMapper() {
    }

    public static String mapToJson(Object metadata) {
        if (metadata == null) {
            return null;
        }
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(metadata);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting metadata to JSON", e);
        }
    }

    public static <T> T mapFromJson(String json, Class<T> clazz) {
        if (json == null || json.isEmpty()) {
            return null;
        }
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(json, new TypeReference<T>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting JSON to metadata", e);
        }
    }

    public  static Map<String, Object> mapFromJsonToMap(String json) {
        if (json == null || json.isEmpty()) {
            return null;
        }
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(json,new TypeReference<Map<String, Object>>(){});
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting JSON to Map<String, Object>", e);
        }
    }
}
