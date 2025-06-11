package com.pollaris.core;

import com.pollaris.fs.Result;

public class NoOpAction implements Action{

    @Override
    public Result execute(Event e) {
       return Result.SUCCESS;
    }
    
}
