package vn.edu.fsoftacademy.api.infrastructure.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import java.io.ByteArrayInputStream;
import org.junit.jupiter.api.Test;
import vn.edu.fsoftacademy.api.application.exception.StorageException;

class MinioObjectStorageTest {
  private static final String BUCKET = "documents";

  @Test
  void createsMissingBucketBeforeUploading() throws Exception {
    var client = mock(MinioClient.class);
    when(client.bucketExists(any(BucketExistsArgs.class))).thenReturn(false);

    new MinioObjectStorage(client, BUCKET)
        .put("projects/p/documents/d", new ByteArrayInputStream(new byte[] {1, 2}), 2, "text/plain");

    verify(client).makeBucket(argThat(args -> BUCKET.equals(args.bucket())));
    verify(client).putObject(argThat(args ->
        BUCKET.equals(args.bucket()) && "projects/p/documents/d".equals(args.object())));
  }

  @Test
  void doesNotCreateExistingBucketBeforeUploading() throws Exception {
    var client = mock(MinioClient.class);
    when(client.bucketExists(any(BucketExistsArgs.class))).thenReturn(true);

    new MinioObjectStorage(client, BUCKET).put("key", new ByteArrayInputStream(new byte[0]), 0, "text/plain");

    verify(client).putObject(any(PutObjectArgs.class));
    verify(client, org.mockito.Mockito.never()).makeBucket(any(MakeBucketArgs.class));
  }

  @Test
  void translatesBucketInitializationFailure() throws Exception {
    var client = mock(MinioClient.class);
    when(client.bucketExists(any(BucketExistsArgs.class))).thenThrow(new RuntimeException("access denied"));

    var error = assertThrows(StorageException.class,
        () -> new MinioObjectStorage(client, BUCKET).put("key", new ByteArrayInputStream(new byte[0]), 0, "text/plain"));

    assertEquals("Could not initialize document storage", error.getMessage());
  }

  @Test
  void translatesPutGetAndDeleteFailures() throws Exception {
    var putClient = mock(MinioClient.class);
    when(putClient.bucketExists(any(BucketExistsArgs.class))).thenReturn(true);
    doThrow(new RuntimeException("write failed")).when(putClient).putObject(any(PutObjectArgs.class));
    assertEquals("Could not store document", assertThrows(StorageException.class,
        () -> new MinioObjectStorage(putClient, BUCKET).put("key", new ByteArrayInputStream(new byte[0]), 0, "text/plain")).getMessage());

    var getClient = mock(MinioClient.class);
    doThrow(new RuntimeException("read failed")).when(getClient).getObject(any(GetObjectArgs.class));
    assertEquals("Could not load document", assertThrows(StorageException.class,
        () -> new MinioObjectStorage(getClient, BUCKET).get("key")).getMessage());

    var deleteClient = mock(MinioClient.class);
    doThrow(new RuntimeException("delete failed")).when(deleteClient).removeObject(any(RemoveObjectArgs.class));
    assertEquals("Could not delete document", assertThrows(StorageException.class,
        () -> new MinioObjectStorage(deleteClient, BUCKET).delete("key")).getMessage());
  }
}
