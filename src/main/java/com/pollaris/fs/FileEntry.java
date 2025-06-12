package com.pollaris.fs;

import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;

public class FileEntry {
    private final Path path;
    private final Instant modifiedTime;
    private final BasicFileAttributes attrs;
    public FileEntry(final Path path, final Instant modifiedTime, BasicFileAttributes attrs){
        this.path=path;
        this.modifiedTime=modifiedTime;
        this.attrs=attrs;
    }
    public Path path(){return path;}
    public Instant instant(){return modifiedTime;}
    public BasicFileAttributes attrs(){return attrs;}

}
