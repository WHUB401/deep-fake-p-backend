package com.pharaoh.deepfake.entity;

import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Data
@DynamicUpdate
@DynamicInsert
@Table(name = "videodataset", indexes = {
        @Index(name = "videodataset_filename_uindex", columnList = "datasetname", unique = true)
}, uniqueConstraints = {
        @UniqueConstraint(name = "videodataset_id_uindex", columnNames = {"id"})
})
@Entity
public class Videodataset {
    @Id
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "datasetname", nullable = false, length = 20)
    private String datasetname;

    @Column(name = "path", nullable = false, length = 50)
    private String path;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getDatasetname() {
        return datasetname;
    }

    public void setDatasetname(String datasetname) {
        this.datasetname = datasetname;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
