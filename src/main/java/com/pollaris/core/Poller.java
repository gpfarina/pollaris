package com.pollaris.core;

import java.util.List;

public interface Poller {
    /**
     * Performs a polling operation.
     *
     * @return a list of new events that occured since the last call.
     *         The list may be empty if no new events are detected.
     */
    List<Event> poll();
}