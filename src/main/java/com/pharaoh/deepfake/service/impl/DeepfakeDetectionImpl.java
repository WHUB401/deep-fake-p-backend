package com.pharaoh.deepfake.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.pharaoh.deepfake.common.util.*;
import com.pharaoh.deepfake.entity.DeepfakeDetection;
import com.pharaoh.deepfake.exception.MyFileNotFoundException;
import com.pharaoh.deepfake.property.FileStorageProperties;
import com.pharaoh.deepfake.repository.DeepfakeDetectionRepository;
import com.pharaoh.deepfake.service.DeepfakeDetectionService;
import com.pharaoh.deepfake.service.FileStorageService;
import io.micrometer.core.instrument.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.pharaoh.deepfake.DeepfakeApplication.missionStatus;
import static com.pharaoh.deepfake.DeepfakeApplication.myLogger;
import static com.pharaoh.deepfake.common.util.readJsonData.readJsonData;

@Service
public class DeepfakeDetectionImpl implements DeepfakeDetectionService {

    private final Path fileStorageLocation;

    @Autowired
    private FileStorageService fileStorageService;
    @Autowired
    private DeepfakeDetectionRepository deepfakeDetectionRepository;

    @Autowired
    public DeepfakeDetectionImpl(FileStorageProperties fileStorageProperties) {
        this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir())
                .toAbsolutePath().normalize();
    }

    public DetectionResult detection(String filename)throws Exception{
        System.out.println("isBusy: " + missionStatus.isBusy());
        System.out.println("Work"+ missionStatus.getWork());
        if(missionStatus.isBusy()){
            String message = missionStatus.getWork() + " is running, please wait!";
            return new DetectionResult(false,message);
        }
        missionStatus.setBusy(true);
        missionStatus.setWork("Deepfake");
        // 如果目标目录不存在，则新建目录
        Path targetFolderPath = fileStorageLocation.resolve("workspace/deepfake_detection").normalize();
        MyFile targetFolder = new MyFile(targetFolderPath.toUri());
        if (!targetFolder.exists()) {
            targetFolder.mkdirs();
        }

        String shellCommondfileStr = "deepfake_detection.sh";
        String workplaceStr = "storage//pycommond";

        Path sourcefilePath = fileStorageLocation.resolve("video/" + filename).normalize();
        Path targetfilePath = fileStorageLocation.resolve("workspace/deepfake_detection/" + filename).normalize();
        Path resultfilePath = fileStorageLocation.resolve("result/deepfake_detection/"
                + filename.substring(0, filename.lastIndexOf('.'))
                + "/result.json").normalize();
        Path wrokplacePath = Paths.get(workplaceStr).normalize();

        File targetFile = new File(targetfilePath.toUri());
        File resultFile = new File(resultfilePath.toUri());
        File workplaceDir = new File(wrokplacePath.toUri());

        try (FileOutputStream os = new FileOutputStream(targetFile)){//被复制到此文件中)
            // 拷贝待检测文件到目标目录
            Resource resource = new UrlResource(sourcefilePath.toUri());
            FileCopyUtils.copy(resource.getInputStream(),os);

            // 运行deepfake检测脚本
            Runtime run = Runtime.getRuntime();
            String commond[] = {"bash", shellCommondfileStr};
            Process process = run.exec(commond, null, workplaceDir);
            new InputStreamRunnable(process.getErrorStream(), "Error").start();
            new InputStreamRunnable(process.getInputStream(), "Info").start();
            boolean code = process.waitFor(30, TimeUnit.MINUTES);
            System.out.println("执行计算结果：code-" + code);
            process.waitFor(); // 等待程序运行结束
            missionStatus.setBusy(false);
        } catch (Exception e) {
            myLogger.error("DeefakeDetection Error!",e);
            throw e;
        } finally {
            targetFolder.deleteRecursively(); //删除中间文件
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
        DeepfakeDetection deepfakeDetection = new DeepfakeDetection(filename);
        deepfakeDetectionRepository.saveAndFlush(deepfakeDetection);
    }

    public JSONObject getMediaResult(String filename)throws Exception{
        String mediaNamePrefix = filename.substring(0,filename.lastIndexOf("."));
        String subDirectory = "result//deepfake_detection//" + mediaNamePrefix + "//result.json";
        Path resultFilePath = fileStorageService.getFileStorageLocation(subDirectory);
        if(!hasResult(filename)){
            throw new Exception();
        }
        String mediaResult = "";
        try {
            mediaResult = readJsonData(resultFilePath);
        } catch (IOException e) {
            myLogger.error("get deepfake result error!",e);
            throw e;
        }
        return JSONObject.parseObject(mediaResult);
    }

    public boolean hasResult(String filename){
        return deepfakeDetectionRepository.existsByFilename(filename);
    }

    public List findAllResult() {
        return deepfakeDetectionRepository.findAll(Sort.by(Sort.Direction.ASC,"id"));
    }

    public HashMap findResultPage(String filename, Pageable pageable){
        Page page = null;
        if(StringUtils.isNotBlank(filename)){
            page = deepfakeDetectionRepository.findAllByAndFilename(pageable,filename);
        }else{
            page = deepfakeDetectionRepository.findAll(pageable);
        }
        HashMap<String,Object> data = new HashMap<String,Object>();
        data.put("result", page.getContent());
        data.put("total", page.getTotalElements());
        return data;
    }

    public void removeResult(String filename) throws IOException{
        String mediaNamePrefix = filename.substring(0,filename.lastIndexOf("."));
        String subDirectory = "result//deepfake_detection//" + mediaNamePrefix;
        Path resultFilePath = fileStorageService.getFileStorageLocation(subDirectory);
        MyFile resultFile = new MyFile(resultFilePath.toUri());
        resultFile.deleteRecursively();
        deepfakeDetectionRepository.deleteByFilename(filename);
    }

    public DetectionResult detection_image()throws Exception{
        System.out.println("isBusy: " + missionStatus.isBusy());
        System.out.println("Work"+ missionStatus.getWork());
        if(missionStatus.isBusy()){
            String message = missionStatus.getWork() + " is running, please wait!";
            return new DetectionResult(false,message);
        }
        missionStatus.setBusy(true);
        missionStatus.setWork("Deepfake_image");

        String shellCommondfileStr = "deepfake_detection_image.sh";
        String workplaceStr = "storage//pycommond";
        Path wrokplacePath = Paths.get(workplaceStr).normalize();
        File workplaceDir = new File(wrokplacePath.toUri());

        try {
            // 运行deepfake检测脚本
            Runtime run = Runtime.getRuntime();
            String commond[] = {"bash", shellCommondfileStr};
            Process process = run.exec(commond, null, workplaceDir);
            new InputStreamRunnable(process.getErrorStream(), "Error").start();
            new InputStreamRunnable(process.getInputStream(), "Info").start();
            boolean code = process.waitFor(30, TimeUnit.MINUTES);
            System.out.println("执行计算结果：code-" + code);
            process.waitFor(); // 等待程序运行结束
            missionStatus.setBusy(false);
        } catch (Exception e) {
            myLogger.error("DeefakeDetection Error!",e);
            throw e;
        } finally {
            missionStatus.setBusy(false);
        }
        missionStatus.setBusy(false);
        return new DetectionResult(true,"detection success!");
    }
}
