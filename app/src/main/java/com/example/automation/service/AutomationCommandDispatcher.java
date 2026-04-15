package com.example.automation.service;

import android.content.Context;
import android.content.Intent;

public final class AutomationCommandDispatcher {
    private AutomationCommandDispatcher() {
    }

    public static void dispatch(Context context, String action) {
        Intent intent = new Intent(action);
        context.sendBroadcast(intent);
    }
}
