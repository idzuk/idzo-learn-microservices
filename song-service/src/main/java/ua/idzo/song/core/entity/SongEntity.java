package ua.idzo.song.core.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "song")
@Getter
@Setter
@NoArgsConstructor
public class SongEntity {

    @Id
    @Column(name = "id")
    private Integer id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "artist", nullable = false, length = 100)
    private String artist;

    @Column(name = "album", nullable = false, length = 100)
    private String album;

    @Column(name = "duration", nullable = false, length = 5)
    private String duration;

    @Column(name = "year", nullable = false)
    private String year;
}
