package com.mreader.LG.Common;

import androidx.room.TypeConverter;

import com.mreader.LG.DataModel.SettingDataModel.Advanced;
import com.mreader.LG.DataModel.SettingDataModel.Download;
import com.mreader.LG.DataModel.SettingDataModel.General;
import com.mreader.LG.DataModel.SettingDataModel.Performance;
import com.mreader.LG.DataModel.SettingDataModel.Privacy;
import com.mreader.LG.Utility.JsonConverter;

public class SettingsTypeConverters {

    @TypeConverter public static String toJson(General v) { return JsonConverter.objToJsonSafe(v); }
    @TypeConverter public static General fromJsonGeneral(String s) { return JsonConverter.jsonToObj(s, General.class); }

    @TypeConverter public static String toJson(Privacy v) { return JsonConverter.objToJsonSafe(v); }
    @TypeConverter public static Privacy fromJsonPrivacy(String s) { return JsonConverter.jsonToObj(s, Privacy.class); }

    @TypeConverter public static String toJson(Performance v) { return JsonConverter.objToJsonSafe(v); }
    @TypeConverter public static Performance fromJsonPerformance(String s) { return JsonConverter.jsonToObj(s, Performance.class); }

    @TypeConverter public static String toJson(Download v) { return JsonConverter.objToJsonSafe(v); }
    @TypeConverter public static Download fromJsonDownload(String s) { return JsonConverter.jsonToObj(s, Download.class); }

    @TypeConverter public static String toJson(Advanced v) { return JsonConverter.objToJsonSafe(v); }
    @TypeConverter public static Advanced fromJsonAdvanced(String s) { return JsonConverter.jsonToObj(s, Advanced.class); }
}

