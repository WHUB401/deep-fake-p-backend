package com.pharaoh.deepfake.service;

import com.alibaba.fastjson.JSONObject;
import com.pharaoh.deepfake.common.util.DetectionResult;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;

public interface AdversarialService {

    public boolean hasData(String dataset, String filename);
    public boolean hasResult(String dataset, String filename);
    public void setResultTrue(String dataset, String filename);
    public void setResultFalse(String dataset, String filename);
    public JSONObject findResult(String dataset, String filename)throws Exception;
    public List findAllResult();
    public boolean removeResult(String dataset, String filename)throws IOException;
//    public void addDatabase(String dataset, String filename);
    public DetectionResult detection(String dataset, String filename)throws Exception;
    public HashMap findResultPage(String dataset, String filename, Pageable pageable);
    public JSONObject findAdvPictureList(String dataset, String filename)throws Exception;
//    public Path getResultPicturePath(String dataset, String filename, String type, String pictureName);

}
