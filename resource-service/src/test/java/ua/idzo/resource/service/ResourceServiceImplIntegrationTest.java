package ua.idzo.resource.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import ua.idzo.resource.core.entity.ResourceEntity;
import ua.idzo.resource.core.integration.processor.ResourceProcessor;
import ua.idzo.resource.core.repository.ResourceRepository;
import ua.idzo.resource.core.service.ResourceService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;

@Testcontainers
@SpringBootTest(properties = {
        "eureka.client.enabled=false",
        "spring.cloud.discovery.enabled=false"
})
class ResourceServiceImplIntegrationTest {

    @Container
    private static final PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>(DockerImageName.parse("postgres:16-alpine"))
                    .withInitScript("resource-db/init.sql");

    @Container
    private static final LocalStackContainer localstack =
            new LocalStackContainer(DockerImageName.parse("localstack/localstack:latest"))
                    .withServices(LocalStackContainer.Service.S3);

    @Autowired
    private ResourceService resourceService;
    @Autowired
    private ResourceRepository resourceRepository;

    @MockBean
    private ResourceProcessor resourceProcessor;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);

        registry.add("file-storage.aws.s3.endpoint", () -> localstack.getEndpointOverride(LocalStackContainer.Service.S3).toString());
        registry.add("file-storage.aws.credentials.access-key", localstack::getAccessKey);
        registry.add("file-storage.aws.credentials.secret-key", localstack::getSecretKey);
        registry.add("file-storage.aws.s3.bucket-name", () -> "song-resources");
    }

    @BeforeAll
    static void beforeAll() {
        S3Client s3Client = S3Client.builder()
                .endpointOverride(localstack.getEndpointOverride(LocalStackContainer.Service.S3))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(localstack.getAccessKey(), localstack.getSecretKey())))
                .region(Region.of(localstack.getRegion()))
                .build();

        s3Client.createBucket(builder -> builder.bucket("song-resources"));
    }

    @Test
    void uploadResource_shouldSaveFileAndPersistToDatabase() {
        // --- GIVEN ---
        byte[] testData = "test-mp3-data".getBytes();

        doNothing().when(resourceProcessor).processResource(any(ResourceEntity.class));

        // --- WHEN ---
        ResourceEntity result = assertDoesNotThrow(() -> resourceService.uploadResource(testData));

        // --- THEN ---
        assertNotNull(result);
        assertNotNull(result.getId());

        long count = resourceRepository.count();
        assertEquals(1, count);

        ResourceEntity savedEntity = resourceRepository.findAll().get(0);
        assertNotNull(savedEntity.getLocation());
    }
}
