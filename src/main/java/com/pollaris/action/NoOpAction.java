package com.pollaris.action;

import com.pollaris.event.Event;
/*
* A concrete implementation of the Action interface. This action does nothing, succesfully.
*/
public class NoOpAction implements Action{

    @Override
    public Result execute(Event e) {
       return Result.SUCCESS;
    }
    
}
