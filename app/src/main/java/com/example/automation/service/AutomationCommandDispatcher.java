package com.example.automation.service;

import android.content.Context;
import android.content.Intent;

public final class AutomationCommandDispatcher {
    private AutomationCommandDispatcher() {
    }

    public static void dispatch(Context context, String action) {
        AutomationAccessibilityService service = AutomationAccessibilityService.getInstance();
        if (service != null) {
            Intent intent = new Intent(action);
            context.sendBroadcast(intent);
        } else {
            Intent intent = new Intent(action);
            context.sendBroadcast(intent);
        }
    }
}
