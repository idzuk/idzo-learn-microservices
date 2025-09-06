package ua.idzo.resource.processor.core.config;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ua.idzo.resource.processor.core.config.properties.ResourceProcessorProperties;

@Configuration
@RequiredArgsConstructor
public class ResourceProcessQueueConfig {

    private final ResourceProcessorProperties resourceProcessorProperties;

    @Bean
    public Queue processCfDocumentsInboundQueue() {
        return QueueBuilder.durable(getQueueName())
                .deadLetterExchange(getDlxName())
                .deadLetterRoutingKey(getDlRoutingKey())
                .build();
    }

    @Bean
    public Queue inboundDlq() {
        return QueueBuilder.durable(getDlqName()).build();
    }

    @Bean
    public DirectExchange inboundDlx() {
        return new DirectExchange(getDlxName());
    }

    @Bean
    public Binding inboundDlqBinding() {
        return BindingBuilder.bind(inboundDlq()).to(inboundDlx()).with(getDlRoutingKey());
    }

    private String getDlqName() {
        return getQueueName().concat("-").concat(RabbitConfig.DLQ);
    }

    private String getDlxName() {
        return getQueueName().concat("-").concat(RabbitConfig.DLX);
    }

    private String getDlRoutingKey() {
        return getQueueName().concat("-").concat(RabbitConfig.DLQ_ROUTING_KEY);
    }

    private String getQueueName() {
        return resourceProcessorProperties.getQueue().getProcessResource().getInbound();
    }
}
