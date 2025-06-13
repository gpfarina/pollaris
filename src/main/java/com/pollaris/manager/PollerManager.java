package com.pollaris.manager;

import java.nio.file.Path;
import java.util.Map;

import com.pollaris.action.Action;
import com.pollaris.poller.Poller;
import com.pollaris.poller.PollerId;


/**
 * A Poller manager manages pollers. 
 * It starts them and kills them. 
 * The scheduling is left at the implementation.
 */
public interface PollerManager {
    public void registerPoller(Poller poller, Integer freqMs, Map<Path, Action> actions);
    public void removePoller(Poller poller);
    public void startPollers();
    public void killPollers();
    public void startPoller(PollerId id);
    public void killPoller(PollerId id);
}
