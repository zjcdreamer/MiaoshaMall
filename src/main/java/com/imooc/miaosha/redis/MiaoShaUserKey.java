package com.imooc.miaosha.redis;

public class MiaoShaUserKey extends BasePrefix{

    public static final int TOKEN_EMPIRE = 3600 *24 * 2;

    public MiaoShaUserKey(String prefix){
        super(prefix);
    }

    public MiaoShaUserKey(int empire, String prefix){
        super(TOKEN_EMPIRE, prefix);
    }

    public static MiaoShaUserKey token = new MiaoShaUserKey(TOKEN_EMPIRE, "token");
    public static MiaoShaUserKey id = new MiaoShaUserKey(0, "id");
}
