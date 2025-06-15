package com.pollaris;

import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.pollaris.fs.FileEntry;
import com.pollaris.fs.FileMetaData;
import com.pollaris.fs.PollableFs;
import com.pollaris.poller.IdGen;
import com.pollaris.poller.MultipleLocationsPoller;
import com.pollaris.poller.Poller;
import com.pollaris.poller.PollerId;

import junit.framework.Assert;

public class MultipleLocationPollerTest {
    /*
    * The poll method returns events only when the location has been accessed since the 
    * last time, through the mocked fs.   
    */ 
    @Test
    public void testReturnsEventOnlyWhenNeeded(){
        long time= Instant.now().toEpochMilli();
        Path path1 = Paths.get("location1");
        Path path2 = Paths.get("location2");
        List<Path> paths = List.of(path1, path2);
        MockedPollableFs mockedFs = new MockedPollableFs(paths);
        Poller poller = new MultipleLocationsPoller(mockedFs, paths,  PollerId.mkOfInteger(IdGen.getInstance().getCount()));
        Assert.assertEquals(poller.poll().size(), 2);
        mockedFs.setLatestAccessTimeLocation(path1, Instant.ofEpochMilli(time++));
        Assert.assertEquals(poller.poll().size(), 1);
        mockedFs.setLatestAccessTimeLocation(path2, Instant.ofEpochMilli(time++));
        Assert.assertEquals(poller.poll().size(), 1);
        mockedFs.setLatestAccessTimeLocation(path1, Instant.ofEpochMilli(time++));
        mockedFs.setLatestAccessTimeLocation(path2, Instant.ofEpochMilli(time++));
        Assert.assertEquals(poller.poll().size(), 2);
        Assert.assertEquals(poller.poll().size(), 0);
    }


    @Test
    public void testAddRemoveLocation(){
        Path path1 = Paths.get("location1");
        Path path2 = Paths.get("location2");
        List<Path> paths = List.of(path1, path2);
        MockedPollableFs mockedFs = new MockedPollableFs(paths);
        Poller poller = new MultipleLocationsPoller(mockedFs, paths, PollerId.mkOfInteger(IdGen.getInstance().getCount()));
        Assert.assertEquals(new HashSet<>(poller.locations()), new HashSet<Path>(paths));
        poller.removeLocation(path1);
        Assert.assertEquals(new HashSet<>(poller.locations()), new HashSet<Path>(List.of(path2)));
        poller.addLocation(path1);
        Assert.assertEquals(new HashSet<>(poller.locations()), new HashSet<Path>(paths));
    }

     class MockedPollableFs implements PollableFs{
        
        Map<Path, Instant> oldAccessTime;
        Map<Path, Instant> latestAccessTime;
        MockedPollableFs(List<Path> paths){
            this.oldAccessTime= new HashMap<>();
            this.latestAccessTime = new HashMap<>();
            for (Path path : paths) {
                this.oldAccessTime.put(path, Instant.ofEpochMilli(0));
                this.latestAccessTime.put(path, Instant.ofEpochMilli(1));
            }
        }

        @Override
        public List<FileEntry> listEntries(String locationToEvents) {
            throw new UnsupportedOperationException("Unimplemented method 'listEntries', not needed for this test");
        }

        @Override
        public FileEntry listEntry(String locationToEvent) {
            Path pathToLocation = Paths.get(locationToEvent);
            Instant oldAccessTime = this.oldAccessTime.get(pathToLocation);
            Instant latestTimeAccessLocation = this.latestAccessTime.get(pathToLocation);
            if(oldAccessTime.isBefore(latestTimeAccessLocation)){
                FileEntry entry = new FileEntry(
                            pathToLocation,
                            new FileMetaData() {
                                @Override
                                public Instant lastModifiedTime(){return latestTimeAccessLocation;}
                                @Override 
                                public Instant creationTime(){return lastModifiedTime();}
                                @Override
                                public Long size(){return Long.valueOf(0);}
                                @Override
                                public URI uri(){return null;}
                            }
                    );
                latestAccessTime.put(pathToLocation, latestTimeAccessLocation);
                return entry;
            }
            else{
                return null;
            }
        }

        void setLatestAccessTimeLocation(Path path, Instant time){
            this.latestAccessTime.put(path, time);
        }
        
    }
}
