package com.example.automation.recorder;

import android.view.accessibility.AccessibilityEvent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class InteractionRecorder {
    private final JSONArray steps = new JSONArray();
    private boolean recording;

    public void start() {
        recording = true;
    }

    public void stop() {
        recording = false;
    }

    public void onEvent(AccessibilityEvent event) {
        if (!recording || event == null) {
            return;
        }
        int type = event.getEventType();
        try {
            if (type == AccessibilityEvent.TYPE_VIEW_CLICKED) {
                addClick(event);
            } else if (type == AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED) {
                addInput(event);
            } else if (type == AccessibilityEvent.TYPE_VIEW_SCROLLED) {
                addScroll();
            }
        } catch (JSONException ignored) {
        }
    }

    private void addClick(AccessibilityEvent event) throws JSONException {
        JSONObject step = new JSONObject();
        step.put("action", "click");
        if (event.getText() != null && !event.getText().isEmpty()) {
            step.put("text", String.valueOf(event.getText().get(0)));
        }
        steps.put(step);
    }

    private void addInput(AccessibilityEvent event) throws JSONException {
        JSONObject step = new JSONObject();
        step.put("action", "input");
        if (event.getText() != null && !event.getText().isEmpty()) {
            step.put("value", String.valueOf(event.getText().get(0)));
        }
        steps.put(step);
    }

    private void addScroll() throws JSONException {
        JSONObject step = new JSONObject();
        step.put("action", "scroll");
        steps.put(step);
    }

    public String exportJson() {
        JSONObject root = new JSONObject();
        try {
            root.put("steps", steps);
        } catch (JSONException ignored) {
        }
        return root.toString();
    }
}
