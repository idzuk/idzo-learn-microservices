package ua.idzo.song.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ua.idzo.song.core.entity.SongEntity;

@Repository
public interface SongRepository extends JpaRepository<SongEntity, Integer> {
}
