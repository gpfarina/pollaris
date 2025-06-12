package com.pollaris.config;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

public class Config {
    List<PollerConfigEntry> pollers;
    public List<PollerConfigEntry> getPollers() {
        return pollers;
    }

    public static Config parse(File yamlFile) throws Exception{
        SimpleModule module = new SimpleModule();
        module.addKeyDeserializer(Path.class, new PathKeyDeserializer());
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        mapper.registerModule(module);
        return mapper.readValue(yamlFile, Config.class);
    }

}
class PathKeyDeserializer extends KeyDeserializer {
    @Override
    public Object deserializeKey(String key, DeserializationContext ctxt) throws IOException {
        return Paths.get(key);
    }
}
