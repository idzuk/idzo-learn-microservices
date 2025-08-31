package ua.idzo.resource.core.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "file-storage")
@Getter
@Setter
public class AWSProperties {

    private Aws aws;

    @Getter
    @Setter
    public static class Aws {
        private String region;
        private Credentials credentials;
        private S3 s3;
    }

    @Getter
    @Setter
    public static class Credentials {
        private String accessKey;
        private String secretKey;
    }

    @Getter
    @Setter
    public static class S3 {
        private String endpoint;
        private String bucketName;
    }
}
