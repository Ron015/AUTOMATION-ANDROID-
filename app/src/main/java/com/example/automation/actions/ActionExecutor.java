package com.example.automation.actions;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.graphics.Path;
import android.os.Bundle;
import android.view.accessibility.AccessibilityNodeInfo;

import com.example.automation.model.AutomationStep;

public class ActionExecutor {

    public boolean executeStep(AccessibilityService service, AutomationStep step, AccessibilityNodeInfo node) {
        if (step == null || step.action == null) {
            return false;
        }

        switch (step.action) {
            case "click":
                return node != null && node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            case "long_click":
                return node != null && node.performAction(AccessibilityNodeInfo.ACTION_LONG_CLICK);
            case "input":
                return inputText(node, step.value);
            case "scroll":
                return node != null && node.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
            case "swipe":
                return swipe(service, step);
            case "wait":
                return true;
            default:
                return false;
        }
    }

    private boolean inputText(AccessibilityNodeInfo node, String value) {
        if (node == null || value == null) {
            return false;
        }
        Bundle args = new Bundle();
        args.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, value);
        return node.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, args);
    }

    private boolean swipe(AccessibilityService service, AutomationStep step) {
        if (service == null) {
            return false;
        }
        int duration = step.duration != null ? step.duration : 400;
        Path path = new Path();
        path.moveTo(700, 1400);
        path.lineTo(700, 500);
        GestureDescription.StrokeDescription stroke = new GestureDescription.StrokeDescription(path, 0, duration);
        GestureDescription gesture = new GestureDescription.Builder().addStroke(stroke).build();
        return service.dispatchGesture(gesture, null, null);
    }
}
