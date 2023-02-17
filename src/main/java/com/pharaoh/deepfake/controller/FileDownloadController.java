package com.pharaoh.deepfake.controller;

import com.pharaoh.deepfake.common.util.R;
import com.pharaoh.deepfake.entity.AdversarialDetection;
import com.pharaoh.deepfake.repository.AdversarialDetectionRepository;
import com.pharaoh.deepfake.service.DatasetService;
import com.pharaoh.deepfake.service.FileStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;

@RestController
@RequestMapping("/downloadFile")
public class FileDownloadController {

    @Autowired
    private FileStorageService fileStorageService;
    @Autowired
    private DatasetService datasetService;
    @Autowired
    AdversarialDetectionRepository adversarialDetectionRepository;

    @GetMapping("/material")
    public ResponseEntity<Resource> downloadVideos(@RequestParam String fileName, HttpServletRequest request,
                                                   @RequestParam String fileType) {

        // Load file as Resource
        Resource resource = fileStorageService.loadFileAsResource(fileType + "//" + fileName);
        return commonProcess(resource, request);
    }

    @GetMapping("/dataset")
    public ResponseEntity<Resource> downloadVideos(@RequestParam("datasetName") String datasetName,
                                                   @RequestParam("datasetType") String datasetType,
                                                   @RequestParam("filename") String filename,
                                                   @RequestParam("isVideo") boolean isVideo,
                                                   HttpServletRequest request){
        // Load file as Resource
        Resource resource;
        if(isVideo){
            resource = datasetService.loadFileAsResource(datasetName,filename,datasetType);
        }else{
            resource = fileStorageService.loadFileAsResource("dataset//" + datasetType
                    + "//" + datasetName
                    + "//" + filename);
        }
        return commonProcess(resource, request);

    }

    @GetMapping("/adversarial/result")
    public ResponseEntity<Resource> downloadAdversarialPicture(@RequestParam("dataset") String dataset,
                                                               @RequestParam("filename") String filename,
                                                               @RequestParam("type") String type,
                                                               @RequestParam("picture") String picture,
                                                               HttpServletRequest request){
        // Load file as Resource
        Resource resource = fileStorageService.loadFileAsResource("result//adversarial_detection//"
                + dataset + "//"
                + filename + "//"
                + type + "//"
                + picture + "//");

        return commonProcess(resource, request);
    }

    @GetMapping("/adversarial/advFile")
    public ResponseEntity<Resource> downloadAdversarialVideo(@RequestParam("dataset") String dataset,
                                                             @RequestParam("filename") String filename,
                                                             @RequestParam("picture") String picture,
                                                             HttpServletRequest request){
        AdversarialDetection adversarialDetection = adversarialDetectionRepository.findAdversarialDetectionByDatasetAndFilename(dataset,filename);
        String advTargetPath = adversarialDetection.getAdvtargetpath();
        // Load file as Resource
        Resource resource = fileStorageService.loadFileAsResource(advTargetPath + "//" + picture);

        return commonProcess(resource, request);
    }

    @GetMapping("/missionLog")
    public ResponseEntity<Resource> downloadMissionLog(@RequestParam("mission") String mission,
                                                   HttpServletRequest request){
        // Load file as Resource
        Resource resource = null;
        String filePath = null;
        HashMap<String,Object> data = new HashMap<String,Object>();
        R r = null;
        switch (mission){
            case "adversarial":
                filePath = "pycommond//adversarial_detection.log";
                break;
            case "deepfake":
                filePath = "pycommond//deepfake_detection.log";
                break;
            case "forgery":
                filePath = "pycommond//forgery_detection.log";
                break;
            case "trace":
                filePath = "pycommond//trace_detection.log";
                break;
            default:
                return commonProcess(resource, request, "text/plain");
        }
        resource = fileStorageService.loadFileAsResource(filePath);
        return commonProcess(resource, request, "text/plain");

    }

    private ResponseEntity<Resource> commonProcess(Resource resource, HttpServletRequest request){
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

    private ResponseEntity<Resource> commonProcess(Resource resource, HttpServletRequest request, String contentType){
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
}
