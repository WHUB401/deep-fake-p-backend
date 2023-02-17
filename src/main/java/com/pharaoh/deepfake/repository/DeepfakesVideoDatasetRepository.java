package com.pharaoh.deepfake.repository;

import com.pharaoh.deepfake.entity.DeepfakesVideoDataset;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface DeepfakesVideoDatasetRepository extends JpaRepository<DeepfakesVideoDataset, Integer> {
    @Transactional
    Page findAllByAndFilename(Pageable pageable, String filename);
}
