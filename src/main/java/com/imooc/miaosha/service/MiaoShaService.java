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

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

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

    public BufferedImage createVerifyCodeImg(MiaoShaUser user, long goodsId) {
        if (user == null){
            return null;
        }
        int width = 80;
        int height = 32;
        //create the image
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        // set the background color
        g.setColor(new Color(0xDCDCDC));
        g.fillRect(0, 0, width, height);
        // draw the border
        g.setColor(Color.black);
        g.drawRect(0, 0, width - 1, height - 1);
        // create a random instance to generate the codes
        Random rdm = new Random();
        // make some confusion
        for (int i = 0; i < 50; i++) {
            int x = rdm.nextInt(width);
            int y = rdm.nextInt(height);
            g.drawOval(x, y, 0, 0);
        }
        // generate a random code
        String verifyCode = generateVerifyCode(rdm);
        g.setColor(new Color(0, 100, 0));
        g.setFont(new Font("Candara", Font.BOLD, 24));
        g.drawString(verifyCode, 8, 24);
        g.dispose();
        //把验证码存到redis中
        int rnd = calc(verifyCode);
        redisService.set(MiaoshaKey.getMiaoshaVerifyCode, user.getId()+","+goodsId, rnd);
        //输出图片
        return image;
    }

    private int calc(String verifyCode) {
        ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
        ScriptEngine javaScript = scriptEngineManager.getEngineByName("JavaScript");
        try {
            return (int)javaScript.eval(verifyCode);
        } catch (ScriptException e) {
            e.printStackTrace();
            return 0;
        }
    }

    private char[] ops = {'+', '-', '*'};

    private String generateVerifyCode(Random rdm) {
        int num1 = rdm.nextInt(10);
        int num2 = rdm.nextInt(10);
        int num3 = rdm.nextInt(10);
        char op1 = ops[rdm.nextInt(3)];
        char op2 = ops[rdm.nextInt(3)];
        return ""+num1 + op1 + num2 + op2 + num3;
    }


    public boolean verify(MiaoShaUser user, long goodsId, int verifyCode) {
        if (user == null || goodsId < 0)
            return false;
        Integer verifyCodeInRedis = redisService.get(MiaoshaKey.getMiaoshaVerifyCode, user.getId()+","+goodsId, Integer.class);
        if (verifyCodeInRedis == null ||  verifyCode - verifyCodeInRedis != 0)
            return false;
        redisService.delete(MiaoshaKey.getMiaoshaVerifyCode, user.getId()+","+goodsId);
        return true;
    }
}
