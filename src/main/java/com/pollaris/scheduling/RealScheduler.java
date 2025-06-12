package com.pollaris.scheduling;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class RealScheduler implements Scheduler {
    private final ScheduledExecutorService executor;

    public RealScheduler(int pollSize){
        executor = Executors.newScheduledThreadPool(pollSize);
    }

    @Override
    public void scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
        executor.scheduleAtFixedRate(command, initialDelay, period, unit);
    } 
}
