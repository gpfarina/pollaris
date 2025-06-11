package com.pollaris.main;
public class BackendConfigEntry {
    public BackendType type;
    public String prefix; // for S3, null otherwise
    public String bucket; // for S3, null otherwise
    public String location;
}

