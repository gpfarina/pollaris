package com.pollaris;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOError;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.pollaris.fs.FileEntry;
import com.pollaris.fs.LocalFs;
import com.pollaris.fs.PollableFs;
public class TestLocalPollebleFs {
    @TempDir
    Path tempDir;

    // Test that listing an empty directory returns an empty list of entries  
    @Test
    public void testRetrieveLocallyEmpty() {
        PollableFs localPollableFs = new LocalFs(); 
        List<FileEntry> entries = localPollableFs.listEntries(tempDir.toString());
        assertNotNull(entries);
        assertEquals(entries.size(),0);
    }

    // Test that listing a non empty directory returns the correct information   
    @Test
    public void testRetrieveLocallyNonEmpty() throws IOException{
        PollableFs localPollableFs = new LocalFs(); 
        Files.createFile(tempDir.resolve("location1"));
        Files.createFile(tempDir.resolve("location2"));
        List<FileEntry> entries = localPollableFs.listEntries(tempDir.toString());
        assertNotNull(entries);
        assertEquals(entries.size(),2);
        assertEquals(entries.get(0).path().getFileName().toString(), "location1");
        assertEquals(entries.get(1).path().getFileName().toString(), "location2");
        Files.deleteIfExists(tempDir.resolve("location1"));
        Files.deleteIfExists(tempDir.resolve("location2"));
    }
    
}
