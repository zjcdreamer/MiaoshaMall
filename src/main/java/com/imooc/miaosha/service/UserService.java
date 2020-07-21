package com.imooc.miaosha.service;

import com.imooc.miaosha.dao.IuserDAO;
import com.imooc.miaosha.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    IuserDAO iuserDAO;

    public User getById(int id){
        return iuserDAO.getById(id);
    }
}
