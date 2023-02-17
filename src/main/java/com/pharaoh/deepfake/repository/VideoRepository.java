package com.pharaoh.deepfake.repository;

import com.pharaoh.deepfake.entity.Video;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.transaction.annotation.Transactional;

public interface VideoRepository extends JpaRepository<Video, Integer> {

    @Transactional
    void deleteByFilename(String Videoname);

    boolean existsByFilename(String Videoname);
}
