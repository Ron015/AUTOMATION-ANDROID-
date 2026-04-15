package com.example.automation.model;

public class WaitCondition {
    public SelectorSpec selector;
    public long timeout = 5000;
    public long interval = 300;
    public boolean waitUntilGone;
}
