package com.pharaoh.deepfake.common.util;

public class FileReturn {
    public FileReturn() {
    }

    public FileReturn(String filePath, boolean uploadSuccess) {
        this.filePath = filePath;
        this.uploadSuccess = uploadSuccess;
    }

    public FileReturn(String filePath, boolean uploadSuccess, String errorMessage) {
        this.filePath = filePath;
        this.uploadSuccess = uploadSuccess;
        this.errorMessage = errorMessage;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public boolean isUploadSuccess() {
        return uploadSuccess;
    }

    public void setUploadSuccess(boolean uploadSuccess) {
        this.uploadSuccess = uploadSuccess;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    String filePath;
    boolean uploadSuccess;
    String errorMessage;
}
