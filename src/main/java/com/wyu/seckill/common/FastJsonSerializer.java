package com.wyu.seckill.common;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import java.nio.charset.StandardCharsets;

public class FastJsonSerializer implements RedisSerializer<Object> {
    @Override
    public byte[] serialize(Object o) throws SerializationException {
        if (o == null){
            return null;
        }
        String json = JSON.toJSONString(o, SerializerFeature.WriteClassName);
        return json.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public Object deserialize(byte[] bytes) throws SerializationException {
        if (bytes == null || bytes.length <= 0) {
            return null;
        }
        String json = new String(bytes, StandardCharsets.UTF_8);
        return JSON.parseObject(json,Object.class, Feature.SupportAutoType);
    }
}
