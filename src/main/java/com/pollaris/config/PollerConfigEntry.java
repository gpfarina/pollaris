package com.pollaris.config;

import java.nio.file.Path;
import java.util.Map;


/*
 * A class to store the definition of a a poller.
 */
public class PollerConfigEntry {
    public String name; 
    public int frequencyMs; // the poller will poll every frequencyMs milliseconds
    public BackendConfigEntry backend; // only local or aws are supported for now
    public Map<Path, String> actions; // a mapping between Paths and Strings denoting actions (actions will be parsed further later on)
}
