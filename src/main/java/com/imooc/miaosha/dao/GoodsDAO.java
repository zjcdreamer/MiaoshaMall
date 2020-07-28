package com.imooc.miaosha.dao;

import com.imooc.miaosha.vo.GoodsVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface GoodsDAO {

    @Select("select g.*, mg.stock_count, mg.start_date, mg.end_date,mg.miaosha_price from goods g left join miaosha_goods mg on g.id = mg.id")
    public List<GoodsVo> listGoodsVo();
}
