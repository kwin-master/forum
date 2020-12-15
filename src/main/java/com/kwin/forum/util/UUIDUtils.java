package com.kwin.forum.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.UUID;

public class UUIDUtils {
    public static String generateUUID() {
        return UUID.randomUUID().toString().replace("-","");
    }

    //md5加密
    //hello + 3e4a8 -> abc123def456abc
    public static String md5(String key) {
        if(StringUtils.isBlank(key)) {
            return null;
        }
        return DigestUtils.md5DigestAsHex(key.getBytes());
    }
}
