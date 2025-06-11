package com.pollaris.fs;

import java.io.File;
import java.io.FileFilter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class LocalFs implements PollableFs {
    @Override
    public List<FileEntry> listEntries(String location) {
        List<FileEntry> entries = new ArrayList<>();
        File folder = new File(location);

        if (!folder.exists() || !folder.isDirectory()) {
            System.err.println("Invalid location: " + location);
            return entries;
        }

        File[] files = folder.listFiles();
        if (files == null) return entries;

        for (File file : files) {
            if (file.isFile()) {
                try {
                    BasicFileAttributes attrs = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
                    entries.add(new FileEntry(
                            Paths.get(file.getAbsolutePath()),
                            attrs.lastModifiedTime().toInstant(),
                            attrs.size()
                    ));
                } catch (Exception e) {
                    System.err.println("Error reading attributes of " + file.getAbsolutePath());
                }
            }
        }

        return entries;
    }

    @Override
    public FileEntry listEntry(String location){
        File file = new File(location);
        FileEntry entry=null;
        if(file.exists() && !file.isDirectory()) {
            try{ 
            BasicFileAttributes attrs = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
            entry = new FileEntry(
                            Paths.get(file.getAbsolutePath()),
                            attrs.lastModifiedTime().toInstant(),
                            attrs.size()
                    );
            } catch(Exception e){
                    System.err.println("Error reading attributes of " + file.getAbsolutePath());
            }
        }
        return entry;
    }


}