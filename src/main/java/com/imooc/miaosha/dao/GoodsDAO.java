package com.imooc.miaosha.dao;

import com.imooc.miaosha.vo.GoodsVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface GoodsDAO {

    @Select("select g.*, mg.stock_count, mg.start_date, mg.end_date,mg.miaosha_price from goods g left join miaosha_goods mg on g.id = mg.id")
    public List<GoodsVo> listGoodsVo();

    @Select("select g.*, mg.stock_count, mg.start_date, mg.end_date,mg.miaosha_price from goods g left join miaosha_goods mg on g.id = mg.id where g.id = #{goodsId}")
    GoodsVo getGoodsByGoodsId(Long goodsId);

    @Update("update miaosha_goods set stock_count = stock_count - 1 where goods_id = #{id}")
    int reduceStock(@Param("id") long id);
}
