package ua.idzo.resource.web.controller;

import java.util.Set;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import ua.idzo.resource.core.entity.ResourceEntity;
import ua.idzo.resource.core.mapper.Mapper;
import ua.idzo.resource.core.service.ResourceService;
import ua.idzo.resource.core.util.FileUtil;
import ua.idzo.resource.dto.response.DeleteResourcesResponse;
import ua.idzo.resource.dto.response.UploadResourceResponse;
import ua.idzo.resource.web.validator.ValidCsvResourcesIds;
import ua.idzo.resource.web.validator.ValidMp3;

@RestController
@RequestMapping("/resources")
@RequiredArgsConstructor
@Validated
public class ResourceRestController {

    private final ResourceService resourceService;

    @GetMapping("/{id}")
    public ResponseEntity<byte[]> getResource(
            @Positive(
                    message = "The provided ID '${validatedValue}' is invalid (e.g., contains letters, decimals, is negative, or zero).")
            @PathVariable Integer id
    ) {
        ResourceEntity resourceEntity = resourceService.getResource(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, FileUtil.AUDIO_MPEG_CONTENT_TYPE)
                .contentLength(resourceEntity.getData().length)
                .body(resourceEntity.getData());
    }

    @PostMapping(consumes = FileUtil.AUDIO_MPEG_CONTENT_TYPE)
    public ResponseEntity<UploadResourceResponse> uploadResource(@ValidMp3 @RequestBody byte[] data) {
        ResourceEntity resourceEntity = resourceService.uploadResource(data);
        return ResponseEntity.ok().body(Mapper.RESOURCE_TO_UPLOAD_RESPONSE.apply(resourceEntity));
    }

    @DeleteMapping
    public ResponseEntity<DeleteResourcesResponse> deleteResources(@ValidCsvResourcesIds @RequestParam String id) {
        Set<Integer> ids = Mapper.CSV_TO_INTEGER_COLLECTION.apply(id);
        Set<Integer> deletedIds = resourceService.deleteResources(ids);
        return ResponseEntity.ok().body(Mapper.RESOURCE_TO_DELETE_RESPONSE.apply(deletedIds));
    }
}
