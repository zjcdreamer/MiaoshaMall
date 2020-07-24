package com.imooc.miaosha.redis;

public abstract class BasePrefix  implements  KeyPrefix{

    int empireSecond;
    String prefix;

    public BasePrefix(){};

    public BasePrefix(String prefix){
        this(0, prefix);
    }

    public BasePrefix(int empireSecond, String prefix){
        this.empireSecond = empireSecond;
        this.prefix = prefix;
    }

    @Override
    public int empireSecond() {
        return empireSecond;
    }

    @Override
    public String getPrefix() {
        String name = getClass().getSimpleName();
        return name+":"+prefix;
    }
}
