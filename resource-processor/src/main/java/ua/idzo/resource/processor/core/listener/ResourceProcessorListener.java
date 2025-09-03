package ua.idzo.resource.processor.core.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.ImmediateRequeueAmqpException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import ua.idzo.resource.processor.core.dto.ResourceProcessRequest;
import ua.idzo.resource.processor.core.service.MetadataExtractor;
import ua.idzo.resource.processor.dto.song.request.CreateSongRequest;
import ua.idzo.resource.processor.dto.song.response.CreateSongResponse;
import ua.idzo.resource.processor.integration.resource.ResourceFeignClient;
import ua.idzo.resource.processor.integration.song.SongFeignClient;

@Slf4j
@Component
@RequiredArgsConstructor
public class ResourceProcessorListener {

    private final ResourceFeignClient resourceFeignClient;
    private final SongFeignClient songFeignClient;

    @RabbitListener(queues = "${resource-processor.queue.process-resource.inbound}")
    public void onMessage(ResourceProcessRequest inboundRequest) {
        try {
            log.info("Process resource with request - {}", inboundRequest);
            ResponseEntity<byte[]> resourceResponse = resourceFeignClient.getResource(inboundRequest.resourceId());

            if (!resourceResponse.getStatusCode().is2xxSuccessful()) {
                log.error("Failed to retrieve resource data for ID: {}. Status: {}",
                        inboundRequest.resourceId(), resourceResponse.getStatusCode());
                throw new ImmediateRequeueAmqpException("Failed to get resource with ID: " + inboundRequest.resourceId());
            }

            CreateSongRequest request = buildCreateSongRequest(inboundRequest.resourceId(), resourceResponse.getBody());
            ResponseEntity<CreateSongResponse> songResponse = songFeignClient.createSong(request);
            if (!songResponse.getStatusCode().is2xxSuccessful()) {
                log.error("Failed to create song metadata for resource ID: {}. Status: {}",
                        inboundRequest.resourceId(), songResponse.getStatusCode());
                throw new ImmediateRequeueAmqpException("Failed to save song metadata for resource ID: " + inboundRequest.resourceId());
            }

            log.info("Successfully processed resource with ID: {}", inboundRequest.resourceId());
        } catch (Exception e) {
            throw new ImmediateRequeueAmqpException("ResourceProcessorListener failed for request ID - %s"
                    .formatted(inboundRequest.resourceId()), e);
        }
    }

    private static CreateSongRequest buildCreateSongRequest(Integer resourceId, byte[] resourceBytes) {
        MetadataExtractor metadataExtractor = MetadataExtractor.getExtractor(resourceBytes);
        return new CreateSongRequest(resourceId,
                metadataExtractor.getName(), metadataExtractor.getArtist(), metadataExtractor.getAlbum(),
                metadataExtractor.getDuration(), metadataExtractor.getYear());
    }
}
