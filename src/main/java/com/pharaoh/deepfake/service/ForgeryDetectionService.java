package com.pharaoh.deepfake.service;

import com.pharaoh.deepfake.common.util.DetectionResult;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public interface ForgeryDetectionService {

    public DetectionResult detection(String filename)throws Exception;
    public void addDatabase(String filename);
    public boolean hasResult(String filename);
    public List findAllResult();
    public void removeResult(String filename)throws IOException;
    public void removeAllResult()throws Exception;
    public HashMap findResultPage(String filename, Pageable pageable);
    public DetectionResult detectionAllImage()throws Exception;
    public void removeImagesTar() throws Exception;

}
