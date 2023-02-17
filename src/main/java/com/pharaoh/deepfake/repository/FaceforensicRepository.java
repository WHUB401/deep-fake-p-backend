package com.pharaoh.deepfake.repository;

import com.pharaoh.deepfake.entity.Faceforensic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface FaceforensicRepository extends JpaRepository<Faceforensic, Integer> {

}
