package com.pharaoh.deepfake.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.pharaoh.deepfake.common.util.DetectionResult;
import com.pharaoh.deepfake.common.util.InputStreamRunnable;
import com.pharaoh.deepfake.common.util.MyFile;
import com.pharaoh.deepfake.entity.AdversarialDetection;
import com.pharaoh.deepfake.entity.Forgerydetection;
import com.pharaoh.deepfake.entity.TraceDetection;
import com.pharaoh.deepfake.property.FileStorageProperties;
import com.pharaoh.deepfake.repository.TraceDetectionRepository;
import com.pharaoh.deepfake.service.FileStorageService;
import com.pharaoh.deepfake.service.TraceService;
import io.micrometer.core.instrument.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.pharaoh.deepfake.DeepfakeApplication.missionStatus;
import static com.pharaoh.deepfake.DeepfakeApplication.myLogger;
import static com.pharaoh.deepfake.common.util.readJsonData.readJsonData;

@Service
public class TraceImpl implements TraceService {

    @Autowired
    TraceDetectionRepository traceDetectionRepository;
    @Autowired
    FileStorageService fileStorageService;

    public boolean hasResult(String filename){
        return traceDetectionRepository.existsByFilename(filename);
    }

    public JSONObject findResult(String filename)throws Exception{
        if(!hasResult(filename)){
            throw new Exception();
        }
        TraceDetection traceDetection = traceDetectionRepository.findTraceDetectionByFilename(filename);
        Path resultDir = fileStorageService.getFileStorageLocation(traceDetection.getResult());
        JSONObject resultJson;
        try {
            resultJson = JSONObject.parseObject(readJsonData(resultDir));
        } catch (IOException e) {
            myLogger.error("Adversarial Detection Error!",e);
            throw new IOException();
        }
        return resultJson;
    }

    public DetectionResult detection(String filename)throws Exception{
        System.out.println("isBusy: " + missionStatus.isBusy());
        System.out.println("Work"+ missionStatus.getWork());
        if(missionStatus.isBusy()){
            String message = missionStatus.getWork() + " is running, please wait!";
            return new DetectionResult(false, message);
        }
        missionStatus.setBusy(true);
        missionStatus.setWork("TraceDetection");

        // 如果目标目录不存在，则新建目录
        Path targetFolderPath = fileStorageService.getFileStorageLocation("workspace/trace_detection").normalize();
        MyFile targetFolder = new MyFile(targetFolderPath.toUri());
        if (!targetFolder.exists()) {
            targetFolder.mkdirs();
        }

        String sourceSubDirectory = "image//" + filename;
        String targetSubDirectory = "workspace//trace_detection//" + filename;
        String resultFileStr = "result//trace_detection//"
                + filename.substring(0,filename.lastIndexOf('.'))
                + ".json";
        String shellCommondfileStr = "trace_detection.sh";
        String workplaceStr = "storage//pycommond";


        Path sourcefilePath = fileStorageService.getFileStorageLocation(sourceSubDirectory);
        Path targetfilePath = fileStorageService.getFileStorageLocation(targetSubDirectory);
        Path resultfilePath = fileStorageService.getFileStorageLocation(resultFileStr);
        Path wrokplacePath = Paths.get(workplaceStr).normalize();

        File targetFile = new File(targetfilePath.toUri());
        File resultFile = new File(resultfilePath.toUri());
        File workplaceDir = new File(wrokplacePath.toUri());

        try (FileOutputStream os = new FileOutputStream(targetFile)){//被复制到此文件中)
            // 拷贝待检测文件到目标目录
            Resource resource = new UrlResource(sourcefilePath.toUri());
            FileCopyUtils.copy(resource.getInputStream(),os);

            // 运行通用篡改检测脚本
            Runtime run = Runtime.getRuntime();
            String commond[] = {"bash", shellCommondfileStr};
            Process process = run.exec(commond, null, workplaceDir);
            new InputStreamRunnable(process.getErrorStream(), "Error").start();
            new InputStreamRunnable(process.getInputStream(), "Info").start();
            boolean code = process.waitFor(30, TimeUnit.MINUTES);
            System.out.println("执行计算结果：code-" + code);
            process.waitFor(); // 等待程序运行结束
        } catch (Exception e) {
            myLogger.error("DeefakeDetection Error!",e);
            throw e;
        } finally {
            targetFolder.deleteRecursively();
            missionStatus.setBusy(false);
        }
        missionStatus.setBusy(false);
        if(resultFile.exists()){
            return new DetectionResult(true,"detection success!");
        }else{
            return new DetectionResult(false,"unknown error.");
        }
    }

    public void addDatabase(String filename){
        String result = "result//trace_detection//"
                + filename.substring(0, filename.lastIndexOf('.'))
                + ".json";
        traceDetectionRepository.saveAndFlush(new TraceDetection(filename, result));
    }

    public void removeResult(String filename) throws Exception{
        String pictureNamePrefix = filename.substring(0,filename.lastIndexOf("."));
        String resultFileStr = "result//trace_detection//" + pictureNamePrefix + ".json";
        Path resultFilePath = fileStorageService.getFileStorageLocation(resultFileStr);
        MyFile resultFile = new MyFile(resultFilePath.toUri());
        resultFile.deleteRecursively();
        traceDetectionRepository.deleteByFilename(filename);
    }

    public void removeAllResult() throws Exception{
        String resultFileStr = "result//trace_detection";
        Path resultFilePath = fileStorageService.getFileStorageLocation(resultFileStr);
        MyFile resultFile = new MyFile(resultFilePath.toUri());
        try {
            resultFile.deleteRecursively();
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        } finally {
            resultFile.mkdirs();
        }
        traceDetectionRepository.deleteAllInBatch();
    };

    public HashMap findResultPage(String filename, Pageable pageable){
        Page page = null;
        if(StringUtils.isNotBlank(filename)){
            page = traceDetectionRepository.findAllByAndFilename(pageable,filename);
        }else{
            page = traceDetectionRepository.findAll(pageable);
        }
        List<TraceDetection> result = page.getContent();
        ArrayList<TraceDetection> r = new ArrayList<>();
        for (TraceDetection traceDetection : result) {
            traceDetection.setResult(null);
            r.add(traceDetection);
        }
        HashMap<String,Object> data = new HashMap<>();
        data.put("result", r);
        data.put("total", page.getTotalElements());
        return data;
    }
}
