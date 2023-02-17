package com.pharaoh.deepfake.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.pharaoh.deepfake.common.util.R;
import com.pharaoh.deepfake.controller.form.LoginForm;
import cn.hutool.json.JSONUtil;
import com.pharaoh.deepfake.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequestMapping("/user")
public class userController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public R login(@RequestBody LoginForm loginform) {
        HashMap param = JSONUtil.parse(loginform).toBean(HashMap.class);
        Integer loginId = userService.login(param);
        if(loginId!=null) {
            StpUtil.login(loginId);
            String token = StpUtil.getTokenInfo().getTokenValue();
            R r = R.ok();
            HashMap<String,Object> data = new HashMap<String,Object>();
            data.put("result", loginId != null);
            data.put("token",token);
            data.put("id",loginId);
            r.setData(data);
            return r;
        }
        R r = R.error(50001, "username or password error.");
        return r;
    }

    @GetMapping("/info")
    public R getInfo(@RequestParam("id") Integer id, @RequestParam("token") String token) {
        StpUtil.login(id);
        String test = StpUtil.getTokenValueByLoginId(id);
        R r;
        if(test.equals(token)){
            r = R.ok();
        }else{
            r = R.error(50002, "error token!");
        }
        return r;
    }

    @PostMapping("/logout")
    public R logout(@RequestHeader("X-Token") String token) {
        StpUtil.logoutByTokenValue(token);
        R r = R.ok();
        return r;
    }

}
