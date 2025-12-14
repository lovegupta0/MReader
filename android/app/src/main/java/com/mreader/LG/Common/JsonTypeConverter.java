package com.mreader.LG.Common;

import androidx.room.TypeConverter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mreader.LG.Utility.JsonConverter;

import java.util.ArrayList;
import java.util.List;

/**
 * TypeConverter using JsonConverter (Jackson-based)
 * to store List<String> or List<Object> in Room as JSON.
 */
public class JsonTypeConverter {

    private static final ObjectMapper mapper = new ObjectMapper();

    @TypeConverter
    public static String fromStringList(List<String> list) {
        return JsonConverter.listToJsonSafe(list);
    }

    @TypeConverter
    public static List<String> toStringList(String json) {
        try {
            if (json == null || json.isEmpty()) return new ArrayList<>();
            return mapper.readValue(json, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}