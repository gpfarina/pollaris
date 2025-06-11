package com.pollaris.core;

import com.pollaris.fs.Result;

public class LogAction implements Action{

    @Override
    public Result execute(Event e) {
       System.out.println("Event has occurred:" + e.toString());
       return Result.SUCCESS;
    }
    
}
