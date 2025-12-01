package ua.idzo.resource.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
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
import ua.idzo.resource.core.util.TransactionUtil;
import ua.idzo.resource.dto.response.UploadResourceResponse;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isA;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Testcontainers
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "eureka.client.enabled=false",
                "spring.cloud.discovery.enabled=false"
        })
@AutoConfigureMockMvc
class ResourceControllerComponentTest {

    private static final String BUCKET_NAME = "song-resources";

    @Container
    private static final PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>(DockerImageName.parse("postgres:16-alpine"))
                    .withInitScript("resource-db/init.sql");

    @Container
    private static final LocalStackContainer localstack =
            new LocalStackContainer(DockerImageName.parse("localstack/localstack:latest"))
                    .withServices(LocalStackContainer.Service.S3);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ResourceRepository resourceRepository;

    @MockBean
    private ResourceProcessor resourceProcessor;
    @MockBean
    private TransactionUtil transactionUtil;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "validate");

        registry.add("file-storage.aws.s3.endpoint", () -> localstack.getEndpointOverride(LocalStackContainer.Service.S3).toString());
        registry.add("file-storage.aws.credentials.access-key", localstack::getAccessKey);
        registry.add("file-storage.aws.credentials.secret-key", localstack::getSecretKey);
        registry.add("file-storage.aws.s3.bucket-name", () -> BUCKET_NAME);
    }

    @BeforeAll
    static void beforeAll() {
        S3Client s3Client = S3Client.builder()
                .endpointOverride(localstack.getEndpointOverride(LocalStackContainer.Service.S3))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(localstack.getAccessKey(), localstack.getSecretKey())))
                .region(Region.of(localstack.getRegion()))
                .build();
        s3Client.createBucket(builder -> builder.bucket(BUCKET_NAME));
    }

    @AfterEach
    void tearDown() {
        resourceRepository.deleteAll();
    }

    @Test
    void uploadResource_shouldReturnOkAndSaveResource() throws Exception {
        // --- GIVEN ---
        byte[] testData = new byte[]{0x49, 0x44, 0x33};

        doAnswer(invocation -> {
            Runnable task = invocation.getArgument(0);
            task.run();
            return null;
        }).when(transactionUtil).runAfterCommit(any(Runnable.class));

        // --- WHEN & THEN ---
        mockMvc.perform(post("/resources")
                        .contentType(MediaType.valueOf("audio/mpeg"))
                        .content(testData))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", isA(Integer.class)));

        assertEquals(1, resourceRepository.count());
        ResourceEntity savedEntity = resourceRepository.findAll().get(0);
        assertNotNull(savedEntity.getLocation());

        verify(resourceProcessor).processResource(any(ResourceEntity.class));
    }

    @Test
    void getResource_shouldReturnResourceData_whenResourceExists() throws Exception {
        // --- GIVEN ---
        byte[] testData = new byte[]{0x49, 0x44, 0x33};

        doAnswer(invocation -> {
            Runnable task = invocation.getArgument(0);
            task.run();
            return null;
        }).when(transactionUtil).runAfterCommit(any(Runnable.class));

        MvcResult result = mockMvc.perform(post("/resources")
                        .contentType(MediaType.valueOf("audio/mpeg"))
                        .content(testData))
                .andExpect(status().isOk())
                .andReturn();

        UploadResourceResponse uploadResponse = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                UploadResourceResponse.class
        );
        Integer resourceId = uploadResponse.id();

        // --- WHEN & THEN ---
        mockMvc.perform(get("/resources/{id}", resourceId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.valueOf("audio/mpeg")))
                .andExpect(result1 -> assertArrayEquals(testData, result1.getResponse().getContentAsByteArray()));
    }
}
