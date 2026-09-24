package vn.edu.fsoftacademy.api.application.command.uploaddocument;

import java.io.InputStream;

public record UploadDocumentCommand(
    String title,
    String originalFilename,
    String contentType,
    long sizeBytes,
    InputStream content) {}
