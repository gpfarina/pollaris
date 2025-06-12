package com.pollaris.fs;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.S3Object;

public class S3Fs implements PollableFs{

    private final S3Client s3Client;
    private final String bucket;

    public S3Fs(S3Client s3Client, String bucket) {
        this.s3Client = s3Client;
        this.bucket = bucket;
    }

    @Override
    public List<FileEntry> listEntries(String location) {
        List<FileEntry> entries = new ArrayList<>();

        ListObjectsV2Request request = ListObjectsV2Request.builder()
                .bucket(bucket)
                .prefix(location)
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
                entries.add(new FileEntry(
                    Paths.get(obj.key()),
                    obj.lastModified(),
                    null
                ));
            }

            continuationToken = response.nextContinuationToken();

        } while (response.isTruncated());

        return entries;
    }

    @Override
    public FileEntry listEntry(String location) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'listEntry'");
    }
    
}
