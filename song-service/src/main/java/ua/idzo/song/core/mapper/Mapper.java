package ua.idzo.song.core.mapper;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.util.StringUtils;

import ua.idzo.song.core.entity.SongEntity;
import ua.idzo.song.dto.response.CreateSongResponse;
import ua.idzo.song.dto.response.DeleteSongResponse;
import ua.idzo.song.dto.response.GetSongResponse;

public class Mapper {

    public final static Function<SongEntity, CreateSongResponse> SONG_TO_CREATE_RESPONSE = song ->
            new CreateSongResponse(song.getId());

    public final static Function<SongEntity, GetSongResponse> SONG_TO_GET_RESPONSE = song ->
            new GetSongResponse(song.getId(), song.getName(), song.getArtist(), song.getAlbum(), song.getDuration(),
                    song.getYear());

    public final static Function<Set<Integer>, DeleteSongResponse> SONG_TO_DELETE_RESPONSE =
            DeleteSongResponse::new;

    public final static Function<String, Set<Integer>> CSV_TO_INTEGER_COLLECTION = value -> {
        if (StringUtils.hasText(value)) {
            return Arrays.stream(value.split(","))
                    .map(String::trim)
                    .map(Integer::parseInt)
                    .collect(Collectors.toSet());
        }
        return Collections.emptySet();
    };
}
