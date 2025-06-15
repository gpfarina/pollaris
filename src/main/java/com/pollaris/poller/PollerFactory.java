package com.pollaris.poller;

import java.nio.file.Path;
import java.util.List;

import com.pollaris.fs.PollableFs;


/*
* A simple factory for Pollers, so we can change the implementation without changing the poller manger.
*/ 
public class PollerFactory {
    public Poller create(PollableFs fs, List<Path> locations){
        return new MultipleLocationsPoller(fs, locations, PollerId.fresh());
    }
}
