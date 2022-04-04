package com.wyu.seckill.common;

import com.wyu.seckill.entity.Item;
import com.wyu.seckill.entity.Promotion;
import com.wyu.seckill.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
//springboot启动后，自动预先加载数据到redis
public class SimulateBackendData implements CommandLineRunner {

    @Autowired
    private ItemService itemService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public void run(String... args) throws Exception {
        cacheItemStock();
        initPromotionGate();
    }
    public void cacheItemStock() {
        List<Item> list = itemService.findItemsOnPromotion();
        for (Item item : list) {
            int stock = item.getItemStock().getStock();
            redisTemplate.opsForValue().set("item:stock:" + item.getId(), stock);
        }
    }
    public void initPromotionGate() {
        List<Item> list = itemService.findItemsOnPromotion();
        for (Item item : list) {
            int stock = item.getItemStock().getStock();
            Promotion promotion = item.getPromotion();
            redisTemplate.opsForValue().set("promotion:gate:" + promotion.getId(), stock * 5);
        }
    }
}
