package com.pharaoh.deepfake.common.util;

import com.pharaoh.deepfake.entity.AdversarialDetection;
import com.pharaoh.deepfake.repository.AdversarialDetectionRepository;
import com.pharaoh.deepfake.repository.ImageRepository;
import com.pharaoh.deepfake.repository.VideoRepository;
import com.pharaoh.deepfake.repository.VideodatasetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Component
public class InitAdversarialDetectionDatabase {

    @Autowired
    AdversarialDetectionRepository adversarialDetectionRepository;
    @Autowired
    VideoRepository videoRepository;
    @Autowired
    ImageRepository imageRepository;

    public void Init(String dataset, String fileFolder){
        Path datasetFolderPath = Paths.get(fileFolder).toAbsolutePath();
        File datasetFolder = datasetFolderPath.toFile();
        File[] fs = datasetFolder.listFiles();	//遍历path下的文件和目录，放在File数组中
        List<AdversarialDetection> list = new ArrayList<AdversarialDetection>();
        String filename;
        String resultPath;
        String advTargetPath;
        if (fs != null) {
            for(File f:fs){					//遍历File[]数组
                if(!f.isDirectory()) {
                    filename = f.getName();
                    resultPath = "result/adversarial_detection/"
                            + dataset + "/"
                            + filename;
                    advTargetPath = "result/adv_targeted_pgd5_32/"
                            + dataset + "/"
                            + filename;
                    list.add(new AdversarialDetection(dataset, filename, resultPath, advTargetPath, true));
                }
            }
        }
        adversarialDetectionRepository.saveAll(list);
        adversarialDetectionRepository.flush();
    }

}
