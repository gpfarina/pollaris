package com.pollaris;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.pollaris.config.Config;
import com.pollaris.fs.LocalFs;
import com.pollaris.manager.MultiThreadedPollerManager;
import com.pollaris.manager.PollerManager;
import com.pollaris.manager.PollerStatus;
import com.pollaris.poller.Poller;
import com.pollaris.poller.PollerFactory;
import com.pollaris.utils.Pair;

public class MultiThreadedPollerManagerTest {
    @Test
    public void testStartKillPollers() throws Exception{
        URL resourceUrl = getClass().getResource("/configTests/testPollerManager.yaml");
        Path resourcePath = Path.of(resourceUrl.toURI());
        Config config = Config.parse(new File(resourcePath.toString()));
        PollerManager manager = new MultiThreadedPollerManager(config, new ImmediateScheduler(), new PollerFactory());
        manager.startPollers();
        assertEquals(manager.pollers().size(), config.getPollers().size());
        for (Pair<Poller, PollerStatus> entry: manager.status()) {
            assertEquals(entry.getSecond(), PollerStatus.STARTED);
        }
        manager.killPollers();
        for (Pair<Poller, PollerStatus> entry: manager.status()) {
            assertEquals(entry.getSecond(), PollerStatus.REGISTERED);
        }
    }
   
    @Test
    public void testAddPoller(){
        PollerFactory pollerFactory = new PollerFactory();
        PollerManager manager = new MultiThreadedPollerManager(Config.mkOfEntries(List.of()), new ImmediateScheduler(), pollerFactory);
        assertEquals(manager.pollers().size(), 0);
        manager.registerPoller(pollerFactory.create(new LocalFs(), List.of(Paths.get("location"))), 1000, Map.of());
        assertEquals(manager.pollers().size(), 1);
    }
    @Test
    public void testRemovePoller(){
        PollerFactory pollerFactory = new PollerFactory();
        PollerManager manager = new MultiThreadedPollerManager(Config.mkOfEntries(List.of()), new ImmediateScheduler(), pollerFactory);
        Poller poller =pollerFactory.create(new LocalFs(), List.of(Paths.get("location")));
        manager.registerPoller(poller, 1000, Map.of());
        assertEquals(manager.pollers().size(), 1);
        manager.removePoller(poller);
        assertEquals(manager.pollers().size(), 0);
    }

    @Test
    public void testStartKillPoller(){
        PollerFactory pollerFactory = new PollerFactory();
        PollerManager manager = new MultiThreadedPollerManager(Config.mkOfEntries(List.of()), new ImmediateScheduler(), pollerFactory);
        Poller poller = pollerFactory.create(new LocalFs(), List.of(Paths.get("location")));
        manager.registerPoller(poller, 1000, Map.of());
        assertEquals(manager.status().size(),1);
        assertEquals(manager.status().get(0).getSecond(),PollerStatus.REGISTERED);
        manager.startPoller(poller.getId());
        assertEquals(manager.status().get(0).getSecond(),PollerStatus.STARTED);
        manager.killPoller(poller.getId());
        assertEquals(PollerStatus.REGISTERED, manager.status().get(0).getSecond());
    }
}
