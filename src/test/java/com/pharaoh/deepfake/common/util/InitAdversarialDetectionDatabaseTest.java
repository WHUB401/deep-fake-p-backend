package com.pharaoh.deepfake.common.util;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class InitAdversarialDetectionDatabaseTest {

    @Autowired
    InitAdversarialDetectionDatabase initAdversarialDetectionDatabase;

    @Test
    void test(){
//        initAdversarialDetectionDatabase.Init("Face2Face", "D:\\workplace\\deep-fake-p-backend\\datasets\\ff++eval\\Face2Face\\c23\\videos");
//        initAdversarialDetectionDatabase.Init("FaceSwap", "D:\\workplace\\deep-fake-p-backend\\datasets\\ff++eval\\FaceSwap\\c23\\videos");
//        initAdversarialDetectionDatabase.Init("NeuralTextures", "D:\\workplace\\deep-fake-p-backend\\datasets\\ff++eval\\NeuralTextures\\c23\\videos");
//        initAdversarialDetectionDatabase.Init("original", "D:\\workplace\\deep-fake-p-backend\\datasets\\ff++eval\\original");
    }

}
