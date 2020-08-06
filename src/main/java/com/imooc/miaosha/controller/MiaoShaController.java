package com.imooc.miaosha.controller;

import com.imooc.miaosha.domain.MiaoShaMessage;
import com.imooc.miaosha.domain.MiaoShaOrder;
import com.imooc.miaosha.domain.MiaoShaUser;
import com.imooc.miaosha.domain.OrderInfo;
import com.imooc.miaosha.rabbitmq.MQSender;
import com.imooc.miaosha.redis.GoodsKey;
import com.imooc.miaosha.redis.RedisService;
import com.imooc.miaosha.result.CodeMsg;
import com.imooc.miaosha.result.Result;
import com.imooc.miaosha.service.*;
import com.imooc.miaosha.util.MD5Util;
import com.imooc.miaosha.util.UUIDUtils;
import com.imooc.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/miaosha")
public class MiaoShaController implements InitializingBean {

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

    @Autowired
    RedisService redisService;

    @Autowired
    MQSender mqSender;

    public Map overMap = new HashMap<Long, Boolean>();

    /**
     * 接口优化后（又加了Redis和MQ)：QPS:510
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        List<GoodsVo> goodLists = goodsService.listGoodsVo();
        if (goodLists == null)
            return;
        for (GoodsVo good : goodLists){
            redisService.set(GoodsKey.getMiaoShaGoodsStock, ""+good.getId(), good.getStockCount());
            overMap.put(good.getId(), false);
        }
    }

    @RequestMapping(value = "/{path}/do_miaosha", method = RequestMethod.POST)
    @ResponseBody
    public Result<Integer> doMiaoSha(Model model, MiaoShaUser user,
                                     @RequestParam("goodsId") long goodsId,
                                     @PathVariable("path") String path){
        //判断用户是否为空
        if(user == null){
            return Result.error(CodeMsg.MOBILE_EMPTY);
        }
        model.addAttribute("user",user);
        //判断path
        boolean check = miaoShaService.checkPath(user, goodsId, path);
        if (!check)
            return Result.error(CodeMsg.REQUEST_ILLIAGEL);
        //预减库存，判断该秒杀商品是否还有库存
        boolean over = (boolean)overMap.get(goodsId);
        if (over){
            return Result.error(CodeMsg.MIAOSHA_OVER);
        }
        long stock = redisService.decr(GoodsKey.getMiaoShaGoodsStock, ""+goodsId);
        if (stock < 0){
           //秒杀商品卖完了
            overMap.put(goodsId, true);
            return Result.error(CodeMsg.MIAOSHA_OVER);
        }

        GoodsVo goods = goodsService.getGoodsByGoodsId(goodsId);
        //判断该用户是否重复秒杀该商品
        MiaoShaOrder miaoShaOrder = orderService.getMiaoShaOrderByUserIdGoodsId(user.getId(), goods.getId());
        if (miaoShaOrder != null){
            model.addAttribute("errmsg", CodeMsg.REPEATE_MIAOSHA.getMsg());
            return Result.error(CodeMsg.REPEATE_MIAOSHA);
        }

        //入队
        MiaoShaMessage miaoShaMessage = new MiaoShaMessage();
        miaoShaMessage.setMiaoShaUser(user);
        miaoShaMessage.setGoodsId(goodsId);
        mqSender.miaoShaSend(miaoShaMessage);
        return Result.success(0);
    }


    /*
    优化前：QPS:69
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
    */

    @RequestMapping(value = "/path", method = RequestMethod.GET)
    @ResponseBody
    public Result<String> getMiaoshaPath(MiaoShaUser user,
                                         @RequestParam("goodsId") long goodsId,
                                         @RequestParam("verifyCode") int verifyCode){
        if (user == null)
            return Result.error(CodeMsg.MOBILE_NOT_EXIST);
        boolean verifySuccess = miaoShaService.verify(user, goodsId, verifyCode);
        if (!verifySuccess){
            return Result.error(CodeMsg.VERIFYCODE_ERROR);
        }
        String path = miaoShaService.createMiaoshaPaht(user, goodsId);
        return Result.success(path);
    }

    /**
     *  orderId：成功
     *  -1：秒杀失败
     *  0： 排队中
     */
    @RequestMapping(value = "/result", method = RequestMethod.GET)
    @ResponseBody
    public Result<Long> getMiaoshaResult(MiaoShaUser user, @RequestParam("goodsId") long goodsId){
        if (user == null)
            return Result.error(CodeMsg.MOBILE_NOT_EXIST);
        long result = miaoShaService.getMiaoshaResult(user, goodsId);
        return Result.success(result);
    }

    @RequestMapping(value = "/verifyCodeImg", method = RequestMethod.GET)
    @ResponseBody
    public Result<Long> getMiaoshaVerifyCodeImg(HttpServletResponse response, MiaoShaUser user, @RequestParam("goodsId") long goodsId){
        if (user == null)
            return Result.error(CodeMsg.SESSION_ERROR);
        try {
            BufferedImage img = miaoShaService.createVerifyCodeImg(user, goodsId);
            OutputStream out = response.getOutputStream();
            ImageIO.write(img, "JPEG", out);
            out.flush();
            out.close();
            return null;
        }catch(Exception e) {
            e.printStackTrace();
            return Result.error(CodeMsg.MIAOSHA_FAIL);
        }
    }

}
