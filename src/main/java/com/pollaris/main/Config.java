package com.pollaris.main;

import java.io.File;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

public class Config {
    List<PollerConfigEntry> pollers;
    public List<PollerConfigEntry> getPollers() {
        return pollers;
    }

    public static Config parse(File yamlFile) throws Exception{
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        return mapper.readValue(yamlFile, Config.class);
    }
}
