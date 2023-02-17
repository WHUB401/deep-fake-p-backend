package com.pharaoh.deepfake.service;

import com.alibaba.fastjson.JSONObject;
import com.pharaoh.deepfake.common.util.DetectionResult;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.util.HashMap;

public interface TraceService {
    public boolean hasResult(String filename);
    public JSONObject findResult(String filename)throws Exception;
    public DetectionResult detection(String filename)throws Exception;
    public void addDatabase(String filename);
    public void removeResult(String filename)throws Exception;
    public void removeAllResult()throws Exception;
    public HashMap findResultPage(String filename, Pageable pageable);
}
