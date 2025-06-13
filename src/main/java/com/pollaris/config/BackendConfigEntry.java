package com.pollaris.config;



/**
 * This calss contains basic information of a backend. Fow now only S3 and Local are supported.
 */
public class BackendConfigEntry {
    public BackendType type;
    public String prefix; // for S3, null otherwise
    public String bucket; // for S3, null otherwise
    public String location;
}

