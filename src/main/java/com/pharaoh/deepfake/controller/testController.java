package com.pharaoh.deepfake.controller;

import com.pharaoh.deepfake.common.util.MissionStatus;
import com.pharaoh.deepfake.common.util.R;
import com.pharaoh.deepfake.entity.AdversarialDetection;
import com.pharaoh.deepfake.repository.AdversarialDetectionRepository;
import com.pharaoh.deepfake.service.AdversarialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

import static com.pharaoh.deepfake.DeepfakeApplication.missionStatus;


@RestController
@RequestMapping("/test")
public class testController {
    @Autowired
    AdversarialDetectionRepository adversarialDetectionRepository;

    @GetMapping("/pageable")
    public R query(Pageable pageable){//用spring自带的pageable对象来得到分页信息


        Page<AdversarialDetection> page =adversarialDetectionRepository.findAll(pageable);
        List<AdversarialDetection> list = page.getContent();
        R r = R.ok();
        r.setData(list);
        return r;
    }

    @GetMapping("/missionStatus")
    public MissionStatus missionStatus(){//用spring自带的pageable对象来得到分页信息
        return missionStatus;
    }
}
