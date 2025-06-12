package com.pollaris.config;
import com.pollaris.config.BackendType;
public class BackendConfigEntry {
    public BackendType type;
    public String prefix; // for S3, null otherwise
    public String bucket; // for S3, null otherwise
    public String location;
}

