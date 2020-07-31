package com.imooc.miaosha.controller;

import com.imooc.miaosha.domain.MiaoShaUser;
import com.imooc.miaosha.redis.RedisService;
import com.imooc.miaosha.result.Result;
import com.imooc.miaosha.service.GoodsService;
import com.imooc.miaosha.service.MiaoShaUserService;
import com.imooc.miaosha.service.UserService;
import com.imooc.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    MiaoShaUserService miaoShaUserService;

    @Autowired
    RedisService redisService;

    @Autowired
    GoodsService goodsService;

    @RequestMapping("/info")
    @ResponseBody
    public Result<MiaoShaUser> info(Model model, MiaoShaUser user){
        return Result.success(user);
    }

}
