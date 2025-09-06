package ua.idzo.resource.processor.dto.song.response;

public record GetSongResponse(Integer id, String name, String artist, String album, String duration, String year) {
}
