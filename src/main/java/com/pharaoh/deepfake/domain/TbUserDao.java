package com.pharaoh.deepfake.domain;

import org.apache.ibatis.annotations.Mapper;

import java.util.HashMap;


@Mapper
public interface TbUserDao {
    public Integer login(HashMap params);
}
