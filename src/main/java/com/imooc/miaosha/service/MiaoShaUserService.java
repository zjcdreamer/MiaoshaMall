package com.imooc.miaosha.service;

import com.imooc.miaosha.dao.MiaoShaUserDAO;
import com.imooc.miaosha.domain.MiaoShaUser;
import com.imooc.miaosha.exception.GlobalException;
import com.imooc.miaosha.result.CodeMsg;
import com.imooc.miaosha.result.Result;
import com.imooc.miaosha.util.MD5Util;
import com.imooc.miaosha.vo.LoginVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MiaoShaUserService {

    @Autowired
    MiaoShaUserDAO miaoShaUserDAO;

    public boolean login(LoginVo loginVo){
        if (loginVo == null)
            throw new GlobalException(CodeMsg.SERVER_ERROR);

        MiaoShaUser miaoShaUser = miaoShaUserDAO.getUserById(Long.parseLong(loginVo.getMobile()));
        if (miaoShaUser == null)
            throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
        String dbpassword = miaoShaUser.getPassword();
        String salt = miaoShaUser.getSalt();
        String formPass = loginVo.getPassword();
        String calPass = MD5Util.formPassToDBPass(formPass, salt);
        if(!dbpassword.equals(calPass))
            throw new GlobalException(CodeMsg.PASSWORD_ERROR);
        return true;
    }
}
