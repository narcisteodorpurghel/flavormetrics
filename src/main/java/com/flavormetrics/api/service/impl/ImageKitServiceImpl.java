package com.flavormetrics.api.service.impl;

import com.flavormetrics.api.exception.ImageKitUploadException;
import com.flavormetrics.api.service.ImageKitService;
import io.imagekit.sdk.ImageKit;
import io.imagekit.sdk.config.Configuration;
import io.imagekit.sdk.models.FileCreateRequest;
import io.imagekit.sdk.models.results.Result;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ImageKitServiceImpl implements ImageKitService {

  private static final ImageKit IMAGE_KIT = ImageKit.getInstance();

  private final String publicKey;
  private final String privateKey;
  private final String urlEndpoint;

  public ImageKitServiceImpl(
    @Value("${imagekit.url}") String publicKey,
    @Value("${imagekit.private-key}") String privateKey,
    @Value("${imagekit.public-key}") String urlEndpoint
  ) {
    this.publicKey = publicKey;
    this.privateKey = privateKey;
    this.urlEndpoint = urlEndpoint;
  }

  @Override
  public String upload(String url, String fileName) {
    if (!fileName.contains(".")) {
      fileName = fileName + ".jpg";
    }
    FileCreateRequest fileCreateRequest = new FileCreateRequest(url, fileName);
    fileCreateRequest.setFolder("/flavormetrics");
    try {
      Result result = IMAGE_KIT.upload(fileCreateRequest);
      return result.getUrl();
    } catch (Exception e) {
      throw new ImageKitUploadException(e.getMessage(), e);
    }
  }

  @Override
  public String upload(MultipartFile file, String fileName) {
    if (!fileName.contains(".")) {
      fileName = fileName + ".jpg";
    }
    try {
      FileCreateRequest fileCreateRequest = new FileCreateRequest(file.getBytes(), fileName);
      fileCreateRequest.setFolder("/flavormetrics");
      Result result = IMAGE_KIT.upload(fileCreateRequest);
      return result.getUrl();
    } catch (Exception e) {
      throw new ImageKitUploadException(e.getMessage(), e);
    }
  }

  @PostConstruct
  void configure() {
    var config = new Configuration(publicKey, privateKey, urlEndpoint);
    IMAGE_KIT.setConfig(config);
  }
}
