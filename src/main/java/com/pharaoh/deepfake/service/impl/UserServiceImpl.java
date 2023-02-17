package com.pharaoh.deepfake.service.impl;
import com.pharaoh.deepfake.domain.TbUserDao;
import com.pharaoh.deepfake.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class UserServiceImpl implements UserService {

    @Autowired(required = false)
    private TbUserDao userDao;

    @Override
    public Integer login(HashMap params) {
        Integer userId = userDao.login(params);
        return userId;
    }
}
