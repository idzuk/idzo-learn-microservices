package ua.idzo.resource.processor.core.config.properties;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "resource-processor")
@Getter
@Setter
public class ResourceProcessorProperties {

    private Queue queue;

    @Data
    public static class Queue {
        private ProcessResource processResource;
    }

    @Data
    public static class ProcessResource {
        private String inbound;
    }
}
