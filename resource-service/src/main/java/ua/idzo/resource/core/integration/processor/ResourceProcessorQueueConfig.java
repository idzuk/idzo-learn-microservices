package ua.idzo.resource.core.integration.processor;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static ua.idzo.resource.core.config.RabbitConfig.*;

@Configuration
@RequiredArgsConstructor
public class ResourceProcessorQueueConfig {

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
        return getQueueName().concat("-").concat(DLQ);
    }

    private String getDlxName() {
        return getQueueName().concat("-").concat(DLX);
    }

    private String getDlRoutingKey() {
        return getQueueName().concat("-").concat(DLQ_ROUTING_KEY);
    }

    private String getQueueName() {
        return resourceProcessorProperties.getQueue().getProcessResource().getInbound();
    }
}
