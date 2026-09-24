package vn.edu.fsoftacademy.api.infrastructure.storage;

import io.minio.*;
import java.io.InputStream;
import org.springframework.stereotype.Component;
import vn.edu.fsoftacademy.api.application.exception.StorageException;
import vn.edu.fsoftacademy.api.application.port.ObjectStorage;

@Component
public class MinioObjectStorage implements ObjectStorage {
    private final MinioClient client;
    private final String bucket;

    public MinioObjectStorage(StorageProperties p) {
        this.client = MinioClient.builder()
                .endpoint(p.endpoint())
                .credentials(p.accessKey(), p.secretKey())
                .build();
        this.bucket = p.bucket();
    }

    private void ensureBucket() {
        try {
            if (!client.bucketExists(BucketExistsArgs.builder().bucket(bucket).build()))
                client.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
        } catch (Exception ex) {
            throw new StorageException("Could not initialize document storage", ex);
        }
    }

    public void put(String key, InputStream content, long size, String contentType) {
        ensureBucket();
        try {
            client.putObject(
                    PutObjectArgs.builder().bucket(bucket).object(key).stream(content, size, -1)
                            .contentType(contentType)
                            .build());
        } catch (Exception ex) {
            throw new StorageException("Could not store document", ex);
        }
    }

    public InputStream get(String key) {
        try {
            return client.getObject(GetObjectArgs.builder().bucket(bucket).object(key).build());
        } catch (Exception ex) {
            throw new StorageException("Could not load document", ex);
        }
    }

    public void delete(String key) {
        try {
            client.removeObject(RemoveObjectArgs.builder().bucket(bucket).object(key).build());
        } catch (Exception ex) {
            throw new StorageException("Could not delete document", ex);
        }
    }
}
