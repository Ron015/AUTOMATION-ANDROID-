package com.example.automation.parser;

import com.example.automation.model.AutomationScript;
import com.example.automation.model.AutomationStep;
import com.example.automation.model.ConditionSpec;
import com.example.automation.model.DelayConfig;
import com.example.automation.model.LoopSpec;
import com.example.automation.model.RetryPolicy;
import com.example.automation.model.SelectorSpec;
import com.example.automation.model.WaitCondition;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ScriptParser {

    public AutomationScript parse(String jsonText) throws JSONException {
        JSONObject root = new JSONObject(jsonText);
        AutomationScript script = new AutomationScript();
        JSONArray steps = root.optJSONArray("steps");
        if (steps == null) {
            return script;
        }
        for (int i = 0; i < steps.length(); i++) {
            script.steps.add(parseStep(steps.getJSONObject(i)));
        }
        return script;
    }

    private AutomationStep parseStep(JSONObject json) throws JSONException {
        AutomationStep step = new AutomationStep();
        step.action = json.optString("action", "");
        step.value = json.optString("value", null);
        if (json.has("duration")) {
            step.duration = json.getInt("duration");
        }

        step.selector = SelectorSpec.fromJson(json);
        JSONArray fallback = json.optJSONArray("find");
        if (fallback != null) {
            for (int i = 0; i < fallback.length(); i++) {
                step.fallbackSelectors.add(SelectorSpec.fromJson(fallback.getJSONObject(i)));
            }
        }
        JSONArray pathArray = json.optJSONArray("path");
        if (pathArray != null) {
            for (int i = 0; i < pathArray.length(); i++) {
                step.path.add(SelectorSpec.fromJson(pathArray.getJSONObject(i)));
            }
        }

        step.delayConfig = parseDelay(json);
        step.retryPolicy = parseRetry(json.optJSONObject("retry"));
        step.condition = parseCondition(json.optJSONObject("if"));
        step.loopSpec = parseLoop(json.optJSONObject("loop"));
        return step;
    }

    private DelayConfig parseDelay(JSONObject json) throws JSONException {
        DelayConfig delayConfig = new DelayConfig();
        delayConfig.delay = json.optLong("delay", 0);
        delayConfig.delayBefore = json.optLong("delay_before", 0);
        delayConfig.delayAfter = json.optLong("delay_after", 0);

        JSONObject random = json.optJSONObject("delay_random");
        if (random != null) {
            delayConfig.randomMin = random.optLong("min");
            delayConfig.randomMax = random.optLong("max");
        }

        delayConfig.waitFor = parseWaitCondition(json.optJSONObject("wait_for"), false);
        delayConfig.waitUntilGone = parseWaitCondition(json.optJSONObject("wait_until_gone"), true);
        return delayConfig;
    }

    private WaitCondition parseWaitCondition(JSONObject json, boolean gone) throws JSONException {
        if (json == null) {
            return null;
        }
        WaitCondition condition = new WaitCondition();
        condition.selector = SelectorSpec.fromJson(json);
        condition.timeout = json.optLong("timeout", 5000);
        condition.interval = json.optLong("interval", 300);
        condition.waitUntilGone = gone;
        return condition;
    }

    private RetryPolicy parseRetry(JSONObject json) {
        RetryPolicy policy = new RetryPolicy();
        if (json == null) {
            return policy;
        }
        policy.maxAttempts = json.optInt("max_attempts", 1);
        policy.delay = json.optLong("delay", 0);
        policy.onFail = json.optString("on_fail", "stop");
        policy.untilFound = json.optBoolean("until_found", false);
        policy.timeout = json.optLong("timeout", 0);
        return policy;
    }

    private ConditionSpec parseCondition(JSONObject json) throws JSONException {
        if (json == null) {
            return null;
        }
        ConditionSpec condition = new ConditionSpec();
        if (json.has("exists")) {
            condition.exists = SelectorSpec.fromJson(json.getJSONObject("exists"));
        }
        if (json.has("not_exists")) {
            condition.notExists = SelectorSpec.fromJson(json.getJSONObject("not_exists"));
        }
        JSONArray thenSteps = json.optJSONArray("then");
        if (thenSteps != null) {
            for (int i = 0; i < thenSteps.length(); i++) {
                condition.thenSteps.add(parseStep(thenSteps.getJSONObject(i)));
            }
        }
        JSONArray elseSteps = json.optJSONArray("else");
        if (elseSteps != null) {
            for (int i = 0; i < elseSteps.length(); i++) {
                condition.elseSteps.add(parseStep(elseSteps.getJSONObject(i)));
            }
        }
        return condition;
    }

    private LoopSpec parseLoop(JSONObject json) throws JSONException {
        if (json == null) {
            return null;
        }
        LoopSpec loop = new LoopSpec();
        loop.count = json.optInt("count", 1);
        JSONArray steps = json.optJSONArray("steps");
        if (steps != null) {
            for (int i = 0; i < steps.length(); i++) {
                loop.steps.add(parseStep(steps.getJSONObject(i)));
            }
        }
        return loop;
    }
}
