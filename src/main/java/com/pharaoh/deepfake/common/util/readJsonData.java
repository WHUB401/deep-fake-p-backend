package com.pharaoh.deepfake.common.util;

import java.io.*;
import java.nio.file.Path;

public class readJsonData {

    /**
     * 读取json文件并且转换成字符串
     * @param filePath
     * @return
     * @throws IOException
     */
    public static String readJsonData(Path filePath) throws IOException {
        // 读取文件数据
        //System.out.println("读取文件数据util");

        StringBuffer strbuffer = new StringBuffer();
        File jsonFile = new File(filePath.toUri());//
        if (!jsonFile.exists()) {
            System.err.println("Can't Find " + filePath);
        }
        try {
            FileInputStream fis = new FileInputStream(jsonFile);
            InputStreamReader inputStreamReader = new InputStreamReader(fis, "UTF-8");
            BufferedReader in  = new BufferedReader(inputStreamReader);

            String str;
            while ((str = in.readLine()) != null) {
                strbuffer.append(str);  //new String(str,"UTF-8")
            }
            in.close();
        } catch (IOException e) {
            e.getStackTrace();
        }
        //System.out.println("读取文件结束util");
        return strbuffer.toString();
    }
}
