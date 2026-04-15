package com.example.automation.engine;

import android.accessibilityservice.AccessibilityService;
import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;

import com.example.automation.actions.ActionExecutor;
import com.example.automation.model.AutomationScript;
import com.example.automation.model.AutomationStep;
import com.example.automation.model.ConditionSpec;
import com.example.automation.model.SelectorSpec;
import com.example.automation.selector.SelectorEngine;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AutomationEngine {
    private static final String TAG = "AutomationEngine";

    private final SelectorEngine selectorEngine = new SelectorEngine();
    private final ActionExecutor actionExecutor = new ActionExecutor();
    private final DelayManager delayManager = new DelayManager();
    private final RetryManager retryManager = new RetryManager();
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private volatile boolean running;
    private volatile boolean paused;

    public void runScript(AccessibilityService service, AutomationScript script) {
        if (script == null) {
            return;
        }
        running = true;
        paused = false;
        executorService.submit(() -> executeSteps(service, script.steps));
    }

    public void stop() {
        running = false;
        paused = false;
    }

    public void pause() {
        paused = true;
    }

    public void resume() {
        paused = false;
    }

    private void executeSteps(AccessibilityService service, List<AutomationStep> steps) {
        for (AutomationStep step : steps) {
            if (!running) {
                return;
            }
            waitIfPaused();

            if (step.condition != null) {
                executeCondition(service, step.condition);
                continue;
            }

            if (step.loopSpec != null) {
                for (int i = 0; i < step.loopSpec.count && running; i++) {
                    executeSteps(service, step.loopSpec.steps);
                }
                continue;
            }

            boolean success = retryManager.runWithRetry(step.retryPolicy, delayManager,
                    () -> executeSingleStep(service, step));

            if (!success) {
                Log.w(TAG, "Step failed: " + step.action);
                if (retryManager.shouldSkipOnFailure(step.retryPolicy)) {
                    continue;
                }
                if (retryManager.shouldStopOnFailure(step.retryPolicy)) {
                    stop();
                    return;
                }
            }
        }
    }

    private boolean executeSingleStep(AccessibilityService service, AutomationStep step) {
        delayManager.applyBefore(step.delayConfig);
        if (!delayManager.awaitCondition(step.delayConfig.waitFor,
                () -> exists(service, step.delayConfig.waitFor != null ? step.delayConfig.waitFor.selector : null))) {
            return false;
        }

        AccessibilityNodeInfo root = service.getRootInActiveWindow();
        AccessibilityNodeInfo node = selectorEngine.findNode(root, step.selector, step.fallbackSelectors, step.path);
        boolean executed = actionExecutor.executeStep(service, step, node);

        boolean goneSatisfied = delayManager.awaitCondition(step.delayConfig.waitUntilGone,
                () -> exists(service, step.delayConfig.waitUntilGone != null ? step.delayConfig.waitUntilGone.selector : null));

        delayManager.applyAfter(step.delayConfig);
        return executed && goneSatisfied;
    }

    private void executeCondition(AccessibilityService service, ConditionSpec condition) {
        boolean valid = true;
        if (condition.exists != null) {
            valid = exists(service, condition.exists);
        }
        if (condition.notExists != null) {
            valid = valid && !exists(service, condition.notExists);
        }
        if (valid) {
            executeSteps(service, condition.thenSteps);
        } else {
            executeSteps(service, condition.elseSteps);
        }
    }

    private boolean exists(AccessibilityService service, SelectorSpec selector) {
        if (selector == null) {
            return true;
        }
        AccessibilityNodeInfo root = service.getRootInActiveWindow();
        return selectorEngine.findFirst(root, selector) != null;
    }

    private void waitIfPaused() {
        while (paused && running) {
            delayManager.sleep(200);
        }
    }
}
