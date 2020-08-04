package com.imooc.miaosha.controller;

import com.imooc.miaosha.domain.User;
import com.imooc.miaosha.rabbitmq.MQSender;
import com.imooc.miaosha.redis.RedisService;
import com.imooc.miaosha.redis.UserKey;
import com.imooc.miaosha.result.Result;
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

    @Autowired
    MQSender mqSender;

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

    @ResponseBody
    @RequestMapping("/mq")
    public Result<String> mq(){
        mqSender.send("hello,mq");
        return Result.success("ok");
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
    public Result<User> redisGet(){
        User user = redisService.get(UserKey.getById, "" + 1, User.class);
        return Result.success(user);
    }

    @RequestMapping("/redis/set")
    @ResponseBody
    public Result<String> redisSet(){
        Boolean b = redisService.set(UserKey.getById, ""+2, "hello");
        String key2 = redisService.get(UserKey.getById, ""+2, String.class);
        return Result.success(key2);
    }
}
