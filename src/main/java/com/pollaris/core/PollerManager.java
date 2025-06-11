package com.pollaris.core;

import com.pollaris.main.PollerConfigEntry;
import com.pollaris.utils.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.pollaris.main.BackendConfigEntry;
import com.pollaris.main.Config;

public class PollerManager {
    private final ScheduledExecutorService scheduler;
    private final Map<PollerId, Pair<Poller, Integer>> pollerIdToPollerWithFrequency;
    private final Map<PollerId, Map<String, Action>> pollerIdToLocationsWithAction;


    public PollerManager(Config configuration){
        this.scheduler=Executors.newScheduledThreadPool(configuration.getPollers().size());
        pollerIdToPollerWithFrequency = new HashMap<>();
        pollerIdToLocationsWithAction = new HashMap<>();
        for(PollerConfigEntry pollerEntry: configuration.getPollers()){
            Poller poller = makePollerFromBackend(pollerEntry.backend);
            pollerIdToPollerWithFrequency.put(poller.getId(), new Pair<Poller,Integer>(poller, pollerEntry.frequencyMs));
            pollerIdToLocationsWithAction.put(poller.getId(), parseActionValue(pollerEntry.actions));
        }
    }


    private Poller makePollerFromBackend(BackendConfigEntry backend) {
        return new MockedPoller();
    }


    private Map<String, Action> parseActionValue(Map<String, String> locToAction) {
        Map<String, Action> mapOutput = new HashMap<>();
        for (Map.Entry<String, String> entry: locToAction.entrySet()){
            mapOutput.put(entry.getKey(), makeActionFromString(entry.getValue()));
        }
        return mapOutput;
    }


    private Action makeActionFromString(String value) {
        if(value.toLowerCase()=="logevent"){
            return new LogAction();
        }
        else{
            System.out.println("No valid Action could be parsed, defaulting to noop action");
            return new NoOpAction();
        }
    }


    public void startPollers(){
        for(Map.Entry<PollerId, Pair<Poller, Integer>> entryPoller: pollerIdToPollerWithFrequency .entrySet()){
            Runnable execPoller = mkLambdaRunnableWrapper(entryPoller.getValue().getFirst());
            scheduler.scheduleAtFixedRate(
            execPoller, 
            0, 
            entryPoller.getValue().getSecond(), 
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

            private void executeActions(PollerId id, List<Event> events) {
                Map<String, Action> actions= pollerIdToLocationsWithAction.get(id);
                for (Event event : events) {
                    Action action = actions.get(event.location().toString());
                    action.execute(event);
                }
            }
        };
    }
}
