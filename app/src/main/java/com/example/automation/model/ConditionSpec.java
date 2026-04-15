package com.example.automation.model;

import java.util.ArrayList;
import java.util.List;

public class ConditionSpec {
    public SelectorSpec exists;
    public SelectorSpec notExists;
    public List<AutomationStep> thenSteps = new ArrayList<>();
    public List<AutomationStep> elseSteps = new ArrayList<>();
}
