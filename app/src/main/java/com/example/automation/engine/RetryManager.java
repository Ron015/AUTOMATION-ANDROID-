package com.example.automation.engine;

import android.os.SystemClock;

import com.example.automation.model.RetryPolicy;

import java.util.function.BooleanSupplier;

public class RetryManager {

    public boolean runWithRetry(RetryPolicy policy, DelayManager delayManager, BooleanSupplier action) {
        if (policy == null) {
            return action.getAsBoolean();
        }
        int attempts = Math.max(policy.maxAttempts, 1);
        long start = SystemClock.uptimeMillis();

        for (int i = 1; i <= attempts; i++) {
            if (action.getAsBoolean()) {
                return true;
            }
            if (policy.untilFound && policy.timeout > 0 && (SystemClock.uptimeMillis() - start) < policy.timeout) {
                delayManager.sleep(policy.delay);
                attempts++;
                continue;
            }
            if (i < attempts) {
                delayManager.sleep(policy.delay);
            }
        }
        return false;
    }

    public boolean shouldSkipOnFailure(RetryPolicy policy) {
        return policy != null && "skip".equalsIgnoreCase(policy.onFail);
    }

    public boolean shouldStopOnFailure(RetryPolicy policy) {
        return policy == null || "stop".equalsIgnoreCase(policy.onFail);
    }
}
