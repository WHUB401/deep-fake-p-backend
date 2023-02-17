package com.pharaoh.deepfake.repository;



import ch.qos.logback.core.net.SyslogOutputStream;
import com.pharaoh.deepfake.entity.TraceDetection;
import com.pharaoh.deepfake.entity.Video;
import org.apache.commons.io.FileUtils;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.python.antlr.ast.Print;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.File;
import java.io.IOException;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class VideoRepositoryTest {

    @Autowired
    private VideoRepository videoRepository;
    @Autowired
    private ImageRepository imageRepository;
    @Autowired
    private TraceDetectionRepository traceDetectionRepository;

    @Test
    void testVideoRepository() throws Exception {
    }

}
