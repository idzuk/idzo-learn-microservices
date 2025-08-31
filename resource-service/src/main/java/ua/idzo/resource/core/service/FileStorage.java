package ua.idzo.resource.core.service;

public interface FileStorage {
    void uploadFile(String key, byte[] file);

    byte[] downloadFile(String key);

    void deleteFile(String key);
}
