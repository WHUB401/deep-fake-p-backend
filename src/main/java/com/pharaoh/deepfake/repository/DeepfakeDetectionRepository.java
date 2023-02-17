package com.pharaoh.deepfake.repository;

import com.pharaoh.deepfake.entity.DeepfakeDetection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;


public interface DeepfakeDetectionRepository extends JpaRepository<DeepfakeDetection, Integer> {

    @Transactional
    void deleteByFilename(String Imagename);
    boolean existsByFilename(String filename);
    @Transactional
    Page findAllByAndFilename(Pageable pageable, String filename);
}
