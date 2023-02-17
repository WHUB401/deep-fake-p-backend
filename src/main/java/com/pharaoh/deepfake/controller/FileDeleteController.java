package com.pharaoh.deepfake.controller;

import com.pharaoh.deepfake.common.util.R;
import com.pharaoh.deepfake.service.FileStorageService;
import com.pharaoh.deepfake.service.DeepfakeDetectionService;
import com.pharaoh.deepfake.service.ForgeryDetectionService;
import com.pharaoh.deepfake.service.TraceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/delete")
public class FileDeleteController {
    @Autowired
    private FileStorageService fileStorageService;
    @Autowired
    private DeepfakeDetectionService deepfakeDetectionService;
    @Autowired
    private ForgeryDetectionService forgeryDetectionService;
    @Autowired
    private TraceService traceService;

    @DeleteMapping("/file")
    public R deleteFile(@RequestParam("fileName") String fileName,
                        @RequestParam("fileType") String fileType){
        try {
            if(fileType.equals("video")){
                deepfakeDetectionService.removeResult(fileName);
            }
            if(fileType.equals("image")){
                forgeryDetectionService.removeResult(fileName);
                traceService.removeResult(fileName);
            }
            fileStorageService.removeFile(fileName, fileType);
        }catch (Exception e){
            return R.error(50051,"delete error.");
        }
        return R.ok("delete '" + fileName + "' success.");
    }

    @DeleteMapping("/allImage")
    public R deleteAllImage(){
        try {
            forgeryDetectionService.removeAllResult();
            traceService.removeAllResult();
            fileStorageService.removeAllImage();
        }catch (Exception e){
            return R.error(50051,"delete error.");
        }
        return R.ok("delete images success.");
    }
}
