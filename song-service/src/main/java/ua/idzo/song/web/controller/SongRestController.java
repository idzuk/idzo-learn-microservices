package ua.idzo.song.web.controller;

import java.util.Set;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ua.idzo.song.core.entity.SongEntity;
import ua.idzo.song.core.mapper.Mapper;
import ua.idzo.song.core.service.SongService;
import ua.idzo.song.dto.request.CreateSongRequest;
import ua.idzo.song.dto.response.CreateSongResponse;
import ua.idzo.song.dto.response.DeleteSongResponse;
import ua.idzo.song.dto.response.GetSongResponse;
import ua.idzo.song.web.validator.ValidCsvResourcesIds;

@RestController
@RequestMapping("/songs")
@RequiredArgsConstructor
@Validated
public class SongRestController {

    private final SongService songService;

    @GetMapping("/{id}")
    public ResponseEntity<GetSongResponse> getSong(@PathVariable Integer id) {
        SongEntity songEntity = songService.getSong(id);
        return ResponseEntity.ok(Mapper.SONG_TO_GET_RESPONSE.apply(songEntity));
    }

    @PostMapping
    public ResponseEntity<CreateSongResponse> createSong(@Valid @RequestBody CreateSongRequest request) {
        SongEntity songEntity = songService.createSong(request);
        return ResponseEntity.ok(Mapper.SONG_TO_CREATE_RESPONSE.apply(songEntity));
    }

    @DeleteMapping
    public ResponseEntity<DeleteSongResponse> deleteSongs(@ValidCsvResourcesIds @RequestParam String id) {
        Set<Integer> ids = Mapper.CSV_TO_INTEGER_COLLECTION.apply(id);
        Set<Integer> deletedIds = songService.deleteSongs(ids);
        return ResponseEntity.ok(Mapper.SONG_TO_DELETE_RESPONSE.apply(deletedIds));
    }
}
