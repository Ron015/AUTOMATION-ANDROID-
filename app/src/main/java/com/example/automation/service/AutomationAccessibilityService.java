package com.example.automation.service;

import android.accessibilityservice.AccessibilityService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import androidx.annotation.Nullable;

import com.example.automation.engine.AutomationEngine;
import com.example.automation.model.AutomationScript;
import com.example.automation.parser.ScriptParser;
import com.example.automation.recorder.InteractionRecorder;
import com.example.automation.util.ScriptRepository;

import org.json.JSONException;

public class AutomationAccessibilityService extends AccessibilityService {
    private static final String TAG = "AutomationService";

    private static volatile AutomationAccessibilityService instance;

    private final AutomationEngine automationEngine = new AutomationEngine();
    private final ScriptParser parser = new ScriptParser();
    private final InteractionRecorder recorder = new InteractionRecorder();

    private final BroadcastReceiver commandReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                handleAction(intent.getAction());
            }
        }
    };

    @Nullable
    public static AutomationAccessibilityService getInstance() {
        return instance;
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        recorder.onEvent(event);
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        instance = this;
        registerCommandReceiver();
        Log.i(TAG, "Accessibility service connected and command bus ready.");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            handleAction(intent.getAction());
        }
        return START_STICKY;
    }

    private void registerCommandReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(AutomationCommands.ACTION_START);
        filter.addAction(AutomationCommands.ACTION_STOP);
        filter.addAction(AutomationCommands.ACTION_PAUSE);
        filter.addAction(AutomationCommands.ACTION_RESUME);
        filter.addAction(AutomationCommands.ACTION_RECORD_START);
        filter.addAction(AutomationCommands.ACTION_RECORD_STOP);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(commandReceiver, filter, RECEIVER_NOT_EXPORTED);
        } else {
            registerReceiver(commandReceiver, filter);
        }
    }

    private void handleAction(String action) {
        if (action == null) {
            return;
        }
        switch (action) {
            case AutomationCommands.ACTION_START:
                startAutomation();
                break;
            case AutomationCommands.ACTION_STOP:
                automationEngine.stop();
                break;
            case AutomationCommands.ACTION_PAUSE:
                automationEngine.pause();
                break;
            case AutomationCommands.ACTION_RESUME:
                automationEngine.resume();
                break;
            case AutomationCommands.ACTION_RECORD_START:
                recorder.start();
                break;
            case AutomationCommands.ACTION_RECORD_STOP:
                recorder.stop();
                ScriptRepository.saveScript(this, recorder.exportJson());
                break;
            default:
                break;
        }
    }

    private void startAutomation() {
        String json = ScriptRepository.loadScript(this);
        try {
            AutomationScript script = parser.parse(json);
            automationEngine.runScript(this, script);
        } catch (JSONException e) {
            Log.e(TAG, "Invalid JSON script", e);
        }
    }

    @Override
    public void onInterrupt() {
        automationEngine.stop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        instance = null;
        try {
            unregisterReceiver(commandReceiver);
        } catch (Exception ignored) {
        }
    }
}
