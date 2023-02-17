package com.pharaoh.deepfake.repository;

import com.pharaoh.deepfake.entity.Face2faceVideoDataset;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface Face2faceVideoDatasetRepository extends JpaRepository<Face2faceVideoDataset, Integer> {
    @Transactional
    Page findAllByAndFilename(Pageable pageable, String filename);
}
