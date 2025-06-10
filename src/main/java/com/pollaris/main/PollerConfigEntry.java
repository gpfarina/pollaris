package com.pollaris.main;

import java.util.List;
import java.util.Map;

public class PollerConfigEntry {
    public String name;
    public int frequencyMs;
    public BackendConfigEntry backend;
    public Map<String, List<String>> actions;
}
