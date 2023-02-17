package com.pharaoh.deepfake.service;

import com.alibaba.fastjson.JSONObject;
import com.pharaoh.deepfake.common.util.DetectionResult;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public interface DeepfakeDetectionService {

    public DetectionResult detection(String filename)throws Exception;
    public DetectionResult detection_image()throws Exception;
    public void addDatabase(String filename);
    public JSONObject getMediaResult(String filename)throws Exception;
    public boolean hasResult(String filename);
    public List findAllResult();
    public void removeResult(String filename)throws IOException;
    public HashMap findResultPage(String filename, Pageable pageable);

}
