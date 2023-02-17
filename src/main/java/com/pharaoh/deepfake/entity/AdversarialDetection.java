package com.pharaoh.deepfake.entity;

import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Data
@DynamicUpdate
@DynamicInsert
@Table(name = "adversarial_detection", indexes = {
        @Index(name = "adversarial_detection_dataset_index", columnList = "dataset"),
        @Index(name = "adversarial_detection_filename_index", columnList = "filename")
})
@Entity
public class AdversarialDetection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "dataset", nullable = false, length = 20)
    private String dataset;

    @Column(name = "filename", nullable = false, length = 20)
    private String filename;

    @Column(name = "resultpath", nullable = false, length = 50)
    private String resultpath;

    @Column(name = "advtargetpath", length = 50)
    private String advtargetpath;

    @Column(name = "has_result", nullable = false)
    private Boolean hasResult = false;

    public Boolean getHasResult() {
        return hasResult;
    }

    public void setHasResult(Boolean hasResult) {
        this.hasResult = hasResult;
    }

    public String getAdvtargetpath() {
        return advtargetpath;
    }

    public void setAdvtargetpath(String advtargetpath) {
        this.advtargetpath = advtargetpath;
    }

    public String getResultpath() {
        return resultpath;
    }

    public void setResultpath(String resultpath) {
        this.resultpath = resultpath;
    }

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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public AdversarialDetection() {
    }

    public AdversarialDetection(String dataset, String filename, String resultpath, String advtargetpath, boolean hasResult) {
        this.dataset = dataset;
        this.filename = filename;
        this.resultpath = resultpath;
        this.advtargetpath = advtargetpath;
        this.hasResult = hasResult;
    }
}
