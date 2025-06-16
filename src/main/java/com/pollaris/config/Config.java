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


/**
 * A class to handle the parsing of yaml configuration files.
 */
public class Config {
    List<PollerConfigEntry> pollers;
    public List<PollerConfigEntry> getPollers() {
        return pollers;
    }
    private Config(){}

    /**
     * Smart constructor
     * @param entries
     * @return a Config instance
     */
    public static Config mkOfEntries(List<PollerConfigEntry> entries){
        Config config = new Config();
        config.pollers=entries;
        return config;

    }
    /**
     * 
     * @param yamlFile The File instance of the configuration file
     * @return a Config instance containing the information read from the file
     * @throws IOException
     */
    public static Config parse(File yamlFile) throws IOException { // readValue can throw, we don't catch it for now.
        SimpleModule module = new SimpleModule();
        module.addKeyDeserializer(Path.class, new PathKeyDeserializer());
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        mapper.registerModule(module);
        return mapper.readValue(yamlFile, Config.class);
    }

}
// Special local class to deseriazialize Path objects
class PathKeyDeserializer extends KeyDeserializer {
    @Override
    public Object deserializeKey(String key, DeserializationContext ctxt){
        return Paths.get(key);
    }
}
