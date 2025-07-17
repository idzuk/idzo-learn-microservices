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

@FeignClient(name = "song", url = "${song.feign.path}/songs")
public interface SongFeignClient {

    @PostMapping
    ResponseEntity<CreateSongResponse> createSong(@RequestBody CreateSongRequest request);

    @DeleteMapping
    ResponseEntity<DeleteSongResponse> deleteSongs(@RequestParam String id);
}
