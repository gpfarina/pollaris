package com.pollaris.poller;

import java.nio.file.Path;
import java.util.List;

import com.pollaris.event.Event;
/**
 * A generic interface for pollers. 
 * Besides adders/getters method the only important method this interface defines is
 * the poll() method: a poller's essence is to poll.
 */
public interface Poller {
    List<Event> poll(); 
    PollerId getId();
    void addLocation(Path location);
    void removeLocation(Path location);
    List<Path> locations();
}