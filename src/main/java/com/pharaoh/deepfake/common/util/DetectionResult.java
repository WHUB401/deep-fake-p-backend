package com.pharaoh.deepfake.common.util;

public class DetectionResult {
    boolean detectionSuccess;
    String message;

    public DetectionResult(boolean detectionSuccess, String message) {
        this.detectionSuccess = detectionSuccess;
        this.message = message;
    }

    public DetectionResult() {
    }

    public boolean isDetectionSuccess() {
        return detectionSuccess;
    }

    public void setDetectionSuccess(boolean detectionSuccess) {
        this.detectionSuccess = detectionSuccess;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
