package com.pollaris.fs;

import java.util.List;

public interface PollableFs {
    List<FileEntry> listEntries(String location);  // Lists files in a folder or prefix
}