package com.pharaoh.deepfake.repository;

import com.pharaoh.deepfake.entity.Imagedataset;
import com.pharaoh.deepfake.entity.Videodataset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;

public interface VideodatasetRepository extends JpaRepository<Videodataset, Integer> {

    @Transactional
    List findByDatasetname(String datasetName);
    @Transactional
    Videodataset findVideodatasetByDatasetname(String dataset);

}
