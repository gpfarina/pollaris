package com.pollaris.action;

import org.jetbrains.annotations.NotNull;

import com.pollaris.event.Event;
import com.pollaris.fs.Result;
/*
* A concrete implementation of the Action interface. This action only logs the event occurence.
*/
public class LogAction implements Action{

    @Override
    public @NotNull Result execute(@NotNull Event e) {
        String toPrint = String.format("Event has occurred: <%s, %s, %s>", e.location().toString(), e.timestamp().toString(), e.metadata().toString());
        System.out.println(toPrint);
        return Result.SUCCESS;
    }
    
}
