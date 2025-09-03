package ua.idzo.resource.processor.integration.song;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(FeignAutoConfiguration.class)
@EnableFeignClients
public class SongFeignClientAutoConfiguration {
}
