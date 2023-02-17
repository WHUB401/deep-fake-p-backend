package com.pharaoh.deepfake.entity;

import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Data
@DynamicUpdate
@DynamicInsert
@Table(name = "face2face_video_dataset", indexes = {
        @Index(name = "Face2Face_video_dataset_filename_uindex", columnList = "filename", unique = true)
}, uniqueConstraints = {
        @UniqueConstraint(name = "Face2Face_video_dataset_id_uindex", columnNames = {"id"})
})
@Entity
public class Face2faceVideoDataset {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "filename", nullable = false, length = 20)
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

    public Face2faceVideoDataset(Integer id, String filename) {
        this.id = id;
        this.filename = filename;
    }

    public Face2faceVideoDataset(String filename) {
        this.filename = filename;
    }

    public Face2faceVideoDataset() {
    }
}
