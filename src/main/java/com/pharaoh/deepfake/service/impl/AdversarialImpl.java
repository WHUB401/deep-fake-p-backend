package com.pharaoh.deepfake.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.pharaoh.deepfake.common.util.*;
import com.pharaoh.deepfake.entity.AdversarialDetection;
import com.pharaoh.deepfake.entity.DeepfakeDetection;
import com.pharaoh.deepfake.property.FileStorageProperties;
import com.pharaoh.deepfake.repository.AdversarialDetectionRepository;
import com.pharaoh.deepfake.repository.VideodatasetRepository;
import com.pharaoh.deepfake.service.AdversarialService;
import com.pharaoh.deepfake.service.FileStorageService;
import io.micrometer.core.instrument.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jsonb.JsonbAutoConfiguration;
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
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.pharaoh.deepfake.DeepfakeApplication.missionStatus;
import static com.pharaoh.deepfake.DeepfakeApplication.myLogger;
import static com.pharaoh.deepfake.common.util.readJsonData.readJsonData;

@Service
public class AdversarialImpl implements AdversarialService {
    private final Path fileStorageLocation;
    @Autowired
    VideodatasetRepository videodatasetRepository;
    @Autowired
    AdversarialDetectionRepository adversarialDetectionRepository;
    @Autowired
    FileStorageService fileStorageService;

    @Autowired
    public AdversarialImpl(FileStorageProperties fileStorageProperties) {
        this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir())
                .toAbsolutePath().normalize();
    }

    public List findAllResult() {
        return adversarialDetectionRepository.findAll(Sort.by(Sort.Direction.ASC,"dataset","id"));
    }

    public HashMap findResultPage(String dataset, String filename, Pageable pageable){
        Page page = null;
        if(StringUtils.isNotBlank(dataset) && StringUtils.isNotBlank(filename)){
            page = adversarialDetectionRepository.findAllByDatasetAndFilenameAndHasResult(pageable,dataset,filename, true);
        }else if(StringUtils.isNotBlank(dataset) && StringUtils.isBlank(filename)){
            page = adversarialDetectionRepository.findAllByDatasetAndHasResult(pageable,dataset, true);
        }else if(StringUtils.isBlank(dataset) && StringUtils.isNotBlank(filename)){
            page = adversarialDetectionRepository.findAllByFilenameAndHasResult(pageable,filename, true);
        }else{
            page = adversarialDetectionRepository.findAllByHasResult(pageable, true);
        }
        HashMap<String,Object> data = new HashMap<String,Object>();
        data.put("result", page.getContent());
        data.put("total", page.getTotalElements());
        return data;
    }

    public boolean hasResult(String dataset, String filename){
        return adversarialDetectionRepository.findAdversarialDetectionByDatasetAndFilename(dataset, filename).getHasResult();
    }

    public boolean hasData(String dataset, String filename){
        return adversarialDetectionRepository.existsByDatasetAndFilename(dataset, filename);
    }

    public JSONObject findResult(String dataset, String filename)throws Exception{
        if(!hasResult(dataset, filename)){
            throw new Exception();
        }
        AdversarialDetection adversarialDetection = adversarialDetectionRepository.findAdversarialDetectionByDatasetAndFilename(dataset,filename);
        Path resultDir = fileStorageService.getFileStorageLocation(adversarialDetection.getResultpath());
        Path adv_adv_Result = resultDir.resolve("adv_adv//result.json");
        Path adv_clean_Result = resultDir.resolve("adv_clean//result.json");
        Path clean_clean_Result = resultDir.resolve("clean_clean//result.json");
        JSONObject R = new JSONObject();
        try {
            JSONObject adv_adv_Json = JSONObject.parseObject(readJsonData(adv_adv_Result));
            JSONObject adv_clean_Json = JSONObject.parseObject(readJsonData(adv_clean_Result));
            JSONObject clean_clean_Json = JSONObject.parseObject(readJsonData(clean_clean_Result));
            R.put("adv_adv", adv_adv_Json);
            R.put("adv_clean", adv_clean_Json);
            R.put("clean_clean", clean_clean_Json);
        } catch (IOException e) {
            myLogger.error("Find Adversarial Result Error!",e);
            throw e;
        }

        return R;
    }

    public JSONObject findAdvPictureList(String dataset, String filename)throws Exception{
        AdversarialDetection adversarialDetection = adversarialDetectionRepository.findAdversarialDetectionByDatasetAndFilename(dataset,filename);
        Path advDirPath = fileStorageService.getFileStorageLocation(adversarialDetection.getAdvtargetpath());
        Path advResultPath = advDirPath.resolve("result.json");
        JSONObject advResult;
        try {
            advResult = JSONObject.parseObject(readJsonData(advResultPath));
        } catch (IOException e) {
            myLogger.error("Find Adversarial Picture List Error!",e);
            throw e;
        }
        return advResult;
    }

    public boolean removeResult(String dataset, String filename)throws IOException{
        AdversarialDetection adversarialDetection = adversarialDetectionRepository.findAdversarialDetectionByDatasetAndFilename(dataset,filename);
        Path resultDirPath = fileStorageService.getFileStorageLocation(adversarialDetection.getResultpath());
        MyFile resultFolder = new MyFile(resultDirPath.toUri());
        resultFolder.deleteRecursively();
        setResultFalse(dataset,filename);
        return true;
    }

