package com.pharaoh.deepfake.common.util;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Path;

public class InterceptAllVideoPhoto {
    public static void fetchFrame(Path videoFile, Path frameFolder, int intervalframes) throws Exception {
        // long start = System.currentTimeMillis();
        File targetFolder = new File(frameFolder.toUri());
        File sourceFile = new File(videoFile.toUri());
        if (!targetFolder.exists()) {
            targetFolder.mkdirs();
        }
        if(sourceFile.exists()){
            try (FFmpegFrameGrabber ff = new FFmpegFrameGrabber(sourceFile)){
                ff.start();
                int ftp = ff.getLengthInFrames();
                Frame frame = null;
                for (int j = 0; j < ftp-1; j++){
                    frame = ff.grabImage();
                    if(j % intervalframes == 0){
                        File targetFile = new File(frameFolder.resolve(String.valueOf(j) + ".jpg").toUri());
                        Java2DFrameConverter converter = new Java2DFrameConverter();
                        BufferedImage bufferedImage = converter.getBufferedImage(frame);
                        ImageIO.write(bufferedImage, "jpg", targetFile);
                    }
                }
            } catch (Exception e) {
                throw e;
            }
        }
    }

}
