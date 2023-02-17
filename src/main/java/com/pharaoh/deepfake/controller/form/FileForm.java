package com.pharaoh.deepfake.controller.form;

import lombok.Data;

@Data
public class FileForm {
    private String filename;

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}
