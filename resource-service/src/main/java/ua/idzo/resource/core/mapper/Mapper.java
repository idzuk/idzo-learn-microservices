package ua.idzo.resource.core.mapper;

import java.util.Arrays;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import ua.idzo.resource.core.entity.ResourceEntity;
import ua.idzo.resource.dto.response.DeleteResourcesResponse;
import ua.idzo.resource.dto.response.UploadResourceResponse;

public class Mapper {

    public final static Function<ResourceEntity, UploadResourceResponse> RESOURCE_TO_UPLOAD_RESPONSE = resource ->
            new UploadResourceResponse(resource.getId());

    public final static Function<Set<Integer>, DeleteResourcesResponse> RESOURCE_TO_DELETE_RESPONSE =
            DeleteResourcesResponse::new;

    public final static Function<String, Set<Integer>> CSV_TO_INTEGER_COLLECTION = value ->
            Arrays.stream(value.split(","))
                    .map(String::trim)
                    .map(Integer::parseInt)
                    .collect(Collectors.toSet());
}
