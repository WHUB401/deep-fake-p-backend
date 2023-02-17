package com.pharaoh.deepfake.entity;

import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Data
@DynamicUpdate
@DynamicInsert
@Table(name = "video", indexes = {
        @Index(name = "video_videoname_uindex", columnList = "filename", unique = true)
})
@Entity
public class Video {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "filename", nullable = false, length = 100)
    private String filename;

    public Video() {

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

    public Video(Integer id, String filename) {
        this.id = id;
        this.filename = filename;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Video(String filename) {
        this.filename = filename;
    }
}
