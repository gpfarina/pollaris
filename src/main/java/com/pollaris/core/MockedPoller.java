package com.pollaris.core;

import java.util.List;

public class MockedPoller implements Poller {

    @Override
    public List<Event> poll() {
        return List.of();
    }

    @Override
    public PollerId getId() {
        return PollerId.mkOfInteger(0);
    }
    
}
