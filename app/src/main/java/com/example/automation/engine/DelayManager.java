package com.example.automation.engine;

import android.os.SystemClock;

import com.example.automation.model.DelayConfig;
import com.example.automation.model.WaitCondition;

import java.util.Random;
import java.util.function.BooleanSupplier;

public class DelayManager {
    private final Random random = new Random();

    public void applyBefore(DelayConfig delayConfig) {
        if (delayConfig == null) {
            return;
        }
        sleep(delayConfig.delay);
        sleep(delayConfig.delayBefore);
        sleepRandom(delayConfig);
    }

    public void applyAfter(DelayConfig delayConfig) {
        if (delayConfig == null) {
            return;
        }
        sleep(delayConfig.delayAfter);
    }

    public boolean awaitCondition(WaitCondition condition, BooleanSupplier existsSupplier) {
        if (condition == null) {
            return true;
        }
        long start = SystemClock.uptimeMillis();
        while (SystemClock.uptimeMillis() - start < condition.timeout) {
            boolean exists = existsSupplier.getAsBoolean();
            if (condition.waitUntilGone) {
                if (!exists) {
                    return true;
                }
            } else if (exists) {
                return true;
            }
            sleep(condition.interval);
        }
        return false;
    }

    private void sleepRandom(DelayConfig delayConfig) {
        if (delayConfig.randomMin == null || delayConfig.randomMax == null) {
            return;
        }
        long min = delayConfig.randomMin;
        long max = delayConfig.randomMax;
        if (max < min) {
            long temp = max;
            max = min;
            min = temp;
        }
        long value = min + random.nextInt((int) (max - min + 1));
        sleep(value);
    }

    public void sleep(long millis) {
        if (millis > 0) {
            SystemClock.sleep(millis);
        }
    }
}
