package com.woolam.memberprofileservice.common.s3;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.IOException;
import java.time.Duration;
import java.util.UUID;

@Service
public class S3ProfileImageService {

    private static final Duration PRESIGNED_URL_DURATION = Duration.ofDays(7);

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private final String bucket;

    public S3ProfileImageService(
            @Value("${cloud.aws.s3.bucket}") String bucket,
            @Value("${spring.cloud.aws.region.static}") String region
    ) {
        this.bucket = bucket;
        Region awsRegion = Region.of(region);
        this.s3Client = S3Client.builder()
                .region(awsRegion)
                .build();
        this.s3Presigner = S3Presigner.builder()
                .region(awsRegion)
                .build();
    }

    public String upload(UUID memberId, MultipartFile file) {
        String objectKey = createObjectKey(memberId, file.getOriginalFilename());
        String contentType = file.getContentType() == null ? "application/octet-stream" : file.getContentType();
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucket)
                .key(objectKey)
                .contentType(contentType)
                .build();

        try {
            s3Client.putObject(request, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
        } catch (IOException exception) {
            throw new IllegalStateException("S3 프로필 이미지 업로드에 실패했습니다.", exception);
        }

        return objectKey;
    }

    public String createPresignedUrl(String objectKey) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucket)
                .key(objectKey)
                .build();
        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(PRESIGNED_URL_DURATION)
                .getObjectRequest(getObjectRequest)
                .build();

        PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(presignRequest);

        return presignedRequest.url().toString();
    }

    private String createObjectKey(UUID memberId, String originalFilename) {
        String safeFilename = originalFilename == null || originalFilename.isBlank()
                ? "profile-image"
                : originalFilename.replace("\\", "_").replace("/", "_");

        return "profile-images/%s/%s-%s".formatted(memberId, UUID.randomUUID(), safeFilename);
    }
}
