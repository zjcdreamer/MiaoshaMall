package com.imooc.miaosha.util;

import org.thymeleaf.util.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidatorUtil {
    public static final Pattern mobile_pattern = Pattern.compile("1\\d{10}");

    public static boolean isMobile(String str){
        if(StringUtils.isEmpty(str))
            return false;

        Matcher m = mobile_pattern.matcher(str);
        return m.matches();
    }
}
