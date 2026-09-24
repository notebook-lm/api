package vn.edu.fsoftacademy.api.api.rest.shared.util;

import java.util.Locale;
import java.util.Set;

public final class FileUploadUtils {
  private static final Set<String> SUPPORTED_DOCUMENT_EXTENSIONS =
      Set.of("pdf", "docx", "doc", "xlsx", "xls", "pptx", "ppt", "md", "txt");

  private FileUploadUtils() {}

  public static boolean isSupportedDocumentExtension(String filename) {
    int extensionSeparator = filename.lastIndexOf('.');
    String extension =
        extensionSeparator < 0
            ? ""
            : filename.substring(extensionSeparator + 1).toLowerCase(Locale.ROOT);
    return SUPPORTED_DOCUMENT_EXTENSIONS.contains(extension);
  }
}
