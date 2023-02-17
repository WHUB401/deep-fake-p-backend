package com.pharaoh.deepfake;

import com.pharaoh.deepfake.common.util.MissionStatus;
import com.pharaoh.deepfake.property.FileStorageProperties;
import com.pharaoh.deepfake.service.DeepfakeDetectionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({
        FileStorageProperties.class
})
public class DeepfakeApplication {

    public final static Logger myLogger = LoggerFactory.getLogger(DeepfakeDetectionService.class);
    public final static MissionStatus missionStatus = new MissionStatus(false,"");

    public static void main(String[] args) {
        SpringApplication.run(DeepfakeApplication.class, args);
    }

}
