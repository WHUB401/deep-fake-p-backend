package com.pharaoh.deepfake.repository;

import com.pharaoh.deepfake.entity.TraceDetection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface TraceDetectionRepository extends JpaRepository<TraceDetection, Integer> {
    @Transactional
    boolean existsByFilename(String filename);
    @Transactional
    TraceDetection findTraceDetectionByFilename(String filename);
    @Transactional
    void deleteByFilename(String filename);
    @Transactional
    void deleteAllInBatch();
    @Transactional
    Page findAllByAndFilename(Pageable pageable, String filename);
}
