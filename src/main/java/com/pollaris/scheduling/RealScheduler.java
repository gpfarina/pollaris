package com.pollaris.scheduling;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * This scheduler spaws one thread for every Runnable command executed.
 * This might not be very efficient in terms of memory and cpu usage.
 */
public class RealScheduler implements Scheduler{
    private final ScheduledExecutorService executor;
    private int pollSize;
    public RealScheduler(int pollSize){
        this.pollSize = pollSize;
        executor = Executors.newScheduledThreadPool(this.pollSize);
    }

    /**
     * Execute the Runnable at fixed frequencies.
     * @param command the action to execute
     * @param initialDelay how long before the first execution
     * @param period frequency 
     * @param unit what time unit to use to interpret the previous two parameters
     * @return a ScheduleFuture to have an handle to the scheduled thread.
     */
    @Override
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
        ScheduledFuture<?> future = executor.scheduleAtFixedRate(command, initialDelay, period, unit);
        return future;
    } 
}
