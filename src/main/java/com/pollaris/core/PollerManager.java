package com.pollaris.core;

import com.pollaris.main.PollerConfigEntry;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.pollaris.main.Config;

public class PollerManager {
    private final ScheduledExecutorService scheduler;
    private Map<Poller, Integer> pollersToFrequency;
    private Map<Poller, List<Action>> pollersToActions;


    public PollerManager(Config configuration){
        this.scheduler=Executors.newScheduledThreadPool(configuration.getPollers().size());
        for(PollerConfigEntry pollerEntry: configuration.getPollers()){
            Poller poller = mkPollerFromConfigEntry(pollerEntry);
            Integer frequencyMs = pollerEntry.frequencyMs;
            Map<String, List<Action>> locationToActions = mkActionsFromConfigEntry(pollerEntry);
        }
        

    }


    public void startPollers(){
        for(Map.Entry<Poller, Integer> pollerToFrequency: pollersToFrequency.entrySet()){
            Runnable execPoller = mkLambdaRunnableWrapper(pollerToFrequency.getKey());
            scheduler.scheduleAtFixedRate(
            execPoller, 
            0, 
            pollerToFrequency.getValue(), 
            TimeUnit.MILLISECONDS
        );
        }
    }




    private Runnable mkLambdaRunnableWrapper(Poller poller){
        return new Runnable(){
            @Override
            public void run() {
                List<Event> events = poller.poll();
                executeActions(poller.getId(), events);
            }

            private void executeActions(long id, List<Event> events) {
                // TODO Auto-generated method stub
                throw new UnsupportedOperationException("Unimplemented method 'executeActions'");
            }
        };
    }

    private Poller mkPollerFromConfigEntry(PollerConfigEntry pollerConfigEntry){
        return null;
    }
    private Map<String, List<Action>> mkActionsFromConfigEntry(PollerConfigEntry pollerConfigEntry){
        return null;
    }
}
