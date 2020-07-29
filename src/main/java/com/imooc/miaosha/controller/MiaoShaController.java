package com.imooc.miaosha.controller;

import com.imooc.miaosha.domain.MiaoShaOrder;
import com.imooc.miaosha.domain.MiaoShaUser;
import com.imooc.miaosha.domain.OrderInfo;
import com.imooc.miaosha.redis.RedisService;
import com.imooc.miaosha.result.CodeMsg;
import com.imooc.miaosha.service.*;
import com.imooc.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/miaosha")
public class MiaoShaController {

    @Autowired
    UserService userService;

    @Autowired
    MiaoShaUserService miaoShaUserService;

    @Autowired
    MiaoShaService miaoShaService;

    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;

    @RequestMapping("/do_miaosha")
    public String doMiaoSha(Model model, MiaoShaUser user, @RequestParam("goodsId") long goodsId){
        //判断用户是否为空
        if(user == null){
            return "login";
        }
        model.addAttribute("user",user);
        //判断该秒杀商品是否还有库存
        GoodsVo goods = goodsService.getGoodsByGoodsId(goodsId);
        int stock = goods.getStockCount();
        if (stock <= 0){
            model.addAttribute("errmsg", CodeMsg.MIAOSHA_OVER.getMsg());
            return "miaosha_fail";
        }

        //判断该用户是否重复秒杀该商品
        MiaoShaOrder miaoShaOrder = orderService.getMiaoShaOrderByUserIdGoodsId(user.getId(), goods.getId());
        if (miaoShaOrder != null){
            model.addAttribute("errmsg", CodeMsg.REPEATE_MIAOSHA.getMsg());
            return "miaosha_fail";
        }

        //减库存、下订单、下秒杀订单
        OrderInfo orderInfo = miaoShaService.miaosha(user, goods);
        model.addAttribute("orderInfo", orderInfo);
        model.addAttribute("goods", goods);
        return "order_detail";
    }
}
