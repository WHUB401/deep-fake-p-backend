package com.pharaoh.deepfake.controller;

import com.alibaba.fastjson.JSONObject;
import com.pharaoh.deepfake.common.util.DetectionResult;
import com.pharaoh.deepfake.common.util.R;
import com.pharaoh.deepfake.controller.form.FileForm;
import com.pharaoh.deepfake.entity.TraceDetection;
import com.pharaoh.deepfake.service.TraceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import com.alibaba.fastjson.JSON;

import java.util.HashMap;

import static com.pharaoh.deepfake.DeepfakeApplication.missionStatus;

@RestController
@RequestMapping("/trace")
public class TraceController {

    @Autowired
    TraceService traceService;

    @GetMapping("/hasResult")
    public R getResult(@RequestParam("filename") String filename){

        if(!traceService.hasResult(filename)){
            R r = R.error(50501,"has no result");
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
    public R findResult(@RequestParam("filename") String filename){

        JSONObject jsonObject = null;
        try {
            jsonObject = traceService.findResult(filename);
        } catch (Exception e) {
            R r = R.error(50502, "has no result");
            HashMap<String,Object> data = new HashMap<String,Object>();
            data.put("hasResult", false);
            r.setData(data);
            return r;
        }
        R r = R.ok();
        HashMap<String,Object> data = new HashMap<String,Object>();
        for (Map.Entry<String, Object> entry : jsonObject.entrySet()) {
            data.put(entry.getKey(), entry.getValue());
        }
        data.put("hasResult", true);
        r.setData(data);
        return r;
    }

    @PostMapping("/detection")
    public R runForgery(@RequestBody FileForm form){
        String filename = form.getFilename();
        if(traceService.hasResult(filename)){
            R r = R.error(50503,"has result");
            HashMap<String,Object> data = new HashMap<String,Object>();
            data.put("filename", filename);
            r.setData(data);
            return r;
        }
        try {
            DetectionResult detectionResult = traceService.detection(filename);
            missionStatus.setBusy(false);
            if(detectionResult.isDetectionSuccess()){
                traceService.addDatabase(filename);
                R r = R.ok("success");
                HashMap<String,Object> data = new HashMap<String,Object>();
                data.put("success", true);
                data.put("filename", filename);
                r.setData(data);
                return r;
            } else {
                R r = R.error(50504,detectionResult.getMessage());
                HashMap<String,Object> data = new HashMap<String,Object>();
                data.put("filename", filename);
                r.setData(data);
                return r;
            }
        } catch (Exception e) {
            R r = R.error(50505, "detection fail!");
            HashMap<String,Object> data = new HashMap<String,Object>();
            data.put("filename", filename);
            r.setData(data);
            return r;
        } finally {
            missionStatus.setBusy(false);
        }
    }

    @PostMapping("/deleteResult")
    public R deleteResult(@RequestBody FileForm form) {
        try {
            traceService.removeResult(form.getFilename());
        } catch (Exception e) {
            return R.error(50013,"delete fail.");
        }
        return R.ok("delete success.");
    }

    @GetMapping("/listResultPage")
    public R listResultPage(Pageable pageable,
                            @RequestParam(value = "filename",required = false) String filename){
        R r = R.ok("find success");
        r.setData(traceService.findResultPage(filename,pageable));
        return r;
    }

}
