package com.pollaris.poller;
/**
 * A singleton class to get fresh integers when needed for ids.
 * 
 */
public class IdGen {
    private static volatile IdGen instance;
    private int count = 0;

    private IdGen() {}
    public static IdGen getInstance() {
        if (instance == null) {
            synchronized (IdGen.class) { // Every thread needs to access the same unique instance
                if (instance == null) {
                    instance = new IdGen();
                }
            }
        }
        return instance;
    }
    // This method will be called (only?) by the poller manager. Since there
    // is only going to be one poller manager the synchronized keyword is not really
    // necessary but in the future we can imagine multiple concurrent managers.
    public synchronized int increment() {
        return ++count;
    }
    // Since there can be multiple pollers executed in parallel
    // we keep on the safe side and use synchronized here.
    public synchronized int getCount() {
        return count;
    }
}