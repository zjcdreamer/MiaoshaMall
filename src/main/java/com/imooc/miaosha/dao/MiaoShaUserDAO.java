package com.imooc.miaosha.dao;

import com.imooc.miaosha.domain.MiaoShaUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface MiaoShaUserDAO {

    @Select("select * from miaosha_user where id = #{id}")
    public MiaoShaUser getUserById(@Param("id") Long id);

    @Update("update miaosha_user set password = #{password} where id = #{id}")
    void update(MiaoShaUser miaoShaUser);
}
