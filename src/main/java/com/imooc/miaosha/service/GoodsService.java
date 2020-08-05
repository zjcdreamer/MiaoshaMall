package com.imooc.miaosha.service;

import com.imooc.miaosha.dao.GoodsDAO;
import com.imooc.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GoodsService {

    @Autowired
    GoodsDAO goodsDAO;

    public List<GoodsVo> listGoodsVo(){
        return goodsDAO.listGoodsVo();
    }

    public GoodsVo getGoodsByGoodsId(Long goodsId) {
        return goodsDAO.getGoodsByGoodsId(goodsId);
    }

    public boolean reduceStock(GoodsVo goods) {
        int ret = goodsDAO.reduceStock(goods.getId());
        return ret > 0;
    }
}
