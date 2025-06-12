package com.pollaris.scheduling;

import java.util.concurrent.TimeUnit;


// We need this interface to separate testing and production execution
// with dependency injection 
public interface Scheduler {
    void scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit);
}