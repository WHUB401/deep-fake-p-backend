package com.pharaoh.deepfake.controller;

import com.alibaba.fastjson.JSONObject;
import com.pharaoh.deepfake.common.util.DetectionResult;
import com.pharaoh.deepfake.common.util.R;
import com.pharaoh.deepfake.controller.form.DatasetVideoForm;
import com.pharaoh.deepfake.controller.form.FileForm;
import com.pharaoh.deepfake.entity.AdversarialDetection;
import com.pharaoh.deepfake.repository.AdversarialDetectionRepository;
import com.pharaoh.deepfake.service.AdversarialService;
import com.pharaoh.deepfake.service.DeepfakeDetectionService;
import io.micrometer.core.instrument.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.pharaoh.deepfake.DeepfakeApplication.missionStatus;
import static com.pharaoh.deepfake.DeepfakeApplication.myLogger;

@Slf4j
@RestController
@RequestMapping("/adversarial")
public class AdversarialController {
    @Autowired
    AdversarialService adversarialService;
    @Autowired
    AdversarialDetectionRepository adversarialDetectionRepository;

    @GetMapping("/listResultPage")
    public R listResultPage(Pageable pageable,
                            @RequestParam(value = "dataset",required = false) String dataset,
                            @RequestParam(value = "filename",required = false) String filename){
        R r = R.ok("find success");
        r.setData(adversarialService.findResultPage(dataset, filename, pageable));
        return r;
    }

    @GetMapping("/listAllResult")
    public R listAllResult(){
        List findResult = adversarialService.findAllResult();
        R r = R.ok("find success");
        HashMap<String,Object> data = new HashMap<String,Object>();
        data.put("result", findResult);
        r.setData(data);
        return r;
    }

    @GetMapping("/hasData")
    public R hasVideo(@RequestParam("dataset") String dataset,
                      @RequestParam("filename") String filename){
        if(!adversarialService.hasData(dataset, filename)){
            R r = R.error(50441,"has no result");
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

    @GetMapping("/hasResult")
    public R getResult(@RequestParam("dataset") String dataset,
                       @RequestParam("filename") String filename){

        if(!adversarialService.hasResult(dataset, filename)){
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

    @GetMapping("/findResult")
    public R getMediaResult(@RequestParam("dataset") String dataset,
                            @RequestParam("filename") String filename){

        JSONObject jsonObject = null;
        try {
            jsonObject = adversarialService.findResult(dataset, filename);
        } catch (Exception e) {
            R r = R.error(50414, "has no result");
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

    @GetMapping("/findAdvPictureList")
    public R findAdvPictureList(@RequestParam("dataset") String dataset,
                                @RequestParam("filename") String filename){

        JSONObject jsonObject = null;
        try {
            jsonObject = adversarialService.findAdvPictureList(dataset, filename);
        } catch (Exception e) {
            R r = R.error(50431,"has no adv picture");
            HashMap<String,Object> data = new HashMap<String,Object>();
            data.put("hasAdvPicture", false);
            r.setData(data);
            return r;
        }
        R r = R.ok();
        HashMap<String,Object> data = new HashMap<String,Object>();
        data.put("result", jsonObject);
        data.put("hasAdvPicture", true);
        r.setData(data);
        return r;
    }

    @PostMapping("/runDetection")
    public R runDetection(@RequestBody DatasetVideoForm datasetVideo){
        String filename = datasetVideo.getFilename();
        String dataset = datasetVideo.getDataset();
        if(adversarialService.hasResult(dataset,filename)){
            R r = R.error(50412,"has detection result");
            HashMap<String,Object> data = new HashMap<String,Object>();
            data.put("filename", filename);
            r.setData(data);
            return r;
        }
        try {
            DetectionResult detectionResult = adversarialService.detection(dataset,filename);
            missionStatus.setBusy(false);
            if(detectionResult.isDetectionSuccess()){
                adversarialService.setResultTrue(dataset,filename);
                R r = R.ok("detection success");
                HashMap<String,Object> data = new HashMap<String,Object>();
                data.put("success", true);
                data.put("filename", filename);
                r.setData(data);
                return r;
            } else {
                R r = R.error(50411,detectionResult.getMessage());
                HashMap<String,Object> data = new HashMap<String,Object>();
                data.put("filename", filename);
                r.setData(data);
                return r;
            }
        } catch (Exception e) {
            R r = R.error(50421,"detection fail!");
            HashMap<String,Object> data = new HashMap<String,Object>();
            data.put("filename", filename);
            r.setData(data);
            return r;
        } finally {
            missionStatus.setBusy(false);
        }
    }

    @PostMapping("/deleteResult")
    public R deleteResult(@RequestBody DatasetVideoForm datasetVideo) {
        try {
            adversarialService.removeResult(datasetVideo.getDataset(),datasetVideo.getFilename());
        } catch (Exception e) {
            log.error("Adversarial Detection Error!",e);
            return R.error(50413, "delete fail!");
        }
        return R.ok("delete success.");
    }

}
