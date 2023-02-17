package com.pharaoh.deepfake.common.util;

import com.pharaoh.deepfake.repository.ImageRepository;
import com.pharaoh.deepfake.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.nio.file.Path;

@Component
public class RenameFile {

    @Autowired
    VideoRepository videoRepository;
    @Autowired
    ImageRepository imageRepository;

    private static VideoRepository staticVideoRepository;
    private static ImageRepository staticImageRepository;

//    public RenameFile(VideoRepository videoRepository, ImageRepository imageRepository) {
//        this.videoRepository = videoRepository;
//        this.imageRepository = imageRepository;
//    }

    @PostConstruct
    public void init() {
        staticVideoRepository = videoRepository;
        staticImageRepository = imageRepository;
    }

    public static FileCheckReturn Rename(String fileName, String fileType){
        String message;
        String prefix = fileName.substring(0, fileName.lastIndexOf("."));
        String suffix = fileName.substring( fileName.lastIndexOf(".") );
        FileCheckReturn r = new FileCheckReturn();
//        if(prefix.length() > 20){
//            prefix = prefix.substring(0, 20);
//        }
        // change illegal char
        String reg = "[%&#=\\\\\\/\\+\\s\\?]";
        prefix = prefix.replaceAll(reg,"_");
        // check whether filename contains ".."
//        if(prefix.contains("..")) {
//            message = "Sorry! Filename contains invalid path sequence " + fileName;
//            r.setSuccess(false);
//            r.setMessage(message);
//            return r;
//        }
        String targetPrefix = prefix;
        fileName = targetPrefix + suffix;
        Integer i = 1;
        // check filename, if file exists, add suffix such as "(1)".
        while( (fileType.equals("video") && staticVideoRepository.existsByFilename(fileName))
        || (fileType.equals("image") && staticImageRepository.existsByFilename(fileName))){
            if ( i == 101 ) {
                message = "Can't upload file " + fileName;
                r.setSuccess(false);
                r.setMessage(message);
                return r;
            }
            targetPrefix = String.valueOf(new StringBuilder(prefix + "(" + i + ")"));
            fileName = targetPrefix + suffix;
            i++;
        }
        message = "success";
        r.setSuccess(true);
        r.setMessage(message);
        r.setTargetFileName(fileName);
        return r;
    }
}
