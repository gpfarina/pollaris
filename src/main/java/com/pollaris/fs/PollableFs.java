package com.pollaris.fs;

import java.util.List;


/**
 * An interface to abstract away details of the file system.
 * The only thing required by an implementation are to list the content of a directory (maybe we can do without?)
 * and to return the metadata of a single file (if exists)
 */
public interface PollableFs {
    // Can list files in a directory or prefix.
    List<FileEntry> listEntries(String locationToEvents);
    // Can list a single file
    FileEntry listEntry(String locationToEvent);
}