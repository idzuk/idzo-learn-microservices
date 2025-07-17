package ua.idzo.resource.dto.song.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateSongRequest(
        Integer id,

        @NotBlank(message = "Song name cannot be blank.")
        @Size(min = 1, max = 100, message = "Song name must be between {min} and {max} characters.")
        String name,

        @NotBlank(message = "Artist name cannot be blank.")
        @Size(min = 1, max = 100, message = "Artist name must be between {min} and {max} characters.")
        String artist,

        @NotBlank(message = "Album name cannot be blank.")
        @Size(min = 1, max = 100, message = "Album name must be between {min} and {max} characters.")
        String album,

        @NotBlank(message = "Duration cannot be blank.")
        @Pattern(regexp = "^[0-5][0-9]:[0-5][0-9]$", message = "Duration must be in 'mm:ss' format (e.g., 01:45). You provided '${validatedValue}'.")
        String duration,

        @NotBlank(message = "Year cannot be blank.")
        @Pattern(regexp = "^(19|20)\\d{2}$", message = "Year must be a four-digit number between 1900 and 2099. You provided '${validatedValue}'.")
        String year
) {
}
