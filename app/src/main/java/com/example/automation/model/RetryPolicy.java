package com.example.automation.model;

public class RetryPolicy {
    public int maxAttempts = 1;
    public long delay = 0;
    public String onFail = "stop";
    public boolean untilFound = false;
    public long timeout = 0;
}
