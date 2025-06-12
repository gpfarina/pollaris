package com.pollaris.poller;

public class IdGen {
    private static volatile IdGen instance;
    private int count = 0;

    private IdGen() {}
    public static IdGen getInstance() {
        if (instance == null) {
            synchronized (IdGen.class) {
                if (instance == null) {
                    instance = new IdGen();
                }
            }
        }
        return instance;
    }

    public synchronized int increment() {
        return ++count;
    }

    public synchronized int getCount() {
        return count;
    }
}