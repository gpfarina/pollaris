package com.pollaris;

import java.io.File;
import java.net.URL;
import java.nio.file.Path;


import org.junit.jupiter.api.Test;

import com.pollaris.config.Config;
import com.pollaris.manager.MultiThreadedPollerManager;
import com.pollaris.manager.PollerManager;

public class TestMultiThreadedPollerManager {
    @Test
    public void testPollerManager() throws Exception{
        URL resourceUrl = getClass().getResource("/configTests/testPollerManager.yaml");
        Path resourcePath = Path.of(resourceUrl.toURI());
        Config config = Config.parse(new File(resourcePath.toString()));
        PollerManager manager = new MultiThreadedPollerManager(config, new ImmediateScheduler());
        manager.startPollers();
    }
}
