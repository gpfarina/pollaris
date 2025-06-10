package com.pollaris.core;

import java.nio.file.Path;
import java.time.Instant;
import java.util.Map;

public interface Event {
    Instant timestamp();
    Map<String, String> metadata();
    Path location();
}