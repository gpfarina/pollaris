package com.pollaris.fs;

import java.nio.file.Path;


/*
 * A class to store information about files that a pollable file system (local or aws) can return
 */
public class FileEntry {
    private final Path path;
    private final FileMetaData metadata;
    public FileEntry(final Path path, final FileMetaData metadata){
        this.path=path;
        this.metadata=metadata;
    }
    public Path path(){return path;}
    public FileMetaData metadata(){return metadata;}
}
