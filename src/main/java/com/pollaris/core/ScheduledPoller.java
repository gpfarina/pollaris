package com.pollaris.core;

public interface ScheduledPoller extends Poller{
    void startPollingProcess();
    void stopPollingProcess();
}
