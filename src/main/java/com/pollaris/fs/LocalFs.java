package com.pollaris.fs;

import java.io.File;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;


/*
 * A class to implement the local pollable fs. 
 */
public class LocalFs implements PollableFs {

    /**
     * List all the files in a directory.
     * @param location a string denoting the path to a directory
     * @return a list of file entries if the directory can be accessed, an empty list otherwise.
     */
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
                    entries.add(new FileEntry(Paths.get(file.getAbsolutePath()), mkMetaData(file, attrs)));
                } catch (Exception e) {
                    System.err.println("Error reading attributes of " + file.getAbsolutePath());
                }
            }
        }

        return entries;
    }

    /**
     * Return the file a the location
     * @param location a string the path to a file
     * @return the list entry of the file, if it exists, null otherwise.
     */
    @Override
    public FileEntry listEntry(String location){
        File file = new File(location);
        FileEntry entry=null;
        if(file.exists() && !file.isDirectory()) {
            try{ 
            BasicFileAttributes attrs = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
            entry = new FileEntry(Paths.get(file.getAbsolutePath()), mkMetaData(file, attrs));
            } catch(Exception e){
                    System.err.println("Error reading attributes of " + file.getAbsolutePath());
            }
        }
        return entry;
    }



    // PRIVATE REGION
    // Construct FileMetaData from BasicFileAttributes instance
    private FileMetaData mkMetaData(File file, BasicFileAttributes attrs){
        return
            new FileMetaData() {

                @Override
                public Instant creationTime() {
                    return attrs.creationTime().toInstant();
                }

                @Override
                public Instant lastModifiedTime() {
                    return attrs.lastModifiedTime().toInstant();
                }

                @Override
                public Long size() {
                    return attrs.size();
                }

                @Override
                public URI uri() {
                    return Paths.get(file.getAbsolutePath()).toUri();
                }
            };
    }

}