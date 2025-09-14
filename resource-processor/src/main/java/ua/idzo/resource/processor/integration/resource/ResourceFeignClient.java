package ua.idzo.resource.processor.integration.resource;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "${resource.service-name}")
public interface ResourceFeignClient {

    @GetMapping("/resources/{id}")
    ResponseEntity<byte[]> getResource(@PathVariable Integer id);
}
