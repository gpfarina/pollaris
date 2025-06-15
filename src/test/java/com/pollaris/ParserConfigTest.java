package com.pollaris;
import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import com.pollaris.config.Config;
public class ParserConfigTest {
    
    // test parsing of config files
    @Test
    public void parseTestFiles() throws Exception{
        URL resourceUrl = getClass().getResource("/configTests");
        Path resourcePath = Path.of(resourceUrl.toURI());
        List<Path> paths = Files.walk(resourcePath).filter(Files::isRegularFile).
        filter(p -> p.toString().endsWith(".yaml")).collect(Collectors.toList());
        for (Path path : paths) {
            Config.parse(new File(path.toString()));   
        }
    }
}
