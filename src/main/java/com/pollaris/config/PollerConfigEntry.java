package com.pollaris.config;

import java.nio.file.Path;
import java.util.Map;

public class PollerConfigEntry {
    public String name;
    public int frequencyMs;
    public BackendConfigEntry backend;
    public Map<Path, String> actions;
}
