package com.pollaris.manager;

import com.pollaris.event.Event;
import com.pollaris.utils.Pair;

import software.amazon.awssdk.services.s3.S3Client;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;



import com.pollaris.action.Action;
import com.pollaris.action.LogAction;
import com.pollaris.action.NoOpAction;
import com.pollaris.config.BackendConfigEntry;
import com.pollaris.config.BackendType;
import com.pollaris.config.Config;
import com.pollaris.config.PollerConfigEntry;
import com.pollaris.fs.LocalFs;
import com.pollaris.fs.PollableFs;
import com.pollaris.fs.S3Fs;
import com.pollaris.poller.MultipleLocationsPoller;
import com.pollaris.poller.Poller;
import com.pollaris.poller.PollerId;
import com.pollaris.scheduling.Scheduler;

public class PollerManager {
    private final Scheduler scheduler;
    private final Map<PollerId, Pair<Poller, Integer>> pollerIdToPollerWithFrequency;
    private final Map<PollerId, Map<Path, Action>> pollerIdToLocationsWithAction;


    public PollerManager(Config configuration,Scheduler scheduler){
        this.scheduler=scheduler;
        pollerIdToPollerWithFrequency = new HashMap<>();
        pollerIdToLocationsWithAction = new HashMap<>();
        for(PollerConfigEntry pollerEntry: configuration.getPollers()){
            List<Path> locations = new ArrayList<>();
            locations.addAll(pollerEntry.actions.keySet());
            PollableFs fs = mkPollableFsFromConfig(pollerEntry.backend);
            Poller poller = new MultipleLocationsPoller(fs, locations);
            pollerIdToPollerWithFrequency.put(poller.getId(), new Pair<Poller,Integer>(poller, pollerEntry.frequencyMs));
            pollerIdToLocationsWithAction.put(poller.getId(), parseActionValue(pollerEntry.actions));
        }
    }

    public void startPollers(){
        for(Map.Entry<PollerId, Pair<Poller, Integer>> entryPoller: pollerIdToPollerWithFrequency.entrySet()){
            Runnable execPoller = mkLambdaRunnableWrapper(entryPoller.getValue().getFirst());
            scheduler.scheduleAtFixedRate(
            execPoller, 
            0, 
            entryPoller.getValue().getSecond(), 
            TimeUnit.MILLISECONDS
        );
        }
    }



    // PRIVATE REGION

    private Map<Path, Action> parseActionValue(Map<Path, String> locToAction) {
            Map<Path, Action> mapOutput = new HashMap<>();
            for (Map.Entry<Path, String> entry: locToAction.entrySet()){
                mapOutput.put(entry.getKey(), mkActionFromString(entry.getValue()));
            }
            return mapOutput;
        }

    private Runnable mkLambdaRunnableWrapper(Poller poller){
        return new Runnable(){
            @Override
            public void run() {
                List<Event> events = poller.poll();
                executeActions(poller.getId(), events);
            }
            private void executeActions(PollerId id, List<Event> events) {
                Map<Path, Action> actions= pollerIdToLocationsWithAction.get(id);
                for (Event event : events) {
                    Action action = actions.get(event.location());
                    action.execute(event);
                }
            }
        };
    }

    private Action mkActionFromString(String value) {
        if(value.toLowerCase().equals("logevent")){
            return new LogAction();
        }
        else{
            System.out.println("No valid Action could be parsed, defaulting to noop action");
            return new NoOpAction();
        }
    }


    private PollableFs mkPollableFsFromConfig(BackendConfigEntry backend) {
        if(backend.type == BackendType.LOCAL){
            return new LocalFs();
        }
        else {
            S3Client s3Client = S3Client.builder().build();
            return new S3Fs(s3Client, backend.bucket);
        }
    }

}
