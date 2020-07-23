package com.imooc.miaosha.controller;

import com.imooc.miaosha.domain.User;
import com.imooc.miaosha.redis.RedisService;
import com.imooc.miaosha.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/demo")
public class Samplecontroller {

    @Autowired
    UserService userService;

    @Autowired
    RedisService redisService;

    @RequestMapping("/thymeleaf")
    public String thymeleaf(Model model){
        model.addAttribute("name", "Jack");
        System.out.println("输出============================");
        return "hello";
    }

    //查找一号用户
    @ResponseBody
    @RequestMapping("/db/get")
    public Result<User> get(){
        User user = userService.getById(1);
        return Result.success(user);
    }

    //这部分是检测事务，注意看Service层的@Transtractioanl
    @ResponseBody
    @RequestMapping("/db/tx")
    public Result<Boolean> tx(){
        userService.tx();
        return Result.success(true);
    }

    @RequestMapping("/redis/get")
    @ResponseBody
    public Result<Long> redisGet(){
        Long key1 = redisService.get("key1", Long.class);
        return Result.success(key1);
    }
}
