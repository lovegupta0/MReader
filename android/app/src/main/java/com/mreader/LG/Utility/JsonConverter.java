// com/mreader/LG/Utility/JsonConverter.java
package com.mreader.LG.Utility;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.util.List;

public class JsonConverter {
    private static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .enable(SerializationFeature.INDENT_OUTPUT);

    // existing methods …
    public static String listToJson(List<?> list) throws Exception {
        if (list == null) return "[]";
        return objectMapper.writeValueAsString(list);
    }

    public static String listToJsonSafe(List<?> list) {
        try { return listToJson(list); } catch (Exception e) { return "[]"; }
    }

    /** Any object -> JSON (null => null; never returns "") */
    public static String objToJsonSafe(Object obj) {
        if (obj == null) return null;   // <— important: write SQL NULL, not ""
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            return null;                // also store NULL on failure
        }
    }

    /** JSON -> object (null / "" / "null" => null) */
    public static <T> T jsonToObj(String json, Class<T> cls) {
        if (json == null) return null;
        String s = json.trim();
        if (s.isEmpty() || "null".equalsIgnoreCase(s) || "\"\"".equals(s)) return null;
        try {
            return objectMapper.readValue(s, cls);
        } catch (Exception e) {
            return null;
        }
    }
}
