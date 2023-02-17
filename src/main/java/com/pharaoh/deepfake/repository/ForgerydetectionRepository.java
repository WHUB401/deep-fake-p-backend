package com.pharaoh.deepfake.repository;

import com.pharaoh.deepfake.entity.Forgerydetection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.transaction.annotation.Transactional;

public interface ForgerydetectionRepository extends JpaRepository<Forgerydetection, Integer> {

    @Transactional
    void deleteByFilename(String Filename);
    @Transactional
    void deleteAllInBatch();
    @Transactional
    boolean existsByFilename(String Imagename);
    @Transactional
    Page findAllByAndFilename(Pageable pageable, String filename);
}
