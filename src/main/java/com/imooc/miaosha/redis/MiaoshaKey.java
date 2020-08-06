package com.imooc.miaosha.redis;

public class MiaoshaKey extends BasePrefix {

    public int empire;
    public String prefix;

    public MiaoshaKey(String prefix){
        super(prefix);
    }

    public MiaoshaKey(int empire, String prefix){
        super(empire, prefix);
    }

    public static MiaoshaKey getMiaoshaPaht = new MiaoshaKey(300, "mp");
}
