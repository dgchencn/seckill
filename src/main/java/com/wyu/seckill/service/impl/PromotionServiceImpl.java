package com.wyu.seckill.service.impl;

import com.wyu.seckill.entity.Item;
import com.wyu.seckill.entity.User;
import com.wyu.seckill.service.ItemService;
import com.wyu.seckill.service.PromotionService;
import com.wyu.seckill.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class PromotionServiceImpl implements PromotionService {

    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private ItemService itemService;

    @Override
    public String generateToken(Integer userId, int itemId, int promotionId) {
        //参数不合法
        if (userId < 0 || itemId < 0 || promotionId < 0) {
            return null;
        }

        // 售罄标识
        if (redisTemplate.hasKey("item:stock:over:" + itemId)) {
            return null;
        }

        // 校验用户
        User user = userService.findUserFromCache(userId);
        if (user == null) {
            return null;
        }

        // 校验商品
        Item item = itemService.findItemInCache(itemId);
        if (item == null) {
            return null;
        }

        // 校验活动
        if (item.getPromotion() == null
                || !item.getPromotion().getId().equals(promotionId)
                || item.getPromotion().getStatus() != 0) {
            return null;
        }

        // 秒杀大闸
        ValueOperations v = redisTemplate.opsForValue();
        if (v.decrement("promotion:gate:" + promotionId, 1) < 0) {
            return null;
        }

        String key = "promotion:token:" + userId + ":" + itemId + ":" + promotionId;
        String token = UUID.randomUUID().toString().replace("-", "");
        v.set(key, token, 10, TimeUnit.MINUTES);

        return token;
    }
}
