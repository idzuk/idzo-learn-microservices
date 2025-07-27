package ua.idzo.resource.core.config;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import ua.idzo.resource.dto.song.request.CreateSongRequest;
import ua.idzo.resource.dto.song.response.CreateSongResponse;
import ua.idzo.resource.dto.song.response.DeleteSongResponse;

@FeignClient(name = "${song.service-name}")
public interface SongFeignClient {

    @PostMapping("/songs")
    ResponseEntity<CreateSongResponse> createSong(@RequestBody CreateSongRequest request);

    @DeleteMapping("/songs")
    ResponseEntity<DeleteSongResponse> deleteSongs(@RequestParam String id);
}
