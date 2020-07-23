package com.imooc.miaosha.service;

import com.imooc.miaosha.dao.IuserDAO;
import com.imooc.miaosha.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    @Autowired
    IuserDAO iuserDAO;

    public User getById(int id){
        return iuserDAO.getById(id);
    }

    @Transactional
    public boolean tx(){
        User u1 = new User(1,"hhh");
        User u2 = new User(2, "tx");
        iuserDAO.insert(u2);
        iuserDAO.insert(u1);
        return true;
    }
}
