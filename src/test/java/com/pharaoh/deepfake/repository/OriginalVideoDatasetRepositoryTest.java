package com.pharaoh.deepfake.repository;

import com.alibaba.excel.EasyExcel;
import com.pharaoh.deepfake.service.AdversarialService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import springfox.documentation.spring.web.json.Json;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.pharaoh.deepfake.DeepfakeApplication.myLogger;
import static com.pharaoh.deepfake.common.util.readJsonData.readJsonData;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class OriginalVideoDatasetRepositoryTest {

    @Autowired
    private OriginalVideoDatasetRepository originalvideodatasetRepository;
    @Autowired
    private DeepfakesVideoDatasetRepository deepfakesVideoDatasetRepository;
    @Autowired
    private Face2faceVideoDatasetRepository face2faceVideoDatasetRepository;
    @Autowired
    private FaceswapVideoDatasetRepository faceswapVideoDatasetRepository;
    @Autowired
    private NeuraltexturesVideoDatasetRepository neuraltexturesVideoDatasetRepository;
    @Autowired
    private AdversarialDetectionRepository adversarialDetectionRepository;
    @Autowired
    private AdversarialService adversarialService;


    @Test
    void testVideoRepository() throws Exception {
    }

}
