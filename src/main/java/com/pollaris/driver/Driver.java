package com.pollaris.driver;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.pollaris.config.Config;
import com.pollaris.manager.MultiThreadedPollerManager;
import com.pollaris.manager.PollerManager;
import com.pollaris.scheduling.RealScheduler;

public class Driver {
    public static void main(String args[]) throws Exception{
        Path configFilePath=null;
        if(args.length==0){
            Path currentRelativePath = Paths.get("config.yaml");
            configFilePath=currentRelativePath.toAbsolutePath();
        }
        if(args.length==1){
            Path currentRelativePath = Paths.get(args[0]);
            configFilePath=currentRelativePath.toAbsolutePath();
        }
        else{
            System.err.println("No valid configuration, path: " + configFilePath);
            System.exit(-1);
        }
        Config configuration=Config.parse(new File(configFilePath.toString()));
        PollerManager manager=new MultiThreadedPollerManager(configuration,new RealScheduler(configuration.getPollers().size()));
        manager.startPollers();
    }
}
