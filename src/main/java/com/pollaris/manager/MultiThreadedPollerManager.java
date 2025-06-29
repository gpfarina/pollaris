package com.pollaris.manager;

import com.pollaris.event.Event;
import com.pollaris.utils.Pair;

import software.amazon.awssdk.core.exception.SdkClientException;
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
    private final Map<PollerId, ScheduledFuture<?>> futures; // we use to store handles to the threads
    private final Map<PollerId, PollerStatus> statusPollers;

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
            Poller poller = pollerFactory.create(fs, locations); 
            PollerId pollerId = poller.getId();
            statusPollers.put(pollerId, PollerStatus.REGISTERED);
            pollerIdToPollerWithFrequency.put(pollerId, new Pair<Poller,Integer>(poller, pollerEntry.frequencyMs));
            pollerIdToLocationsWithAction.put(pollerId, parseActionValue(pollerEntry.actions));
        }
    }

    /**
     * Return the status of the pollers.
     */
    @Override
    public List<Pair<Poller, PollerStatus>> status(){
        List<Pair<Poller, PollerStatus>> returnList = new ArrayList<>();
        for (Map.Entry<PollerId, PollerStatus> entry : statusPollers.entrySet()) {
            returnList.add(new Pair<Poller,PollerStatus>(pollerIdToPollerWithFrequency.get(entry.getKey()).getFirst(), entry.getValue()));
        }
        return returnList;
    }
    

    /**
     * Return the list of pollers.
     */
    @Override
    public List<Poller> pollers(){
        return pollerIdToPollerWithFrequency.values().stream().map(Pair::getFirst).toList();
    }
    
    /**
     * Initialize the pollers.
     */
    @Override
    public void startPollers(){
        for(Map.Entry<PollerId, Pair<Poller, Integer>> entryPoller: pollerIdToPollerWithFrequency.entrySet()){
            Runnable execPoller = mkLambdaRunnableWrapper(entryPoller.getValue().getFirst());
            ScheduledFuture<?> handle = scheduler.scheduleAtFixedRate(
            execPoller, 
            0, 
            entryPoller.getValue().getSecond(), 
            TimeUnit.MILLISECONDS
            );
            statusPollers.put(entryPoller.getKey(), PollerStatus.STARTED);
            futures.put(entryPoller.getKey(), handle);
        }
    }

    /**
    * Deregister and kill all the pollers.
    */
    @Override
    public void killPollers() {
        for (Map.Entry<PollerId, ScheduledFuture<?>> entry : this.futures.entrySet()) {
            entry.getValue().cancel(true); // gracelessly kills the thread
            statusPollers.put(entry.getKey(), PollerStatus.REGISTERED);
        }
    }

    /**
     * Starts a specific poller
     * @param id the PollerId to start
     */
    @Override
    public void startPoller(PollerId id){
        PollerStatus status = statusPollers.get(id);
        Pair<Poller, Integer> pollerAndFreq = pollerIdToPollerWithFrequency.get(id);
        if(status == PollerStatus.REGISTERED && pollerAndFreq!=null){
            Runnable execPoller = mkLambdaRunnableWrapper(pollerAndFreq.getFirst());
            ScheduledFuture<?> handle = scheduler.scheduleAtFixedRate(
            execPoller, 
            0, 
            pollerAndFreq.getSecond(), 
            TimeUnit.MILLISECONDS
            );
            futures.put(id, handle);
            statusPollers.put(id, PollerStatus.STARTED);
        }
        else{
            System.err.println("Attempt to start a non existent poller.");
        }

    }

    /**
     * Kills a specific poller
     * @param id the PollerId to kill
     */
    @Override
    public void killPoller(PollerId id) {
        PollerStatus status = statusPollers.get(id);
        ScheduledFuture<?> future = futures.get(id);
        if(status==PollerStatus.STARTED && future!=null){
            futures.get(id).cancel(true);
            statusPollers.put(id, PollerStatus.REGISTERED);
        }
        else{
            System.err.println("Attempt to kill a non existent poller.");
        }
    }

    /**
    * Registers a specific poller
    * @param poller the poller to register
    * @param frequency the frequency (in ms) of the poller
    * @param actions a map from Path to Action, denoting the actions of the poller for every location it monitors
    */
    @Override
    public void registerPoller(Poller poller, Integer frequency, Map<Path, Action> actions) {
        pollerIdToPollerWithFrequency.put(poller.getId(), new Pair<Poller, Integer>(poller, frequency));
        pollerIdToLocationsWithAction.put(poller.getId(), actions);
        statusPollers.put(poller.getId(), PollerStatus.REGISTERED);
    }
    /**
     * Removes a poller and deregisters it. But first it kills it if necessary. 
     */
    @Override
    public void removePoller(Poller poller) {
        this.killPoller(poller.getId());
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
            S3Client s3Client;
            try{ 
                s3Client = S3Client.builder().build();
                return new S3Fs(s3Client, backend.bucket);
            } catch(SdkClientException e){
                System.out.println(e.toString());
                System.out.println("**********");
                System.out.println("Please make sure that AWS credentials and region are setup as environment variables in the terminal where Pollaris is executed.");
                System.out.println("More information in the README file.");
                System.out.println("**********");
                System.exit(-1);
            }
            return null;
        }
    }
}
