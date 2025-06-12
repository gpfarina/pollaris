package com.pollaris.event;

import java.nio.file.Path;
import java.time.Instant;
import java.util.Map;

/**
 * Generic interface for Event.
 */
public interface Event {
    Instant timestamp();
    Map<String, String> metadata();
    Path location();
}