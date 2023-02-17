package com.pharaoh.deepfake.entity;

import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Data
@DynamicUpdate
@DynamicInsert
@Table(name = "faceswap_video_dataset", indexes = {
        @Index(name = "faceswapl_video_dataset_filename_uindex", columnList = "filename", unique = true)
})
@Entity
public class FaceswapVideoDataset {
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

    public FaceswapVideoDataset() {
    }

    public FaceswapVideoDataset(Integer id, String filename) {
        this.id = id;
        this.filename = filename;
    }

    public FaceswapVideoDataset(String filename) {
        this.filename = filename;
    }
}
