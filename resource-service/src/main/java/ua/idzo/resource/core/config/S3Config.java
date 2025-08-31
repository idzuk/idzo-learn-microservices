package ua.idzo.resource.core.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import ua.idzo.resource.core.config.properties.AWSProperties;

import java.net.URI;

@Configuration
@RequiredArgsConstructor
public class S3Config {

    private final AWSProperties awsProperties;

    @Bean
    public S3Client s3Client() {
        AWSProperties.Aws awsProps = awsProperties.getAws();
        AWSProperties.Credentials credentials = awsProps.getCredentials();
        AWSProperties.S3 s3Props = awsProps.getS3();

        S3Configuration serviceConfiguration = S3Configuration.builder()
                .pathStyleAccessEnabled(true)
                .build();

        return S3Client.builder()
                .endpointOverride(URI.create(s3Props.getEndpoint()))
                .region(Region.of(awsProps.getRegion()))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(credentials.getAccessKey(), credentials.getSecretKey())))
                .serviceConfiguration(serviceConfiguration)
                .build();
    }
}
