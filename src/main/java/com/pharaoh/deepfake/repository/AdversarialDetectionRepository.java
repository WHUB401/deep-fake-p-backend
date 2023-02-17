package com.pharaoh.deepfake.repository;
import com.pharaoh.deepfake.entity.AdversarialDetection;
import com.pharaoh.deepfake.service.AdversarialService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface AdversarialDetectionRepository extends JpaRepository<AdversarialDetection, Integer> {

    @Transactional
    boolean existsByDatasetAndFilename(String dataset, String filename);
    @Transactional
    void deleteByDataset(String dataset);
    @Transactional
    AdversarialDetection findAdversarialDetectionByDatasetAndFilename(String dataset, String filename);
    @Transactional
    void deleteByDatasetAndFilename(String dataset, String filename);
    @Transactional
    Page<AdversarialDetection> findAllByHasResult(Pageable pageable, boolean hasResult);
    @Transactional
    Page<AdversarialDetection> findAllByDatasetAndFilenameAndHasResult(Pageable pageable, String filename, String dataset, boolean hasResult);
    @Transactional
    Page<AdversarialDetection> findAllByDatasetAndHasResult(Pageable pageable, String dataset, boolean hasResult);
    @Transactional
    Page<AdversarialDetection> findAllByFilenameAndHasResult(Pageable pageable, String filename, boolean hasResult);
}
