package com.pollaris.core;
import com.pollaris.fs.Result;

public interface Action {
    // We make the action potentially depend on the event 
    // even though for now we won't exploit this possibility
    // as the actions are defined in the config file and do not depend
    // on the event, only on its occurence.@interface
    // For testing reasons we could acutally use the event though
    public Result execute(Event e);
}
