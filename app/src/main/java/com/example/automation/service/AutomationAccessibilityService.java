package com.example.automation.service;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import com.example.automation.engine.AutomationEngine;
import com.example.automation.model.AutomationScript;
import com.example.automation.parser.ScriptParser;
import com.example.automation.recorder.InteractionRecorder;
import com.example.automation.util.ScriptRepository;

import org.json.JSONException;

public class AutomationAccessibilityService extends AccessibilityService {
    public static final String ACTION_START = "com.example.automation.START";
    public static final String ACTION_STOP = "com.example.automation.STOP";
    public static final String ACTION_PAUSE = "com.example.automation.PAUSE";
    public static final String ACTION_RESUME = "com.example.automation.RESUME";
    public static final String ACTION_RECORD_START = "com.example.automation.RECORD_START";
    public static final String ACTION_RECORD_STOP = "com.example.automation.RECORD_STOP";

    private final AutomationEngine automationEngine = new AutomationEngine();
    private final ScriptParser parser = new ScriptParser();
    private final InteractionRecorder recorder = new InteractionRecorder();

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        recorder.onEvent(event);
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Log.i("AutomationService", "Accessibility service connected.");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null || intent.getAction() == null) {
            return START_STICKY;
        }
        String action = intent.getAction();
        switch (action) {
            case ACTION_START:
                startAutomation();
                break;
            case ACTION_STOP:
                automationEngine.stop();
                break;
            case ACTION_PAUSE:
                automationEngine.pause();
                break;
            case ACTION_RESUME:
                automationEngine.resume();
                break;
            case ACTION_RECORD_START:
                recorder.start();
                break;
            case ACTION_RECORD_STOP:
                recorder.stop();
                ScriptRepository.saveScript(this, recorder.exportJson());
                break;
            default:
                break;
        }
        return START_STICKY;
    }

    private void startAutomation() {
        String json = ScriptRepository.loadScript(this);
        try {
            AutomationScript script = parser.parse(json);
            automationEngine.runScript(this, script);
        } catch (JSONException e) {
            Log.e("AutomationService", "Invalid JSON script", e);
        }
    }

    @Override
    public void onInterrupt() {
        automationEngine.stop();
    }
}
