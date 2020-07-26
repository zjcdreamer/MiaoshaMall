package com.imooc.miaosha.controller;

import com.imooc.miaosha.domain.MiaoShaUser;
import com.imooc.miaosha.redis.KeyPrefix;
import com.imooc.miaosha.redis.RedisService;
import com.imooc.miaosha.result.Result;
import com.imooc.miaosha.service.MiaoShaUserService;
import com.imooc.miaosha.service.UserService;
import com.imooc.miaosha.vo.LoginVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@Controller
@RequestMapping("/goods")
public class GoodsController {

    @Autowired
    UserService userService;

    @Autowired
    MiaoShaUserService miaoShaUserService;

    @Autowired
    RedisService redisService;

    @RequestMapping("/to_list")
    public String toList(Model model,
            @CookieValue(value = MiaoShaUserService.COOKIE_NAME_TOKEN, required = false) String cookieToToken,
            @RequestParam(value = MiaoShaUserService.COOKIE_NAME_TOKEN, required = false) String param){
        if (StringUtils.isEmpty(cookieToToken) && StringUtils.isEmpty(param)){
            return "login";
        }
        String token = StringUtils.isEmpty(param)?cookieToToken:param;
        MiaoShaUser userByToken = miaoShaUserService.getByToken(token);
        model.addAttribute("user", userByToken);
        return "goods_list";
    }
}
