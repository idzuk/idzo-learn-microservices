package ua.idzo.resource.core.service.impl;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import ua.idzo.resource.core.config.SongFeignClient;
import ua.idzo.resource.core.entity.ResourceEntity;
import ua.idzo.resource.core.exception.NotFoundRuntimeException;
import ua.idzo.resource.core.repository.ResourceRepository;
import ua.idzo.resource.core.service.ResourceService;
import ua.idzo.resource.dto.song.request.CreateSongRequest;
import ua.idzo.resource.dto.song.response.CreateSongResponse;
import ua.idzo.resource.dto.song.response.DeleteSongResponse;

@Service
@RequiredArgsConstructor
public class ResourceServiceImpl implements ResourceService {

    private final SongFeignClient songFeignClient;
    private final ResourceRepository resourceRepository;

    @Override
    public ResourceEntity getResource(Integer id) {
        return resourceRepository.findById(id)
                .orElseThrow(() -> new NotFoundRuntimeException("Resource with ID=%s not found.".formatted(id)));
    }

    @Override
    @Transactional
    public ResourceEntity uploadResource(byte[] data) {
        ResourceEntity resourceEntity = new ResourceEntity();
        resourceEntity.setData(data);
        ResourceEntity resource = resourceRepository.save(resourceEntity);
        ResponseEntity<CreateSongResponse> song = songFeignClient.createSong(buildCreateSongRequest(resource));
        if (song.getStatusCode().is2xxSuccessful()) {
            return resource;
        }

        throw new RuntimeException("Something went wrong at creating song metadata!");
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

        if (deletedSongs.getStatusCode().is2xxSuccessful()) {
            return ids.stream()
                    .filter(resourceId -> resources.stream().anyMatch(resource -> resource.getId().equals(resourceId)))
                    .collect(Collectors.toSet());
        }

        throw new RuntimeException("Something went wrong at deleting related songs");
    }

    @Override
    public boolean isExists(Integer id) {
        return resourceRepository.existsById(id);
    }

    private static CreateSongRequest buildCreateSongRequest(ResourceEntity resource) {
        SongMetadataExtractor metadataExtractor = new SongMetadataExtractor(resource.getData());
        return new CreateSongRequest(resource.getId(),
                metadataExtractor.getName(), metadataExtractor.getArtist(), metadataExtractor.getAlbum(),
                metadataExtractor.getDuration(), metadataExtractor.getYear());
    }
}
