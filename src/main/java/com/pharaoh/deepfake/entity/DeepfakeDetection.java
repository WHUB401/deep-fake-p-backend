package com.pharaoh.deepfake.entity;

import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Data
@DynamicUpdate
@DynamicInsert
@Table(name = "deepfake_detection", indexes = {
        @Index(name = "deepfake_detection_filename_uindex", columnList = "filename", unique = true)
}, uniqueConstraints = {
        @UniqueConstraint(name = "deepfake_detection_id_uindex", columnNames = {"id"})
})
@Entity
public class DeepfakeDetection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "filename", nullable = false, length = 50)
    private String filename;

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

    public DeepfakeDetection(){}

    public DeepfakeDetection(String filename) {
        this.filename = filename;
    }
}
