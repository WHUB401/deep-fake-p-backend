package com.pharaoh.deepfake.controller;

import com.pharaoh.deepfake.common.util.DetectionResult;
import com.pharaoh.deepfake.common.util.MyFile;
import com.pharaoh.deepfake.common.util.R;
import com.pharaoh.deepfake.controller.form.FileForm;
import com.pharaoh.deepfake.service.FileStorageService;
import com.pharaoh.deepfake.service.ForgeryDetectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;

import static com.pharaoh.deepfake.DeepfakeApplication.missionStatus;

@RestController
@RequestMapping("/forgeryDetection")
public class ForgeryDetectionController {

    @Autowired
    private ForgeryDetectionService forgeryDetectionService;
    @Autowired
    private FileStorageService fileStorageService;

    @GetMapping("/hasResult")
    public R getResult(@RequestParam("filename") String filename){

        if(!forgeryDetectionService.hasResult(filename)){
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

    @GetMapping("/findAll")
    public R forgeryFindAll(){
        List findResult = forgeryDetectionService.findAllResult();
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
        r.setData(forgeryDetectionService.findResultPage(filename,pageable));
        return r;
    }

    @GetMapping("/downloadResult")
    public ResponseEntity<Resource> downloadPicture(@RequestParam("filename") String fileName, HttpServletRequest request) {
        // Load file as Resource
        Resource resource = fileStorageService.loadFileAsResource("result//forgery_detection//" + fileName);

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

    @PostMapping("/runforgery")
    public R runForgery(@RequestParam("filename") String filename){
        if(forgeryDetectionService.hasResult(filename)){
            R r = R.error(50312,"has result");
            HashMap<String,Object> data = new HashMap<String,Object>();
            data.put("filename", filename);
            r.setData(data);
            return r;
        }
        try {
            DetectionResult detectionResult = forgeryDetectionService.detection(filename);
            missionStatus.setBusy(false);
            if(detectionResult.isDetectionSuccess()){
                forgeryDetectionService.addDatabase(filename);
                R r = R.ok("success");
                HashMap<String,Object> data = new HashMap<String,Object>();
                data.put("success", true);
                data.put("filename", filename);
                r.setData(data);
                return r;
            } else {
                R r = R.error(50311,detectionResult.getMessage());
                HashMap<String,Object> data = new HashMap<String,Object>();
                data.put("filename", filename);
                r.setData(data);
                return r;
            }
        } catch (Exception e) {
            R r = R.error(50321,"detection fail!");
            HashMap<String,Object> data = new HashMap<String,Object>();
            data.put("filename", filename);
            r.setData(data);
            return r;
        } finally {
            missionStatus.setBusy(false);
        }
    }

    // 检测所有图片
    @PostMapping("/detectAllImages")
    public R runForgery(){
        try {
            forgeryDetectionService.removeImagesTar();
            DetectionResult detectionResult = forgeryDetectionService.detectionAllImage();
            missionStatus.setBusy(false);
            if(detectionResult.isDetectionSuccess()){
                R r = R.ok("success");
                HashMap<String,Object> data = new HashMap<String,Object>();
                data.put("success", true);
                r.setData(data);
                return r;
            } else {
                R r = R.error(50311,detectionResult.getMessage());
                HashMap<String,Object> data = new HashMap<String,Object>();
                r.setData(data);
                return r;
            }
        } catch (Exception e) {
            R r = R.error(50321,"detection fail!");
            HashMap<String,Object> data = new HashMap<String,Object>();
            r.setData(data);
            return r;
        } finally {
            missionStatus.setBusy(false);
        }
    }

    // 下载所有图片检测结果打包后的文件
    @GetMapping("/downloadImageResult")
    public ResponseEntity<Resource> downloadImageResult(HttpServletRequest request){
        // Load file as Resource
        Resource resource = fileStorageService.loadFileAsResource("result/forgery_detection_all_image.tar");

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

    @PostMapping("/deleteResult")
    public R deleteResult(@RequestBody FileForm form) {
        try {
            forgeryDetectionService.removeResult(form.getFilename());
        } catch (IOException e) {
            return R.error(50013,"delete fail.");
        }
        return R.ok("delete success.");
    }

}
