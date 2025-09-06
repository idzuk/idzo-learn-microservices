package ua.idzo.resource.core.service;

import ua.idzo.resource.core.dto.UploadFileDTO;

public interface FileStorage {
    UploadFileDTO uploadFile(String key, byte[] file);

    byte[] downloadFile(String key);

    void deleteFile(String key);
}
