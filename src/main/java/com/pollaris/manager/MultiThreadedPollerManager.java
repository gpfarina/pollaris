package com.pollaris.manager;

import com.pollaris.event.Event;
import com.pollaris.utils.Pair;

import software.amazon.awssdk.services.s3.S3Client;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
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
import com.pollaris.poller.IdGen;
import com.pollaris.poller.MultipleLocationsPoller;
import com.pollaris.poller.Poller;
import com.pollaris.poller.PollerFactory;
import com.pollaris.poller.PollerId;
import com.pollaris.scheduling.Scheduler;

/**
 * A simple poller managers which uses one thread for poller.
 * The injected scheduler keeps tracks of the pollers and schedules them
 * at the right time.
 */
public class MultiThreadedPollerManager implements PollerManager {
    private final Scheduler scheduler; // This calls the polling system at the right frequency
    private final Map<PollerId, Pair<Poller, Integer>> pollerIdToPollerWithFrequency; // Needed to quickly retrieve the frequency of every poller
    private final Map<PollerId, Map<Path, Action>> pollerIdToLocationsWithAction; // Needed to quickly retrieve the the actions for every poller
    private final Map<PollerId, ScheduledFuture> futures; // we use to store handles to the threads
    private final Map<PollerId, Status> statusPollers;

    public MultiThreadedPollerManager(Config configuration, Scheduler scheduler, PollerFactory pollerFactory){
        this.scheduler=scheduler;
        pollerIdToPollerWithFrequency = new HashMap<>();
        pollerIdToLocationsWithAction = new HashMap<>();
        statusPollers = new HashMap<>();
        futures = new HashMap<>();
        for(PollerConfigEntry pollerEntry: configuration.getPollers()){
            List<Path> locations = new ArrayList<>();
            locations.addAll(pollerEntry.actions.keySet());
            PollableFs fs = mkPollableFsFromConfig(pollerEntry.backend);
            int id = IdGen.getInstance().getCount();
            IdGen.getInstance().increment(); // important to increment after we assigned the id.
            PollerId pollerId = PollerId.mkOfInteger(id);
            statusPollers.put(pollerId, Status.REGISTERED);
            Poller poller = pollerFactory.create(fs, locations, pollerId); 
            
            new MultipleLocationsPoller(fs, locations, pollerId); // this needs to be injected, (use maybe a factory?)
            pollerIdToPollerWithFrequency.put(pollerId, new Pair<Poller,Integer>(poller, pollerEntry.frequencyMs));
            pollerIdToLocationsWithAction.put(pollerId, parseActionValue(pollerEntry.actions));
        }
    }

    /**
     * Initialize the pollers.
     */
    @Override
    public void startPollers(){
        for(Map.Entry<PollerId, Pair<Poller, Integer>> entryPoller: pollerIdToPollerWithFrequency.entrySet()){
            Runnable execPoller = mkLambdaRunnableWrapper(entryPoller.getValue().getFirst());
            ScheduledFuture handle = scheduler.scheduleAtFixedRate(
            execPoller, 
            0, 
            entryPoller.getValue().getSecond(), 
            TimeUnit.MILLISECONDS
            );
            statusPollers.put(entryPoller.getKey(), Status.STARTED);
            futures.put(entryPoller.getKey(), handle);
        }
    }

    @Override
    public void killPollers() {
        for (Map.Entry<PollerId, ScheduledFuture> entry : this.futures.entrySet()) {
            entry.getValue().cancel(true); // gracelessly kills the thread
            statusPollers.put(entry.getKey(), Status.REGISTERED);
        }
    }

    @Override
    public void startPoller(PollerId id){
        Status status = statusPollers.get(id);
        Pair<Poller, Integer> pollerAndFreq = pollerIdToPollerWithFrequency.get(id);
        if(status == Status.REGISTERED && pollerAndFreq!=null){
            Runnable execPoller = mkLambdaRunnableWrapper(pollerAndFreq.getFirst());
            ScheduledFuture handle = scheduler.scheduleAtFixedRate(
            execPoller, 
            0, 
            pollerAndFreq.getSecond(), 
            TimeUnit.MILLISECONDS
            );
            futures.put(id, handle);
            statusPollers.put(id, Status.STARTED);
        }
        else{
            System.err.println("Attempt to start a non existent poller.");
        }

    }

    @Override
    public void killPoller(PollerId id) {
        Status status = statusPollers.get(id);
        ScheduledFuture future = futures.get(id);
        if(status==Status.STARTED && future!=null){
            futures.get(id).cancel(true);
            statusPollers.put(id, Status.REGISTERED);
        }
        else{
            System.err.println("Attempt to kill a non existent poller.");
        }
    }

    @Override
    public void registerPoller(Poller poller, Integer frequency, Map<Path, Action> actions) {
        pollerIdToPollerWithFrequency.put(poller.getId(), new Pair<Poller, Integer>(poller, frequency));
        pollerIdToLocationsWithAction.put(poller.getId(), actions);
        statusPollers.put(poller.getId(), Status.REGISTERED);
    }

    @Override
    public void removePoller(Poller poller) {
        pollerIdToLocationsWithAction.remove(poller.getId());
        pollerIdToPollerWithFrequency.remove(poller.getId());
        futures.remove(poller.getId());
        statusPollers.remove(poller.getId());
    }

    // PRIVATE REGION
    // parses strings into valid actions and translates the maps
    private Map<Path, Action> parseActionValue(Map<Path, String> locToAction) {
            Map<Path, Action> mapOutput = new HashMap<>();
            for (Map.Entry<Path, String> entry: locToAction.entrySet()){
                mapOutput.put(entry.getKey(), mkActionFromString(entry.getValue()));
            }
            return mapOutput;
        }
    // smart constructor for runnable actions
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
    // parse strings into valid action.
    // we only have two valid actions for now.
    private static Action mkActionFromString(String value) {
        if(value.toLowerCase().equals("logevent")){
            return new LogAction();
        }
        else{
            System.out.println("No valid Action could be parsed, defaulting to noop action");
            return new NoOpAction();
        }
    }

    // smart constructor for PollableFs
    private static PollableFs mkPollableFsFromConfig(BackendConfigEntry backend) {
        if(backend.type == BackendType.LOCAL){
            return new LocalFs();
        }
        else {
            S3Client s3Client = S3Client.builder().build();
            return new S3Fs(s3Client, backend.bucket);
        }
    }

    private enum Status{
        REGISTERED,
        STARTED,
    }
}
