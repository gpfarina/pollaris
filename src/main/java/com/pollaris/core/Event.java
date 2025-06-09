package com.pollaris.core;

import java.nio.file.Path;
import java.time.Instant;
import java.util.Map;

public interface Event {
    /**
     * The path or logical key (e.g., full S3 key or local file path) of the new file.
     */
    Path getLocation();

    /**
     * The exact timestamp when the event was detected.
     */
    Instant getTimestamp();

    /**
     * Optional metadata (e.g., file size, content type, S3 tags) associated with this file.
     */
    Map<String, String> getMetadata();
}