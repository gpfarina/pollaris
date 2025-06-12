package com.pollaris.event;

import java.nio.file.Path;
import java.time.Instant;
import java.util.Map;

import org.jetbrains.annotations.NotNull;
/**
 * Generic interface for Event.
 */
public interface Event {
    @NotNull Instant timestamp();
    @NotNull Map<String, String> metadata();
    @NotNull Path location();
}