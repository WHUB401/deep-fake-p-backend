package com.pharaoh.deepfake.controller;

import com.pharaoh.deepfake.common.util.R;
import com.pharaoh.deepfake.entity.AdversarialDetection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/testGet")
public class testGetController {

    @GetMapping("/test")
    public String test(){//用spring自带的pageable对象来得到分页信息
        return "This is a get rest.";
    }

}
