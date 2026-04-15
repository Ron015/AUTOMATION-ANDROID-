package com.example.automation.ui;

public class StepItem {
    public String action;
    public String selector;
    public String value;

    public StepItem(String action, String selector, String value) {
        this.action = action;
        this.selector = selector;
        this.value = value;
    }
}
