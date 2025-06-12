package com.pollaris.poller;

import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.Nullable;

import com.pollaris.event.Event;
import com.pollaris.fs.FileEntry;
import com.pollaris.fs.PollableFs;

public class MultipleLocationsPoller implements Poller {
    private final ArrayList<Path> locations;
    private final PollerId pollerId;
    private final PollableFs pollableFs;
    private final Map<Path, Instant> latestEvents;



    public MultipleLocationsPoller(PollableFs fs, List<Path> locations){
        this.locations=new ArrayList<>(); // needs to be mutable
        this.locations.addAll(locations);
        this.pollerId=PollerId.mkOfInteger(IdGen.getInstance().getCount());
        IdGen.getInstance().increment();
        this.pollableFs = fs;
        this.latestEvents = new HashMap<>();
    }

    @Override
    public List<Event> poll() {
        List<Event> events = new ArrayList<>();
        for (Path locationPath: locations) {
            Event event = singleLocationPoll(locationPath);
            if(event!=null){
                events.add(event);
            }
        }
        return events;
    }

    @Override
    public PollerId getId() {
        return pollerId;
    }

    @Override
    public void addLocation(Path location) {
        locations.add(location);
    }

    @Override
    public void removeLocation(Path location) {
        locations.removeIf(path -> path.equals(location));
    }

    @Override
    public List<Path> locations() {
        return this.locations;
    }
    
    private @Nullable Event singleLocationPoll(Path pathToSingleLocation) {
        final FileEntry entry = this.pollableFs.listEntry(pathToSingleLocation.toString());
        Instant lastRecordedTime = this.latestEvents.getOrDefault(pathToSingleLocation, Instant.ofEpochMilli(0));
        if(entry!=null && entry.instant().isAfter(lastRecordedTime)){
            Instant newTime = entry.instant(); 
            this.latestEvents.put(pathToSingleLocation, newTime);
            return new Event() {

                @Override
                public Instant timestamp() {
                    return newTime;
                }

                @Override
                public Map<String, String> metadata() {
                    return Map.of("Size", Long.toString(entry.attrs().size()),
                                "Creation time", Long.toString(entry.attrs().creationTime().toInstant().toEpochMilli())
                                );
                }

                @Override
                public Path location() {
                    return entry.path();
                }
            };
        }
        return null;
    }
}
