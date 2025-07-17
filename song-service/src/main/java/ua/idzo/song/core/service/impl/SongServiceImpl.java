package ua.idzo.song.core.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import ua.idzo.song.core.entity.SongEntity;
import ua.idzo.song.core.exception.ConflictRuntimeException;
import ua.idzo.song.core.exception.NotFoundRuntimeException;
import ua.idzo.song.core.repository.SongRepository;
import ua.idzo.song.core.service.SongService;
import ua.idzo.song.dto.request.CreateSongRequest;

@Service
@RequiredArgsConstructor
public class SongServiceImpl implements SongService {

    private final SongRepository songRepository;

    @Override
    public SongEntity getSong(Integer id) {
        return songRepository.findById(id)
                .orElseThrow(() -> new NotFoundRuntimeException("Song with ID=%s not found.".formatted(id)));
    }

    @Override
    @Transactional
    public SongEntity createSong(CreateSongRequest request) {
        Optional<SongEntity> existedSong = songRepository.findById(request.id());
        if (existedSong.isPresent()) {
            throw new ConflictRuntimeException("Metadata for this ID=%s already exists.".formatted(request.id()));
        }

        SongEntity songEntity = new SongEntity();
        songEntity.setId(request.id());
        songEntity.setYear(request.year());
        songEntity.setName(request.name());
        songEntity.setArtist(request.artist());
        songEntity.setAlbum(request.album());
        songEntity.setDuration(request.duration());
        songRepository.save(songEntity);
        return songEntity;
    }

    @Override
    @Transactional
    public Set<Integer> deleteSongs(Set<Integer> ids) {
        List<SongEntity> songs = songRepository.findAllById(ids);
        songRepository.deleteAll(songs);

        return ids.stream()
                .filter(id -> songs.stream().anyMatch(song -> song.getId().equals(id)))
                .collect(Collectors.toSet());
    }

}
