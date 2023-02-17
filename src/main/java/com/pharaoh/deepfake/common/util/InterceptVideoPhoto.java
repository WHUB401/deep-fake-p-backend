package com.pharaoh.deepfake.common.util;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Path;

public class InterceptVideoPhoto {
    public static void fetchFrame(Path videoFile, Path frameFile) throws Exception {
        // long start = System.currentTimeMillis();
        File targetFile = new File(frameFile.toUri());
        File targetDir = targetFile.getParentFile();
        File sourceFile = new File(videoFile.toUri());
        if (!targetDir.exists()) {
            targetDir.mkdirs();
        }
        if(sourceFile.exists()){
            try (FFmpegFrameGrabber ff = new FFmpegFrameGrabber(sourceFile)){
                ff.start();
                int ftp = ff.getLengthInFrames();
                int flag = Math.min(24, ftp/2);
                Frame frame = null;
                for (int j = 0; j < flag; j++){
                    frame = ff.grabImage();
                }
                Java2DFrameConverter converter = new Java2DFrameConverter();
                BufferedImage bufferedImage = converter.getBufferedImage(frame);
                ImageIO.write(bufferedImage, "jpg", targetFile);
            } catch (Exception e) {
                throw e;
            }
        }
    }

}
