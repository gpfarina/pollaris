package com.pollaris.fs;

import java.time.Instant;

public class FileEntry {
    private final String path;
    private final Instant modifiedTime;
    private final long size;
    public FileEntry(final String path, final Instant modifiedTime, final long size){
        this.path=path;
        this.modifiedTime=modifiedTime;
        this.size=size;
    }
    public String path(){return path;}
    public Instant instant(){return modifiedTime;}
    public long size(){return size;}

}
