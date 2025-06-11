package com.pollaris.fs;

import java.util.List;

public interface PollableFs {
    // Can list files in a directory or prefix.
    List<FileEntry> listEntries(String locationToEvents);
    // Can list a single file
    FileEntry listEntry(String locationToEvent);
}