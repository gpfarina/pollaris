package com.pollaris.fs;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
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
                            file.getAbsolutePath(),
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
}