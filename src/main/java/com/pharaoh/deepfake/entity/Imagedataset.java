package com.pharaoh.deepfake.entity;

import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Data
@DynamicUpdate
@DynamicInsert
@Table(name = "imagedataset", indexes = {
        @Index(name = "imagedataset_name_uindex", columnList = "datasetname", unique = true)
})
@Entity
public class Imagedataset {
    @Id
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "datasetname", nullable = false, length = 20)
    private String datasetname;

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
