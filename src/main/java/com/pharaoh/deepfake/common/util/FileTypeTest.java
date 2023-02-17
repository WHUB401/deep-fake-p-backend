package com.pharaoh.deepfake.common.util;

public class FileTypeTest
{
    static public boolean FileIsVideo(String fileName){
        String fileType = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        switch (fileType){
            case "mp4":
//            case "mkv":
//            case "mov":
//            case "ogg":
//            case "wmv":
//            case "rm":
//            case "rmvb":
//            case "flv":
//            case "avi":
                return true;
        }
        return false;
    }
    static public boolean FileIsNotVideo(String fileName){
        return !FileIsVideo(fileName);
    }

    static public boolean FileIsImage(String fileName){
        String fileType = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        switch (fileType){
            case "jpeg":
            case "jpg":
            case "png":
            case "tif":
            case "tiff":
            case "gif":
            case "tga":
            case "svg":
                return true;
        }
        return false;
    }
    static public boolean FileIsNotImage(String fileName){
        return !FileIsImage(fileName);
    }
}
