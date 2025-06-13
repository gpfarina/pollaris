package com.pollaris.fs;

import java.net.URI;
import java.time.Instant;
/**
 * AWS and LOCAL Pollable fs need to implement such interface to return
 * metadata about files they return 
 * 
 */
public interface FileMetaData {
    Instant creationTime();
    Instant lastModifiedTime();
    Long size();
    URI uri();
}
