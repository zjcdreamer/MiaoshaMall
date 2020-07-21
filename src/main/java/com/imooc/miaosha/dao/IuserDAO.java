package com.imooc.miaosha.dao;

import com.imooc.miaosha.domain.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface IuserDAO {

    //查找一名用户
    @Select("select * from user where id = #{id}")
    public User getById(@Param("id") int id);
}
