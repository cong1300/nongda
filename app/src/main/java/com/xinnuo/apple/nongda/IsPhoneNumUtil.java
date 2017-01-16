package com.xinnuo.apple.nongda;
/**
 * 电信号段正则表达式 根据传过来的电话号判断是否是电信号段
 * */

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2016/11/1 0001.
 */

public class IsPhoneNumUtil {
    public static boolean isMobileNumber(String phoneNumber) {
        Pattern pattern = Pattern.compile("^(133|149|153|173|177|180|181|189)\\d{8}$");
        Matcher matcher = pattern.matcher(phoneNumber);
        return matcher.matches();
    }
}
