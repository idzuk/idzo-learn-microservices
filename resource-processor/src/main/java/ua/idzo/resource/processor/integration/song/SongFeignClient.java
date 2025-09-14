package ua.idzo.resource.processor.integration.song;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ua.idzo.resource.processor.dto.song.request.CreateSongRequest;
import ua.idzo.resource.processor.dto.song.response.CreateSongResponse;

@FeignClient(name = "${song.service-name}")
public interface SongFeignClient {

    @PostMapping("/songs")
    ResponseEntity<CreateSongResponse> createSong(@RequestBody CreateSongRequest request);
}
