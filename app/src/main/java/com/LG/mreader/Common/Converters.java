package com.LG.mreader.Common;

// Or your preferred package for converters

import androidx.room.TypeConverter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Converters {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @TypeConverter
    public static LocalDateTime toLocalDateTime(String value) {
        return value == null ? null : LocalDateTime.parse(value, formatter);
    }

    @TypeConverter
    public static String fromLocalDateTime(LocalDateTime dateTime) {
        return dateTime == null ? null : dateTime.format(formatter);
    }
}

