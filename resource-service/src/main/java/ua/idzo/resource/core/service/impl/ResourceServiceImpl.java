package ua.idzo.resource.core.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.idzo.resource.core.config.SongFeignClient;
import ua.idzo.resource.core.entity.ResourceEntity;
import ua.idzo.resource.core.exception.NotFoundRuntimeException;
import ua.idzo.resource.core.repository.ResourceRepository;
import ua.idzo.resource.core.service.FileStorage;
import ua.idzo.resource.core.service.ResourceService;
import ua.idzo.resource.core.util.MetadataExtractor;
import ua.idzo.resource.dto.song.request.CreateSongRequest;
import ua.idzo.resource.dto.song.response.CreateSongResponse;
import ua.idzo.resource.dto.song.response.DeleteSongResponse;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ResourceServiceImpl implements ResourceService {

    private final SongFeignClient songFeignClient;
    private final ResourceRepository resourceRepository;
    private final FileStorage fileStorage;

    @Override
    public byte[] getResourceData(Integer id) {
        ResourceEntity resource = resourceRepository.findById(id)
                .orElseThrow(() -> new NotFoundRuntimeException("Resource with ID=%s not found.".formatted(id)));
        return fileStorage.downloadFile(resource.getLocation());
    }

    @Override
    @Transactional
    public ResourceEntity uploadResource(byte[] data) {
        String s3Key = UUID.randomUUID().toString();
        ResourceEntity resource = new ResourceEntity();

        try {
            fileStorage.uploadFile(s3Key, data);
            resource.setLocation(s3Key);
            resourceRepository.save(resource);

            CreateSongRequest songRequest = buildCreateSongRequest(resource.getId(), data);
            ResponseEntity<CreateSongResponse> song = songFeignClient.createSong(songRequest);
            if (!song.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("Something went wrong at creating song metadata!");
            }

        } catch (Exception e) {
            fileStorage.deleteFile(s3Key);
        }

        return resource;
    }

    @Override
    @Transactional
    public Set<Integer> deleteResources(Set<Integer> ids) {
        List<ResourceEntity> resources = resourceRepository.findAllById(ids);
        String resourcesIds = String.join(",", resources.stream()
                .map(resource -> String.valueOf(resource.getId()))
                .toList());
        resourceRepository.deleteAll(resources);

        ResponseEntity<DeleteSongResponse> deletedSongs = songFeignClient.deleteSongs(resourcesIds);
        if (!deletedSongs.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Something went wrong at deleting related songs");
        }

        resources.forEach(resource -> fileStorage.deleteFile(resource.getLocation()));

        return ids.stream()
                .filter(resourceId -> resources.stream().anyMatch(resource -> resource.getId().equals(resourceId)))
                .collect(Collectors.toSet());
    }

    @Override
    public boolean isExists(Integer id) {
        return resourceRepository.existsById(id);
    }

    private static CreateSongRequest buildCreateSongRequest(Integer resourceId, byte[] resourceBytes) {
        MetadataExtractor metadataExtractor = MetadataExtractor.getExtractor(resourceBytes);
        return new CreateSongRequest(resourceId,
                metadataExtractor.getName(), metadataExtractor.getArtist(), metadataExtractor.getAlbum(),
                metadataExtractor.getDuration(), metadataExtractor.getYear());
    }
}
