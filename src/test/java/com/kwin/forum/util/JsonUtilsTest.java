package com.kwin.forum.util;

import com.kwin.forum.ForumApplicationTests;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class JsonUtilsTest extends ForumApplicationTests {

    @Test
    public void getJSONString() {
        Map<String,Object> map = new HashMap<>();
        map.put("name","zhangsan");
        map.put("age",25);
        System.out.println(JsonUtils.getJSONString(0,"ok",map));
    }
}