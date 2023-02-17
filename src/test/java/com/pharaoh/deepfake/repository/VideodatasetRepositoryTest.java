package com.pharaoh.deepfake.repository;



import ch.qos.logback.core.net.SyslogOutputStream;
import com.pharaoh.deepfake.controller.form.DatasetForm;
import com.pharaoh.deepfake.entity.DeepfakeDetection;
import com.pharaoh.deepfake.entity.Forgerydetection;
import com.pharaoh.deepfake.entity.Videodataset;
import org.apache.commons.io.FileUtils;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.python.antlr.ast.Print;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class VideodatasetRepositoryTest {

    @Autowired
    private VideodatasetRepository videodatasetRepository;
    @Autowired
    private ImagedatasetRepository imagedatasetRepository;
    @Autowired
    private ForgerydetectionRepository forgerydetectionRepository;
    @Test
    void testVideoRepository() throws Exception {
    }

}
