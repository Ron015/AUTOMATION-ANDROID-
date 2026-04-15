package com.example.automation.model;

import android.graphics.Rect;

import org.json.JSONException;
import org.json.JSONObject;

public class SelectorSpec {
    public String text;
    public String id;
    public String desc;
    public String className;
    public String packageName;
    public Integer index;
    public String regex;
    public Rect bounds;

    public static SelectorSpec fromJson(JSONObject json) throws JSONException {
        SelectorSpec spec = new SelectorSpec();
        spec.text = json.optString("text", null);
        spec.id = json.optString("id", null);
        spec.desc = json.optString("desc", null);
        spec.className = json.optString("class", null);
        spec.packageName = json.optString("package", null);
        if (json.has("index")) {
            spec.index = json.getInt("index");
        }
        spec.regex = json.optString("regex", null);
        if (json.has("bounds")) {
            JSONObject bounds = json.getJSONObject("bounds");
            spec.bounds = new Rect(
                    bounds.optInt("left"),
                    bounds.optInt("top"),
                    bounds.optInt("right"),
                    bounds.optInt("bottom")
            );
        }
        return spec;
    }
}
