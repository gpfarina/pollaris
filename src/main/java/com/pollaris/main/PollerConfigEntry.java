package com.pollaris.main;

import java.util.Map;

public class PollerConfigEntry {
    public String name;
    public int frequencyMs;
    public BackendConfigEntry backend;
    public Map<String, String> actions;
}
