package com.pharaoh.deepfake.repository;

import com.pharaoh.deepfake.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.transaction.annotation.Transactional;

public interface ImageRepository extends JpaRepository<Image, Integer> {

    @Transactional
    void deleteByFilename(String Imagename);

    void deleteAllInBatch();

    boolean existsByFilename(String Imagename);
}
