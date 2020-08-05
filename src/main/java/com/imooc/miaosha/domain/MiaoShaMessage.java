package com.imooc.miaosha.domain;

public class MiaoShaMessage {

    private MiaoShaUser miaoShaUser;
    private long goodsId;

    public MiaoShaUser getMiaoShaUser() {
        return miaoShaUser;
    }

    public void setMiaoShaUser(MiaoShaUser miaoShaUser) {
        this.miaoShaUser = miaoShaUser;
    }

    public long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(long goodsId) {
        this.goodsId = goodsId;
    }
}
