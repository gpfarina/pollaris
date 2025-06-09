package com.pollaris.fs;

import java.util.List;
import java.util.Optional;

public interface PollableFs {
    List<FileEntry> listFiles(String location);  // Lists files in a folder or prefix
}