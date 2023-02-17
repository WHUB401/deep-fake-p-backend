package com.pharaoh.deepfake.service;

import com.pharaoh.deepfake.common.util.FileReturn;
import com.pharaoh.deepfake.common.util.R;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;

public interface FileStorageService {
    public boolean updateDatabase(String filename, String fileType);
    public FileReturn storeFile(MultipartFile file, String fileType)throws Exception;
    public Resource loadFileAsResource(String fileName);
    public void removeFile(String fileName, String fileType);
    public void removeAllImage()throws Exception;
    public Path getFileStorageLocation(String subDirectory);
}
