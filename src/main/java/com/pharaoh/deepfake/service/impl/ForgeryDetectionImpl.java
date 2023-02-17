package com.pharaoh.deepfake.service.impl;

import ch.qos.logback.core.net.SyslogOutputStream;
import com.pharaoh.deepfake.common.util.DetectionResult;
import com.pharaoh.deepfake.common.util.InputStreamRunnable;
import com.pharaoh.deepfake.common.util.MyFile;
import com.pharaoh.deepfake.entity.Video;
import com.pharaoh.deepfake.entity.Forgerydetection;
import com.pharaoh.deepfake.property.FileStorageProperties;
import com.pharaoh.deepfake.repository.ForgerydetectionRepository;
import com.pharaoh.deepfake.service.FileStorageService;
import com.pharaoh.deepfake.service.ForgeryDetectionService;
import io.micrometer.core.instrument.util.StringUtils;
import org.python.antlr.ast.Print;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static com.pharaoh.deepfake.DeepfakeApplication.missionStatus;
import static com.pharaoh.deepfake.DeepfakeApplication.myLogger;

@Service
public class ForgeryDetectionImpl implements ForgeryDetectionService {

    @Autowired
    private ForgerydetectionRepository forgerydetectionRepository;
    @Autowired
    private FileStorageService fileStorageService;
    final private Path fileStorageLocation;

    @Autowired
    public ForgeryDetectionImpl(FileStorageProperties fileStorageProperties) {
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
        missionStatus.setWork("ForgeryDetection");

        // 如果目标目录不存在，则新建目录
        Path targetFolderPath = fileStorageLocation.resolve("workspace/forgery_detection").normalize();
        MyFile targetFolder = new MyFile(targetFolderPath.toUri());
        if (!targetFolder.exists()) {
            targetFolder.mkdirs();
        }

        String sourceSubDirectory = "image/" + filename;
        String targetSubDirectory = "workspace/forgery_detection/" + filename;
        String resultSubDirectory = "result/forgery_detection/" + filename;
        String shellCommondfileStr = "forgery_detection.sh";
        String workplaceStr = "storage//pycommond";


        Path sourcefilePath = fileStorageLocation.resolve(sourceSubDirectory).normalize();
        Path targetfilePath = fileStorageLocation.resolve(targetSubDirectory).normalize();
        Path resultfilePath = fileStorageLocation.resolve(resultSubDirectory).normalize();
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
            missionStatus.setBusy(false);
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

        forgerydetectionRepository.saveAndFlush(new Forgerydetection(filename));

    }

    public boolean hasResult(String filename){
        return forgerydetectionRepository.existsByFilename(filename);

    }

    public List findAllResult(){
        return forgerydetectionRepository.findAll(Sort.by(Sort.Direction.ASC,"id"));
    }

    public HashMap findResultPage(String filename, Pageable pageable){
        Page page = null;
        if(StringUtils.isNotBlank(filename)){
            page = forgerydetectionRepository.findAllByAndFilename(pageable,filename);
        }else{
            page = forgerydetectionRepository.findAll(pageable);
        }
        HashMap<String,Object> data = new HashMap<String,Object>();
        data.put("result", page.getContent());
        data.put("total", page.getTotalElements());
        return data;
    }

    public void removeResult(String filename) throws IOException{
        String pictureNamePrefix = filename.substring(0,filename.lastIndexOf("."));
        String subDirectory = "result//forgery_detection//" + pictureNamePrefix + ".jpg";
        Path resultFilePath = fileStorageService.getFileStorageLocation(subDirectory);
        MyFile resultFile = new MyFile(resultFilePath.toUri());
        resultFile.deleteRecursively();
        forgerydetectionRepository.deleteByFilename(filename);
    }

    public void removeAllResult() throws Exception{
        String subDirectory = "result//forgery_detection";
        Path resultFilePath = fileStorageService.getFileStorageLocation(subDirectory);
        MyFile resultFile = new MyFile(resultFilePath.toUri());
        try {
            resultFile.deleteRecursively();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            resultFile.mkdirs();
        }
        forgerydetectionRepository.deleteAllInBatch();
    }

    public DetectionResult detectionAllImage()throws Exception{
        System.out.println("isBusy: " + missionStatus.isBusy());
        System.out.println("Work"+ missionStatus.getWork());
        if(missionStatus.isBusy()){
            String message = missionStatus.getWork() + " is running, please wait!";
            return new DetectionResult(false,message);
        }
        missionStatus.setBusy(true);
        missionStatus.setWork("Forgery_all_image");

        String shellCommondfileStr = "forgery_detection_all_image.sh";
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
            myLogger.error("Forgery Detection Error!",e);
            throw e;
        } finally {
            missionStatus.setBusy(false);
        }
        missionStatus.setBusy(false);
        return new DetectionResult(true,"detection success!");
    }

    public void removeImagesTar() throws Exception{
        String subDirectory = "result//forgery_detection_all_image";
        String tarDirectory = "result//forgery_detection_all_image.tar";
        Path resultFilePath = fileStorageService.getFileStorageLocation(subDirectory);
        Path tarFilePath = fileStorageService.getFileStorageLocation(tarDirectory);
        MyFile resultFile = new MyFile(resultFilePath.toUri());
        MyFile tarFile = new MyFile(tarFilePath.toUri());
        try {
            resultFile.deleteRecursively();
            tarFile.deleteRecursively();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            resultFile.mkdirs();
        }
    }
}
