package com.example.automation.util;

import android.content.Context;
import android.content.SharedPreferences;

public class ScriptRepository {
    private static final String PREF = "automation_scripts";
    private static final String KEY_ACTIVE = "active_script";

    public static void saveScript(Context context, String json) {
        SharedPreferences preferences = context.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        preferences.edit().putString(KEY_ACTIVE, json).apply();
    }

    public static String loadScript(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        return preferences.getString(KEY_ACTIVE, defaultScript());
    }

    public static String defaultScript() {
        return "{\n" +
                "  \"steps\": [\n" +
                "    {\n" +
                "      \"action\": \"click\",\n" +
                "      \"find\": [\n" +
                "        { \"id\": \"com.app:id/login_btn\" },\n" +
                "        { \"text\": \"Login\" },\n" +
                "        { \"desc\": \"login button\" }\n" +
                "      ],\n" +
                "      \"delay_before\": 800,\n" +
                "      \"delay_after\": 1200,\n" +
                "      \"retry\": { \"max_attempts\": 3, \"delay\": 1000, \"on_fail\": \"retry\" }\n" +
                "    },\n" +
                "    {\n" +
                "      \"action\": \"input\",\n" +
                "      \"id\": \"com.app:id/username\",\n" +
                "      \"value\": \"user123\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"action\": \"input\",\n" +
                "      \"id\": \"com.app:id/password\",\n" +
                "      \"value\": \"pass123\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"action\": \"click\",\n" +
                "      \"text\": \"Submit\",\n" +
                "      \"wait_for\": { \"text\": \"Submit\", \"timeout\": 5000, \"interval\": 300 }\n" +
                "    }\n" +
                "  ]\n" +
                "}";
    }
}
