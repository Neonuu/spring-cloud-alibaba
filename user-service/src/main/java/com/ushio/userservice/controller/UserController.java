package com.ushio.userservice.controller;

import com.ushio.commonmodule.entity.Order;
import com.ushio.commonmodule.entity.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping(value = "/user")
@RefreshScope
public class UserController {
    @Value("${config.redisTimeout}")
    private Long redisTimeout;

    @Autowired
    private RedisTemplate redisTemplate;

    @GetMapping(value = "/login")
    public String login (){

        //验证账号密码
        //jwt生成token
        String token = JwtUtil.getToken("123");
        //将token存入redis
        redisTemplate.opsForValue().set(token,token,redisTimeout, TimeUnit.SECONDS);
        //将token返回客户端
        return token;
    }


}
