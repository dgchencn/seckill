package com.wyu.seckill.common;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Toolbox implements ErrorCode {

    private static final String salt = "起来饥寒交迫的奴隶！";

    public static String md5(String str) {
        if (StringUtils.isEmpty(str)) {
            throw new BusinessException(PARAMETER_ERROR, "参数不合法！");
        }

        return DigestUtils.md5DigestAsHex((str + salt).getBytes());
    }

    public static String format(Date date, String pattern) {
        return new SimpleDateFormat(pattern).format(date);
    }

}
