package com.pollaris.manager;

import com.pollaris.poller.PollerId;

public interface PollerManager {
    public void startPollers();
    public void killPollers();
    public void startPoller(PollerId id);
    public void killPoller(PollerId id);
}
