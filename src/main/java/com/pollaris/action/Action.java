package com.pollaris.action;
import org.jetbrains.annotations.NotNull;

import com.pollaris.event.Event;
/**
 * Generic interface for Actions.
 */
public interface Action {
    // We make the action potentially depend on the event 
    // even though for now we won't exploit this possibility
    // as the actions are defined in the config file and do not depend
    // on the event, only on its occurence
    // For testing reasons we could acutally use the event though
    public Result execute(@NotNull Event e);
}
