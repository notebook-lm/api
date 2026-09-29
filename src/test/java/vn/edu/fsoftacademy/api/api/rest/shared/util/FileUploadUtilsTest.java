package vn.edu.fsoftacademy.api.api.rest.shared.util;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class FileUploadUtilsTest {
  @Test
  void acceptsEverySupportedExtensionCaseInsensitively() {
    for (var extension : new String[] {"pdf", "docx", "doc", "xlsx", "xls", "pptx", "ppt", "md", "txt"}) {
      assertTrue(FileUploadUtils.isSupportedDocumentExtension("source." + extension));
      assertTrue(FileUploadUtils.isSupportedDocumentExtension("source." + extension.toUpperCase()));
    }
  }

  @Test
  void rejectsMissingTrailingAndUnsupportedExtensions() {
    assertFalse(FileUploadUtils.isSupportedDocumentExtension("README"));
    assertFalse(FileUploadUtils.isSupportedDocumentExtension("README."));
    assertFalse(FileUploadUtils.isSupportedDocumentExtension("archive.pdf.exe"));
  }
}
