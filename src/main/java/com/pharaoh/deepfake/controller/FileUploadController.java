package com.pharaoh.deepfake.controller;

import com.pharaoh.deepfake.common.util.FileReturn;
import com.pharaoh.deepfake.common.util.R;
import com.pharaoh.deepfake.service.FileStorageService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URLDecoder;
import java.util.HashMap;

import static com.pharaoh.deepfake.common.util.FileTypeTest.FileIsNotImage;
import static com.pharaoh.deepfake.common.util.FileTypeTest.FileIsNotVideo;

@RestController
public class FileUploadController {

    @Autowired
    private FileStorageService fileStorageService;

    @SneakyThrows
    @PostMapping("/uploadFile")
    public R uploadVideo(@RequestParam("file") MultipartFile file,
                         @RequestParam("fileType") String fileType) {
        String fileName = StringUtils.cleanPath(URLDecoder.decode(file.getOriginalFilename(),"UTF-8"));
        if( (fileType.equals("video") && !FileIsNotVideo(fileName))
                || (fileType.equals("image") && !FileIsNotImage(fileName)) ){
            FileReturn fileReturn = null;
            try {
                fileReturn = fileStorageService.storeFile(file, fileType);
            } catch (Exception e) {
                R r = R.error(50101, "unknown error.");
                return r;
            }
            if (fileReturn.isUploadSuccess() &&
                    fileStorageService.updateDatabase(fileReturn.getFilePath(), fileType)){
                R r = R.ok("upload success");
                String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                        .path("/downloadFile/")
                        .path(fileReturn.getFilePath())
                        .toUriString();
                HashMap<String,String> data = new HashMap<String,String>();
                data.put("fileName", fileReturn.getFilePath());
                data.put("fileDownloadUri", fileDownloadUri);
                r.setData(data);
                return r;
            }
            R r = R.error(50101, fileReturn.getErrorMessage());
            return r;
        }
        R r = R.error(50101, "file type error.");
        return r;
    }

}
