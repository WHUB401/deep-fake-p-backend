package com.pharaoh.deepfake.controller.form;

import lombok.Data;

@Data
public class DatasetVideoForm {
    String filename;
    String dataset;

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getDataset() {
        return dataset;
    }

    public void setDataset(String dataset) {
        this.dataset = dataset;
    }
}
