package ua.idzo.resource.core.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import ua.idzo.resource.core.config.properties.AWSProperties;

import java.net.URI;

@Configuration
@RequiredArgsConstructor
public class S3Config {

    private final AWSProperties awsProperties;

    @Bean
    public S3Client s3Client() {
        return S3Client.builder()
                .endpointOverride(URI.create(awsProperties.getAws().getS3().getEndpoint()))
                .region(Region.of(awsProperties.getAws().getRegion()))
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(
                        awsProperties.getAws().getCredentials().getAccessKey(),
                        awsProperties.getAws().getCredentials().getSecretKey()))
                )
                .build();
    }
}
