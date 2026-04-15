package com.example.automation.model;

import java.util.ArrayList;
import java.util.List;

public class AutomationStep {
    public String action;
    public SelectorSpec selector;
    public List<SelectorSpec> fallbackSelectors = new ArrayList<>();
    public List<SelectorSpec> path = new ArrayList<>();
    public String value;
    public Integer duration;
    public DelayConfig delayConfig = new DelayConfig();
    public RetryPolicy retryPolicy = new RetryPolicy();
    public ConditionSpec condition;
    public LoopSpec loopSpec;
}
