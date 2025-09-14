package ua.idzo.resource.core.config;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ua.idzo.resource.dto.song.response.DeleteSongResponse;

@FeignClient(name = "${song.service-name}")
public interface SongFeignClient {

    @DeleteMapping("/songs")
    ResponseEntity<DeleteSongResponse> deleteSongs(@RequestParam String id);
}
