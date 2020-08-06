package com.imooc.miaosha.service;

import com.imooc.miaosha.dao.GoodsDAO;
import com.imooc.miaosha.domain.MiaoShaOrder;
import com.imooc.miaosha.domain.MiaoShaUser;
import com.imooc.miaosha.domain.OrderInfo;
import com.imooc.miaosha.redis.GoodsKey;
import com.imooc.miaosha.redis.MiaoshaKey;
import com.imooc.miaosha.redis.RedisService;
import com.imooc.miaosha.util.MD5Util;
import com.imooc.miaosha.util.UUIDUtils;
import com.imooc.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MiaoShaService {

    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;

    @Autowired
    RedisService redisService;

    @Transactional
    public OrderInfo miaosha(MiaoShaUser user, GoodsVo goods) {
        //减库存、下订单、下秒杀订单
        boolean success = goodsService.reduceStock(goods);
        if (success){
            return orderService.createOrder(user, goods);
        }
        return null;
    }

    public String createMiaoshaPaht(MiaoShaUser user, long goodsId) {
        if (user == null){
            return null;
        }
        String path = MD5Util.md5(UUIDUtils.uuid() + "123456");
        redisService.set(MiaoshaKey.getMiaoshaPaht, ""+user.getId()+"_"+goodsId, path);
        return path;
    }

    public long getMiaoshaResult(MiaoShaUser user, long goodsId) {
        MiaoShaOrder order = orderService.getMiaoShaOrderByUserIdGoodsId(user.getId(), goodsId);
        if (order != null){
            return order.getId();
        }else {
            int stock = redisService.get(GoodsKey.getMiaoShaGoodsStock, ""+goodsId, Integer.class);
            if (stock <= 0){
                return -1;
            }else {
                return 0;
            }
        }
    }

    public boolean checkPath(MiaoShaUser user,long goodsId,String path) {
        String pathInRedis = redisService.get(MiaoshaKey.getMiaoshaPaht, ""+user.getId()+"_"+goodsId, String.class);
        return path.equals(pathInRedis);
    }
}
