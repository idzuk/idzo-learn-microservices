package ua.idzo.resource.core.service;

import java.util.Set;

import ua.idzo.resource.core.entity.ResourceEntity;

public interface ResourceService {

    ResourceEntity getResource(Integer id);

    ResourceEntity uploadResource(byte[] data);

    Set<Integer> deleteResources(Set<Integer> ids);

    boolean isExists(Integer id);
}
