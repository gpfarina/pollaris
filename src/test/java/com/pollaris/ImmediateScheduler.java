package com.pollaris;

import java.util.concurrent.Delayed;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import com.pollaris.scheduling.Scheduler;

public class ImmediateScheduler implements Scheduler{

    // just run the command right away and return a dummy ScheduledFuture
    @Override
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
        command.run();
        return new LocalScheduledFuture();
    }

    public void doCall(Runnable command){
        scheduleAtFixedRate(command, 0, 0, null);
    }
    

    /*
    * Dummy implementation of a ScheduledFuture
    **/ 
    class LocalScheduledFuture implements ScheduledFuture<Object>{

        @Override
        public long getDelay(TimeUnit unit) {
            throw new UnsupportedOperationException("Unimplemented method 'getDelay'");
        }

        @Override
        public int compareTo(Delayed arg0) {
            throw new UnsupportedOperationException("Unimplemented method 'compareTo'");
        }

        @Override
        public boolean cancel(boolean mayInterruptIfRunning) {
            return true;
        }

        @Override
        public Object get() {
            throw new UnsupportedOperationException("Unimplemented method 'get'");
        }

        @Override
        public Object get(long timeout, TimeUnit unit) {
            throw new UnsupportedOperationException("Unimplemented method 'get'");
        }

        @Override
        public boolean isCancelled() {
            throw new UnsupportedOperationException("Unimplemented method 'isCancelled'");
        }

        @Override
        public boolean isDone() {
            throw new UnsupportedOperationException("Unimplemented method 'isDone'");
        }
        
    }
}
