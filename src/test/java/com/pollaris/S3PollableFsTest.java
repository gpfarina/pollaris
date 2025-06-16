package com.pollaris;

import com.adobe.testing.s3mock.junit5.S3MockExtension;
import com.pollaris.fs.FileEntry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.net.URI;
import java.util.List;

import com.pollaris.fs.S3Fs;

import static org.junit.jupiter.api.Assertions.*;

class S3PollableFsTest {

    @RegisterExtension
    static final S3MockExtension S3_MOCK = S3MockExtension.builder()
        .withSecureConnection(false)
        .withInitialBuckets("test-bucket")
        .build();

    private S3Client s3Client;
    private S3Fs s3Fs;

    @BeforeEach
    void setUp() {
        s3Client = S3Client.builder()
            .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create("accessKey", "secretKey")))
            .region(Region.US_EAST_1)
            .endpointOverride(URI.create(S3_MOCK.getServiceEndpoint().toString()))
            .forcePathStyle(true)
            .build();

        s3Fs = new S3Fs(s3Client, "test-bucket");
    }

    /**
     * Test the s3 pollable fs returns all the objects in a prefix.
     */
    @Test
    void testListEntries_returnsAllObjectsWithPrefix() {
        // Arrange: upload some files
        s3Client.putObject(putReq("test-bucket", "prefix/file1.txt"), RequestBody.fromString("content1"));
        s3Client.putObject(putReq("test-bucket", "prefix/file2.txt"), RequestBody.fromString("content2"));
        s3Client.putObject(putReq("test-bucket", "other/file3.txt"), RequestBody.fromString("content3"));

        // Act
        List<FileEntry> entries = s3Fs.listEntries("prefix");

        // Assert
        assertEquals(2, entries.size());
        assertTrue(entries.stream().anyMatch(e -> e.path().toString().equals("prefix/file1.txt")));
        assertTrue(entries.stream().anyMatch(e -> e.path().toString().equals("prefix/file2.txt")));
    }

    private PutObjectRequest putReq(String bucket, String key) {
        return PutObjectRequest.builder()
            .bucket(bucket)
            .key(key)
            .build();
    }
}
