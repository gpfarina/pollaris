package com.pollaris.core;

import java.util.List;

public interface Poller {
    List<Event> poll(); 
    long getId();
}