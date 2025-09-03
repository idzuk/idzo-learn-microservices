package ua.idzo.resource.core.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import ua.idzo.resource.core.config.properties.AWSProperties;
import ua.idzo.resource.core.dto.UploadFileDTO;
import ua.idzo.resource.core.service.FileStorage;

@Service
@RequiredArgsConstructor
public class S3FileStorage implements FileStorage {

    private final S3Client s3Client;
    private final AWSProperties awsProperties;

    @Override
    public UploadFileDTO uploadFile(String key, byte[] file) {
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(awsProperties.getAws().getS3().getBucketName())
                .key(key)
                .build();

        s3Client.putObject(request, RequestBody.fromBytes(file));
        return new UploadFileDTO(key);
    }

    @Override
    public byte[] downloadFile(String key) {
        GetObjectRequest request = GetObjectRequest.builder()
                .bucket(awsProperties.getAws().getS3().getBucketName())
                .key(key)
                .build();

        return s3Client.getObjectAsBytes(request).asByteArray();
    }

    @Override
    public void deleteFile(String key) {
        DeleteObjectRequest request = DeleteObjectRequest.builder()
                .bucket(awsProperties.getAws().getS3().getBucketName())
                .key(key)
                .build();

        s3Client.deleteObject(request);
    }
}
