package ua.idzo.song.core.service;

import java.util.Set;

import ua.idzo.song.core.entity.SongEntity;
import ua.idzo.song.dto.request.CreateSongRequest;

public interface SongService {

    SongEntity getSong(Integer id);

    SongEntity createSong(CreateSongRequest request);

    Set<Integer> deleteSongs(Set<Integer> ids);
}
