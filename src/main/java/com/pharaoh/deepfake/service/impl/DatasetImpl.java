package com.pharaoh.deepfake.service.impl;

import ch.qos.logback.core.net.SyslogOutputStream;
import com.pharaoh.deepfake.common.util.FileReturn;
import com.pharaoh.deepfake.common.util.R;
import com.pharaoh.deepfake.common.util.VariableTable;
import com.pharaoh.deepfake.controller.form.DatasetForm;
import com.pharaoh.deepfake.entity.OriginalVideoDataset;
import com.pharaoh.deepfake.entity.Videodataset;
import com.pharaoh.deepfake.exception.MyFileNotFoundException;
import com.pharaoh.deepfake.repository.*;
import com.pharaoh.deepfake.service.DatasetService;
import io.micrometer.core.instrument.util.StringUtils;
import org.python.antlr.ast.Print;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;

@Service
public class DatasetImpl implements DatasetService {

    @Autowired
    VariableTable variableTable;
    @Autowired
    ImagedatasetRepository imagedatasetRepository;
    @Autowired
    VideodatasetRepository videodatasetRepository;
    @Autowired
    DeepfakesVideoDatasetRepository deepfakesVideoDatasetRepository;
    @Autowired
    Face2faceVideoDatasetRepository face2faceVideoDatasetRepository;
    @Autowired
    FaceswapVideoDatasetRepository faceswapVideoDatasetRepository;
    @Autowired
    NeuraltexturesVideoDatasetRepository neuraltexturesVideoDatasetRepository;
    @Autowired
    OriginalVideoDatasetRepository originalVideoDatasetRepository;

    public R setDataset(DatasetForm datasetForm) {
        JpaRepository jpaRepository = null;
        switch (datasetForm.getType()){
            case "image":
                jpaRepository = imagedatasetRepository;
                break;
            case "video":
                jpaRepository = videodatasetRepository;
                break;
            default:
                R r = R.error(50201, "DatasetType error.");
                return r;
        }
        String datasetName = datasetForm.getType() + "_" + datasetForm.getName();
        variableTable.create(datasetName);
        R r = R.ok("success.");
        return r;
    }

    public List findDataset(String datasetType) {
        List findResult = null;
        if(datasetType.equals("video")){
            findResult = videodatasetRepository.findAll();
        }else if(datasetType.equals("image")){
            findResult = imagedatasetRepository.findAll();
        }
        return findResult;
    }

    public HashMap listVideoDatasetPage(Pageable pageable, String datasetName, String filename ){
        Page page = null;
        if(StringUtils.isNotBlank(filename)){
            switch (datasetName){
                case "original":
                    page = originalVideoDatasetRepository.findAllByAndFilename(pageable,filename);
                    break;
                case "Deepfakes":
                    page = deepfakesVideoDatasetRepository.findAllByAndFilename(pageable,filename);
                    break;
                case "Face2Face":
                    page = face2faceVideoDatasetRepository.findAllByAndFilename(pageable,filename);
                    break;
                case "FaceSwap":
                    page = faceswapVideoDatasetRepository.findAllByAndFilename(pageable,filename);
                    break;
                case "NeuralTextures":
                    page = neuraltexturesVideoDatasetRepository.findAllByAndFilename(pageable,filename);
                    break;
                default:
                    page = null;
            }
        }else{
            switch (datasetName){
                case "original":
                    page = originalVideoDatasetRepository.findAll(pageable);
                    break;
                case "Deepfakes":
                    page = deepfakesVideoDatasetRepository.findAll(pageable);
                    break;
                case "Face2Face":
                    page = face2faceVideoDatasetRepository.findAll(pageable);
                    break;
                case "FaceSwap":
                    page = faceswapVideoDatasetRepository.findAll(pageable);
                    break;
                case "NeuralTextures":
                    page = neuraltexturesVideoDatasetRepository.findAll(pageable);
                    break;
                default:
                    page = null;
            }
        }
        HashMap<String,Object> data = new HashMap<String,Object>();
        data.put("result", page.getContent());
        data.put("total", page.getTotalElements());
        return data;
    }

    public Resource loadFileAsResource(String datasetName, String filename, String datasetType) {
        try {
            String dir = videodatasetRepository.findVideodatasetByDatasetname(datasetName).getPath();
            Path dirPath = Paths.get(dir).toAbsolutePath().normalize();
            Path filePath = dirPath.resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if(resource.exists()) {
                return resource;
            } else {
                throw new MyFileNotFoundException("File not found " + filename);
            }
        } catch (MalformedURLException ex) {
            throw new MyFileNotFoundException("File not found " + filename, ex);
        }
    }

}
