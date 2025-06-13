package com.pollaris.fs;

import java.net.URI;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.S3Object;

/*
 * A class to implement the AWS pollable fs. 
 */
public class S3Fs implements PollableFs{

    private final S3Client s3Client;
    private final String bucket;

    public S3Fs(S3Client s3Client, String bucket) {
        this.s3Client = s3Client;
        this.bucket = bucket;
    }

    /**
     * List all the files in a directory.
     * @param prefix a string denoting the path to a directory. The input is supposed to live inside the bucket.
     * @return a list of file entries if the directory can be accessed, an empty list otherwise.
    */
    @Override
    public List<FileEntry> listEntries(String prefix) {
        List<FileEntry> entries = new ArrayList<>();

        ListObjectsV2Request request = ListObjectsV2Request.builder()
                .bucket(bucket)
                .prefix(prefix)
                .build();

        ListObjectsV2Response response;
        String continuationToken = null;

        do {
            ListObjectsV2Request.Builder reqBuilder = request.toBuilder();
            if (continuationToken != null) {
                reqBuilder.continuationToken(continuationToken);
            }
            response = s3Client.listObjectsV2(reqBuilder.build());

            for (S3Object obj : response.contents()) {
                String key = obj.key();
                try {
                    HeadObjectRequest headRequest = HeadObjectRequest.builder()
                            .bucket(bucket)
                            .key(key)
                            .build();

                    HeadObjectResponse headResponse = s3Client.headObject(headRequest);
                    FileMetaData metadata = mkFileMeta(headResponse, key);
                    FileEntry entry = new FileEntry(Paths.get(key), metadata);
                    entries.add(entry);
                } catch (NoSuchKeyException e) {
                    // Object might have been deleted between list and head; skip
                }
            }

            continuationToken = response.nextContinuationToken();
        } while (response.isTruncated());

        return entries;
    }

    /**
     * Return the file a the location (inside the bucket)
     * @param location a string the path to a file
     * @return the list entry of the file, if it exists, null otherwise.
     */
    @Override
    public FileEntry listEntry(String key) {
        try {
            HeadObjectRequest headRequest = HeadObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

            HeadObjectResponse headResponse = s3Client.headObject(headRequest);
            FileMetaData metadata = mkFileMeta(headResponse, key);
            return new FileEntry(Paths.get(key),metadata);
        } catch (NoSuchKeyException e) {
            return null;
        }
    }


    // PRIVATE REGION
    // Construct a FileMetaData from an HeadObjectResponse and the name of the file
    private FileMetaData mkFileMeta(HeadObjectResponse headResponse, String key){
        FileMetaData metadata = new FileMetaData() {

                @Override
                public Instant creationTime() {
                    return headResponse.lastModified(); // S3 does not store creation time
                }

                @Override
                public Instant lastModifiedTime() {
                    return headResponse.lastModified();
                }

                @Override
                public Long size() {
                    return headResponse.contentLength();
                }

                @Override
                public URI uri() {
                    String uri = String.format("https://%s.s3.amazonaws.com/%s", bucket, key);
                    return URI.create(uri);
                }
                
            };
            return metadata;
    }
    
}
