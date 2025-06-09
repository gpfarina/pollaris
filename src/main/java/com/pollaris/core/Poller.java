package com.pollaris.core;

import java.util.List;

public interface Poller {
    /**
     * Performs a polling operation on the configured file system location.
     *
     * @return a list of events representing newly discovered files or relevant file system changes.
     *         The list may be empty if no new events are detected.
     */
    List<Event> poll();
}