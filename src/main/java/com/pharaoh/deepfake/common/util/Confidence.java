package com.pharaoh.deepfake.common.util;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static com.pharaoh.deepfake.DeepfakeApplication.myLogger;

public class Confidence {

    public static Map<Integer,Float> getConfidenceFromFile(String filePath) {
        Map<Integer,Float> chartData = new HashMap<>();
        File file = new File(filePath);
        InputStreamReader read = null;
        BufferedReader reader = null;
        try {
            read = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8);
            reader = new BufferedReader(read);
            String line;
            int index = 1;
            while ((line = reader.readLine()) != null) {
                chartData.put(index, Float.parseFloat(line));
                index += 1;
            }
        } catch (IOException e) {
            myLogger.error("DeefakeDetection Error!",e);
        } finally {
            if (read != null) {
                try {
                    read.close();
                } catch (IOException e) {
                    myLogger.error("DeefakeDetection Error!",e);
                }
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    myLogger.error("DeefakeDetection Error!",e);
                }
            }

        }
        return chartData;
    }
}
