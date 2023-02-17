package com.pharaoh.deepfake.common.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class InputStreamRunnable extends Thread {
    BufferedReader bReader = null;
    String type = null;
    public InputStreamRunnable(InputStream is, String _type) {
        try {
            bReader = new BufferedReader(new InputStreamReader(new BufferedInputStream(is), "UTF-8"));
            type = _type;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    public void run() {
        String line;
        int lineNum = 0;
        try {
            while ((line = bReader.readLine()) != null) {
                lineNum++;
                System.out.println(type+":"+line);
            }
            bReader.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
