package com.imooc.miaosha.service;

import com.imooc.miaosha.dao.MiaoShaUserDAO;
import com.imooc.miaosha.domain.MiaoShaUser;
import com.imooc.miaosha.exception.GlobalException;
import com.imooc.miaosha.redis.KeyPrefix;
import com.imooc.miaosha.redis.MiaoShaUserKey;
import com.imooc.miaosha.redis.RedisService;
import com.imooc.miaosha.result.CodeMsg;
import com.imooc.miaosha.result.Result;
import com.imooc.miaosha.util.MD5Util;
import com.imooc.miaosha.util.UUIDUtils;
import com.imooc.miaosha.vo.LoginVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@Service
public class MiaoShaUserService {
    public static final String COOKIE_NAME_TOKEN = "token";

    @Autowired
    MiaoShaUserDAO miaoShaUserDAO;

    @Autowired
    RedisService redisService;

    public boolean login(HttpServletResponse response, LoginVo loginVo){
        if (loginVo == null)
            throw new GlobalException(CodeMsg.SERVER_ERROR);

        //检验手机号码是否正确
        MiaoShaUser miaoShaUser = miaoShaUserDAO.getUserById(Long.parseLong(loginVo.getMobile()));
        if (miaoShaUser == null)
            throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);

        //校验密码是否正确
        String dbpassword = miaoShaUser.getPassword();
        String salt = miaoShaUser.getSalt();
        String formPass = loginVo.getPassword();
        String calPass = MD5Util.formPassToDBPass(formPass, salt);
        if(!dbpassword.equals(calPass))
            throw new GlobalException(CodeMsg.PASSWORD_ERROR);

        String token = UUIDUtils.uuid();
        redisService.set(MiaoShaUserKey.token, token, miaoShaUser);
        Cookie cookie = new Cookie(COOKIE_NAME_TOKEN, token);
        cookie.setMaxAge(MiaoShaUserKey.token.empireSecond());
        cookie.setPath("/");
        response.addCookie(cookie);
        return true;
    }

    public MiaoShaUser getByToken(String token) {
        if (StringUtils.isEmpty(token)){
            return null;
        }
        return redisService.get(MiaoShaUserKey.token, token, MiaoShaUser.class);
    }
}
