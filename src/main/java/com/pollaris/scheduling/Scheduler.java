package com.pollaris.scheduling;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;


// We need this interface to separate testing and production scheduling and to simplify dependency injection 
public interface Scheduler {
    ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit);
}