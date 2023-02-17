package com.pharaoh.deepfake.controller;

import com.alibaba.fastjson.JSONObject;
import com.pharaoh.deepfake.common.util.DetectionResult;
import com.pharaoh.deepfake.common.util.R;
import com.pharaoh.deepfake.controller.form.FileForm;
import com.pharaoh.deepfake.service.DeepfakeDetectionService;
import com.pharaoh.deepfake.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.HashMap;
import java.util.List;

import static com.pharaoh.deepfake.DeepfakeApplication.missionStatus;
import static com.pharaoh.deepfake.DeepfakeApplication.myLogger;

@RestController
@RequestMapping("/dfDetection")
public class DeepfakeDetectionController {
    @Autowired
    private FileStorageService fileStorageService;
    @Autowired
    private DeepfakeDetectionService deepfakeDetectionService;

    @GetMapping("/findAll")
    public R dfFindAll(){
        List findResult = deepfakeDetectionService.findAllResult();
        R r = R.ok("find success");
        HashMap<String,Object> data = new HashMap<String,Object>();
        data.put("result", findResult);
        r.setData(data);
        return r;
    }

    @GetMapping("/listResultPage")
    public R listResultPage(Pageable pageable,
                            @RequestParam(value = "filename",required = false) String filename){
        R r = R.ok("find success");
        r.setData(deepfakeDetectionService.findResultPage(filename,pageable));
        return r;
    }

    @GetMapping({"/hasResult"})
    public R getResult(@RequestParam String filename){

        if(!deepfakeDetectionService.hasResult(filename)){
            R r = R.ok("has no result");
            HashMap<String,Object> data = new HashMap<String,Object>();
            data.put("hasResult", false);
            r.setData(data);
            return r;
        }
        R r = R.ok();
        HashMap<String,Object> data = new HashMap<String,Object>();
        data.put("hasResult", true);
        r.setData(data);
        return r;

    }

    @GetMapping("/mediaResult")
    public R getMediaResult(@RequestParam("mediaName") String mediaName){

        JSONObject jsonObject = null;
        try {
            jsonObject = deepfakeDetectionService.getMediaResult(mediaName);
        } catch (Exception e) {
            R r = R.error(50014,"has no result");
            HashMap<String,Object> data = new HashMap<String,Object>();
            data.put("hasResult", false);
            r.setData(data);
            return r;
        }
        R r = R.ok();
        HashMap<String,Object> data = new HashMap<String,Object>();
        data.put("result", jsonObject);
        data.put("hasResult", true);
        r.setData(data);
        return r;
    }

    @GetMapping("/download")
    public ResponseEntity<Resource> downloadDeepfakeDetectionResult(HttpServletRequest request,
                                                                    @RequestParam("mediaName") String mediaName,
                                                                    @RequestParam("pictureName") String pictureName){
        String mediaPrefix = mediaName.substring(0,mediaName.lastIndexOf('.'));
        String filePathString = "result//deepfake_detection//" + mediaPrefix + "//" + pictureName;

        // Load file as Resource
        Resource resource = fileStorageService.loadFileAsResource(filePathString);

        // Try to determine file's content type
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
        }

        // Fallback to the default content type if type could not be determined
        if(contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @PostMapping("/runDetection")
    public R videoDetection(@RequestBody FileForm form) {
        String filename = form.getFilename();
        if(deepfakeDetectionService.hasResult(filename)){
            R r = R.error(50012,"has detection result");
            HashMap<String,Object> data = new HashMap<String,Object>();
            data.put("filename", filename);
            r.setData(data);
            return r;
        }
        try {
            DetectionResult detectionResult = deepfakeDetectionService.detection(filename);
            if(detectionResult.isDetectionSuccess()){
                deepfakeDetectionService.addDatabase(filename);
                R r = R.ok("detection success");
                HashMap<String,Object> data = new HashMap<String,Object>();
                data.put("success", true);
                data.put("filename", filename);
                r.setData(data);
                return r;
            } else {
                R r = R.error(50011,detectionResult.getMessage());
                HashMap<String,Object> data = new HashMap<String,Object>();
                data.put("filename", filename);
                r.setData(data);
                return r;
            }
        } catch (Exception e) {
            R r = R.error(50021,"detection fail!");
            HashMap<String,Object> data = new HashMap<String,Object>();
            data.put("filename", filename);
            r.setData(data);
            return r;
        } finally {
            missionStatus.setBusy(false);
        }
    }

    // 检测所有图片
    @PostMapping("/DetectionImage")
    public R imageDetection() {
        try {
            DetectionResult detectionResult = deepfakeDetectionService.detection_image();
            if(detectionResult.isDetectionSuccess()){
                R r = R.ok("detection success");
                HashMap<String,Object> data = new HashMap<String,Object>();
                data.put("success", true);
                r.setData(data);
                return r;
            } else {
                R r = R.error(50015,detectionResult.getMessage());
                HashMap<String,Object> data = new HashMap<String,Object>();
                r.setData(data);
                return r;
            }
        } catch (Exception e) {
            R r = R.error(50021,"detection fail!");
            HashMap<String,Object> data = new HashMap<String,Object>();
            r.setData(data);
            return r;
        } finally {
            missionStatus.setBusy(false);
        }
    }

    // 下载图片检测结果
    @GetMapping("/downloadImageResult")
    public ResponseEntity<Resource> downloadImageResult(HttpServletRequest request){
        // Load file as Resource
        Resource resource = fileStorageService.loadFileAsResource("result/deepfake_detection_image/result.txt");

        // Try to determine file's content type
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
        }

        // Fallback to the default content type if type could not be determined
        if(contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @PostMapping("deleteResult")
    public R deleteResult(@RequestBody FileForm form) {
        try {
            deepfakeDetectionService.removeResult(form.getFilename());
        } catch (IOException e) {
            return R.error(50013,"delete fail.");
        }
        return R.ok("delete success.");
    }
}
