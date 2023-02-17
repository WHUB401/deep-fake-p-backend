package com.pharaoh.deepfake.controller;


import com.pharaoh.deepfake.common.util.R;
import com.pharaoh.deepfake.entity.Image;
import com.pharaoh.deepfake.entity.Video;
import com.pharaoh.deepfake.repository.ImageRepository;
import com.pharaoh.deepfake.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping
public class FileList {

    @Autowired
    private VideoRepository videoRepository;
    @Autowired
    private ImageRepository imageRepository;

    @GetMapping("/findAll")
    public R findAll(@RequestParam String fileType){
        List findResult = null;
        if(fileType.equals("video")){
            findResult = videoRepository.findAll();
        }else if(fileType.equals("image")){
            findResult = imageRepository.findAll();
        }
        R r = R.ok("find success");
        HashMap<String,Object> data = new HashMap<String,Object>();
        data.put(fileType, findResult);
        r.setData(data);
        return r;
    }
}
