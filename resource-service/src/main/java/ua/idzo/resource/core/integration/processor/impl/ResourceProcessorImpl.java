package ua.idzo.resource.core.integration.processor.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import ua.idzo.resource.core.entity.ResourceEntity;
import ua.idzo.resource.core.integration.processor.ResourceProcessorProperties;
import ua.idzo.resource.core.integration.processor.request.ResourceProcessRequest;
import ua.idzo.resource.core.integration.processor.ResourceProcessor;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResourceProcessorImpl implements ResourceProcessor {

    private final ResourceProcessorProperties resourceProcessorProperties;
    private final RabbitTemplate rabbitTemplate;

    @Override
    public void processResource(ResourceEntity resource) {
        String queue = resourceProcessorProperties.getQueue().getProcessResource().getInbound();
        log.debug("Process resource request - {} with id {} to Resource Processor's queue", queue, resource.getId());
        try {
            rabbitTemplate.convertAndSend(queue, new ResourceProcessRequest(resource.getId()));
        } catch (Exception e) {
            log.error("An error occurred while sending request Resource Processor's queue", e);
            throw new RuntimeException(e);
        }
    }
}
