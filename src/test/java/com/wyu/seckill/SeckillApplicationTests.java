package com.wyu.seckill;

import com.wyu.seckill.dao.ItemMapper;
import com.wyu.seckill.entity.Item;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class SeckillApplicationTests {

    @Autowired
    private ItemMapper itemMapper;
    @Test
    void contextLoads() {
    }

    @Test
    void test() {
        List<Item> items = itemMapper.selectAll();
        System.out.println(items.size());
        for (Item item:items) {
            System.out.println(item.getTitle());
        }
    }

}
