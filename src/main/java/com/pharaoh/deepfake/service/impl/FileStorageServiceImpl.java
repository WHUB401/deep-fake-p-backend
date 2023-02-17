package com.pharaoh.deepfake.service.impl;

import com.pharaoh.deepfake.common.util.FileCheckReturn;
import com.pharaoh.deepfake.common.util.FileReturn;
import com.pharaoh.deepfake.common.util.InterceptVideoPhoto;
import com.pharaoh.deepfake.common.util.MyFile;
import com.pharaoh.deepfake.entity.Image;
import com.pharaoh.deepfake.entity.Video;
import com.pharaoh.deepfake.exception.FileStorageException;
import com.pharaoh.deepfake.exception.MyFileNotFoundException;
import com.pharaoh.deepfake.property.FileStorageProperties;
import com.pharaoh.deepfake.repository.ImageRepository;
import com.pharaoh.deepfake.repository.VideoRepository;
import com.pharaoh.deepfake.service.FileStorageService;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import static com.pharaoh.deepfake.DeepfakeApplication.myLogger;
import static com.pharaoh.deepfake.common.util.RenameFile.Rename;

@Service
public class FileStorageServiceImpl implements FileStorageService {
    private final Path fileStorageLocation;
    private final Path videoStorageLocation;
    private final Path imageStorageLocation;

    @Autowired
    private VideoRepository videoRepository;
    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    public FileStorageServiceImpl(FileStorageProperties fileStorageProperties) {
        this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir())
                .toAbsolutePath().normalize();
        this.videoStorageLocation = Paths.get(fileStorageProperties.getUploadDir()).resolve("video")
                .toAbsolutePath().normalize();
        this.imageStorageLocation = Paths.get(fileStorageProperties.getUploadDir()).resolve("image")
                .toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.videoStorageLocation);
        } catch (Exception ex) {
            throw new FileStorageException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    public FileReturn storeFile(MultipartFile file, String fileType)throws Exception {
        // Normalize file name
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        Path folderPath = fileStorageLocation.resolve(fileType).normalize();
        // 重命名文件，删除非法字符，若存在同名文件则增加序号
        FileCheckReturn fileCheck = Rename(fileName, fileType);
        if(!fileCheck.isSuccess()){
            return new FileReturn(fileName, false, fileCheck.getMessage() );
        }
        String targetFilename = fileCheck.getTargetFileName();
        Path targetLocation = folderPath.resolve(targetFilename);
        // upload file and write in database.
        try {
            // 拷贝文件到目录
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            // 文件名，不带后缀
            String targetPrefix = targetFilename.substring(0, targetFilename.lastIndexOf("."));
            // 后缀
            String targetSuffix = targetFilename.substring(targetFilename.lastIndexOf("."));
            // 如果是视频则切帧,并添加到视频数据库中
            // 图片则只添加到图片数据库中
            if(fileType.equals("video")){
                InterceptVideoPhoto.fetchFrame(targetLocation,
                        folderPath.resolve(targetPrefix + ".jpg"));
            }
            return new FileReturn(targetFilename, true);
        } catch (Exception ex) {
            myLogger.error("Upload Error!",ex);
            MyFile targetFile = new MyFile(targetLocation.toUri());
            targetFile.deleteRecursively();
            throw ex;
        }
    }

    public boolean updateDatabase(String filename, String fileType){
        if(fileType.equals("video")){
            videoRepository.saveAndFlush(new Video(filename));
            return true;
        }else if(fileType.equals("image")){
            imageRepository.saveAndFlush(new Image(filename));
            return true;
        }
        return false;
    }

    public Resource loadFileAsResource(String subDirectory) {
        try {
            Path filePath = fileStorageLocation.resolve(subDirectory).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if(resource.exists()) {
                return resource;
            } else {
                throw new MyFileNotFoundException("File not found " + subDirectory);
            }
        } catch (MalformedURLException ex) {
            throw new MyFileNotFoundException("File not found " + subDirectory, ex);
        }
    }

    public void removeFile(String fileName, String fileType){
        // 文件名，不带后缀
        String prefix = fileName.substring(0, fileName.lastIndexOf("."));
        Path fileStorageLocation;
        if(fileType.equals("video")){
            // 选取文件储存路径
            fileStorageLocation = videoStorageLocation;
            // 获取图片名称，带后缀
            String pictureName = prefix + ".jpg";
            // 定位文件
            File targetFile = fileStorageLocation.resolve(fileName).toFile();
            File targetPicture = fileStorageLocation.resolve(pictureName).toFile();
            try {
                // 删除数据库和文件
                videoRepository.deleteByFilename(fileName);
                targetPicture.delete();
                targetFile.delete();
            }catch (SecurityException e) {
                throw e;
            }
        }else if(fileType.equals("image")){
            fileStorageLocation = imageStorageLocation;
            File targetFile = fileStorageLocation.resolve(fileName).toFile();
            try {
                imageRepository.deleteByFilename(fileName);
                targetFile.delete();
            }catch (SecurityException e) {
                throw e;
            }
        }
    }

    public void removeAllImage() throws Exception{
        Path fileStorageLocation = imageStorageLocation;
        MyFile targetFile = new MyFile(fileStorageLocation.toUri());
        try {
            imageRepository.deleteAllInBatch();
            targetFile.deleteRecursively();
        }catch (Exception e) {
            throw e;
        }finally {
            targetFile.mkdirs();
        }
    }

    public Path getFileStorageLocation(String subDirectory){
        Path filePath = fileStorageLocation.resolve(subDirectory).normalize();
        return filePath;
    }

}
