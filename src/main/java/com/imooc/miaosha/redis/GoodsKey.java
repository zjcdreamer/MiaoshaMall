package com.imooc.miaosha.redis;

public class GoodsKey extends BasePrefix{

    public GoodsKey(String prefix){
        super(prefix);
    }
    public GoodsKey(int empireSecond, String prefix){
        super(empireSecond, prefix);
    }

    public static GoodsKey getGoodsList = new GoodsKey(60, "gl");
    public static GoodsKey getGoodsDetail = new GoodsKey(60, "gd");
}