//    public void addDatabase(String dataset, String filename){
//        String resultpath = "result/adversarial_detection/" + dataset + "/" + filename;
//        String advtargetpath = "result/adv_targeted_pgd5_32/" + dataset + "/" + filename;
//        AdversarialDetection adversarialDetection = new AdversarialDetection(dataset, filename, resultpath, advtargetpath, true);
//        adversarialDetectionRepository.saveAndFlush(adversarialDetection);
//    }

    public void setResultTrue(String dataset, String filename){
        AdversarialDetection adversarialDetection = adversarialDetectionRepository.findAdversarialDetectionByDatasetAndFilename(dataset, filename);
        adversarialDetection.setHasResult(true);
        adversarialDetectionRepository.saveAndFlush(adversarialDetection);
    }

    public void setResultFalse(String dataset, String filename){
        AdversarialDetection adversarialDetection = adversarialDetectionRepository.findAdversarialDetectionByDatasetAndFilename(dataset, filename);
        adversarialDetection.setHasResult(false);
        adversarialDetectionRepository.saveAndFlush(adversarialDetection);
    }

    public DetectionResult detection(String dataset, String filename)throws Exception{
        System.out.println("isBusy: " + missionStatus.isBusy());
        System.out.println("Work"+ missionStatus.getWork());
        if(missionStatus.isBusy()){
            String message = missionStatus.getWork() + " is running, please wait!";
            return new DetectionResult(false,message);
        }
        missionStatus.setBusy(true);
        missionStatus.setWork("Adversarial");
        String workplaceStr = "storage//pycommond";
        String sourcefileDirStr = videodatasetRepository.findVideodatasetByDatasetname(dataset).getPath();
        String filePathStr = sourcefileDirStr + "/" + filename;

        Path wrokplacePath = Paths.get(workplaceStr).normalize();
        Path resultfilePath = fileStorageLocation.resolve("result/adversarial_detection"
                + "/" + dataset
                + "/" + filename
                + "/adv_adv/result.json").normalize();

        File resultFile = new File(resultfilePath.toUri());
        File workplaceDir = new File(wrokplacePath.toUri());

        try {
            // 运行检测脚本
            Runtime run = Runtime.getRuntime();
            String commond[] = {"bash", "adversarial_detection.sh", filePathStr};
            Process process = run.exec(commond, null, workplaceDir);
            new InputStreamRunnable(process.getErrorStream(), "Error").start();
            new InputStreamRunnable(process.getInputStream(), "Info").start();
            boolean code = process.waitFor(30, TimeUnit.MINUTES);
            System.out.println("执行计算结果：code-" + code);
            process.waitFor(); // 等待程序运行结束
            missionStatus.setBusy(false);
        } catch (Exception e) {
            myLogger.error("DeefakeDetection Error!",e);
            missionStatus.setBusy(false);
            throw e;
        } finally {
            missionStatus.setBusy(false);
        }
        missionStatus.setBusy(false);
        if(resultFile.exists()){
            return new DetectionResult(true,"detection success!");
        }else{
            return new DetectionResult(false,"unknown error.");
        }
    }

//    public Path getResultPicturePath(String dataset, String filename, String type, String pictureName){
//        AdversarialDetection adversarialDetection = adversarialDetectionRepository.findAdversarialDetectionByDatasetAndFilename(dataset,filename);
//        Path resultDir = fileStorageService.getFileStorageLocation(adversarialDetection.getResultpath());
//        return resultDir.resolve(type).resolve(pictureName);
//    }

}
