package com.pharaoh.deepfake.entity;

import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Data
@DynamicUpdate
@DynamicInsert
@Table(name = "trace_detection", indexes = {
        @Index(name = "trace_detection_filename_uindex", columnList = "filename", unique = true),
        @Index(name = "trace_detection_result_uindex", columnList = "result", unique = true)
})
@Entity
public class TraceDetection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "filename", nullable = false, length = 50)
    private String filename;

    @Column(name = "result", nullable = false, length = 50)
    private String result;

    public TraceDetection() {
    }

    public TraceDetection(String filename, String result) {
        this.filename = filename;
        this.result = result;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
