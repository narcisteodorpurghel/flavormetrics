package com.flavormetrics.api.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.flavormetrics.api.exception.ImageKitUploadException;
import io.imagekit.sdk.ImageKit;
import io.imagekit.sdk.models.FileCreateRequest;
import io.imagekit.sdk.models.results.Result;
import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;

class ImageKitServiceImplTest {

  private static MockedStatic<ImageKit> imageKitStaticMock;
  private static ImageKit mockImageKit;
  private ImageKitServiceImpl service;

  @BeforeAll
  static void beforeAll() {
    imageKitStaticMock = mockStatic(ImageKit.class);
    mockImageKit = mock(ImageKit.class);
    imageKitStaticMock.when(ImageKit::getInstance).thenReturn(mockImageKit);
  }

  @AfterAll
  static void afterAll() {
    imageKitStaticMock.close();
  }

  @BeforeEach
  void setUp() {
    service = new ImageKitServiceImpl("mock-public-key", "mock-private-key", "mock-url-endpoint");
    service.configure();
  }

  @Test
  void upload_success_shouldReturnUrl() throws Exception {
    String url = "https://test.com/test-image.jpg";
    String fileName = "test-image.jpg";
    Result mockResult = new Result();
    mockResult.setUrl("https://imagekit.io/test-image.jpg");

    when(mockImageKit.upload(any(FileCreateRequest.class))).thenReturn(mockResult);

    String resultUrl = service.upload(url, fileName);

    assertEquals("https://imagekit.io/test-image.jpg", resultUrl);
  }

  @Test
  void upload_missingFileExtension_shouldAppendJpg() throws Exception {
    String url = "https://test.com/image";
    String fileName = "no-extension";
    Result mockResult = new Result();
    mockResult.setUrl("https://imagekit.io/no-extension.jpg");

    when(mockImageKit.upload(any(FileCreateRequest.class))).thenReturn(mockResult);

    String result = service.upload(url, fileName);
    assertEquals("https://imagekit.io/no-extension.jpg", result);
  }

  @Test
  void upload_failure_shouldThrowImageKitUploadException() throws Exception {
    String url = "https://test.com/image.jpg";
    String fileName = "invalid-image.jpg";

    when(mockImageKit.upload(any(FileCreateRequest.class))).thenThrow(new RuntimeException());

    assertThrows(ImageKitUploadException.class, () -> service.upload(url, fileName));
  }
}
