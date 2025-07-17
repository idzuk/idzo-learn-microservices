package ua.idzo.song.dto.response;

public record GetSongResponse(Integer id, String name, String artist, String album, String duration, String year) {
}
