package ua.idzo.resource.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.idzo.resource.core.dto.UploadFileDTO;
import ua.idzo.resource.core.entity.ResourceEntity;
import ua.idzo.resource.core.integration.processor.impl.ResourceProcessorImpl;
import ua.idzo.resource.core.repository.ResourceRepository;
import ua.idzo.resource.core.service.impl.ResourceServiceImpl;
import ua.idzo.resource.core.service.impl.S3FileStorage;
import ua.idzo.resource.core.util.TransactionUtil;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResourceServiceImplTest {

    @Mock
    private ResourceRepository resourceRepository;
    @Mock
    private S3FileStorage s3Service;
    @Mock
    private ResourceProcessorImpl resourceProcessor;
    @Mock
    private TransactionUtil transactionUtil;

    @InjectMocks
    private ResourceServiceImpl resourceService;

    @Test
    void uploadResource_shouldSaveFileAndDelegateToProcessor_afterCommit() {
        // --- GIVEN ---
        byte[] testData = "test-mp3-data".getBytes();

        doAnswer(invocation -> {
            Runnable task = invocation.getArgument(0);
            task.run();
            return null;
        }).when(transactionUtil).runAfterCommit(any(Runnable.class));

        UploadFileDTO uploadedFileDTO = new UploadFileDTO("some-unique-s3-key");
        when(s3Service.uploadFile(anyString(), any(byte[].class))).thenReturn(uploadedFileDTO);

        ResourceEntity savedEntity = new ResourceEntity();
        savedEntity.setId(1);
        savedEntity.setLocation("some-unique-s3-key");
        when(resourceRepository.save(any(ResourceEntity.class))).thenReturn(savedEntity);

        // --- WHEN ---
        resourceService.uploadResource(testData);

        // --- THEN ---
        verify(s3Service).uploadFile(anyString(), any(byte[].class));
        verify(resourceRepository).save(any(ResourceEntity.class));

        ArgumentCaptor<ResourceEntity> resourceId = ArgumentCaptor.forClass(ResourceEntity.class);
        verify(resourceProcessor).processResource(resourceId.capture());
    }
}
