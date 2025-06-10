package com.pollaris.fs;

import java.nio.file.Path;
import java.time.Instant;

public class FileEntry {
    private final Path path;
    private final Instant modifiedTime;
    private final long size;
    public FileEntry(final Path path, final Instant modifiedTime, final long size){
        this.path=path;
        this.modifiedTime=modifiedTime;
        this.size=size;
    }
    public Path path(){return path;}
    public Instant instant(){return modifiedTime;}
    public long size(){return size;}

}
