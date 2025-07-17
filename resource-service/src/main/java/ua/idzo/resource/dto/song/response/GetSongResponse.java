package ua.idzo.resource.dto.song.response;

public record GetSongResponse(Integer id, String name, String artist, String album, String duration, String year) {
}
