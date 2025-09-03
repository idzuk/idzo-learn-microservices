package ua.idzo.resource.processor.dto.song.request;

public record CreateSongRequest(Integer id, String name, String artist, String album, String duration, String year) {
}
