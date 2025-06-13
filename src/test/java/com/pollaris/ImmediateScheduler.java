package com.pollaris;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import com.pollaris.scheduling.Scheduler;

public class ImmediateScheduler implements Scheduler{

    // just run the command right away
    @Override
    public ScheduledFuture scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
       command.run();
       return null;
    }

    public void doCall(Runnable command){
        scheduleAtFixedRate(command, 0, 0, null);
    }
    
}
