package com.wyu.seckill.service;


import com.wyu.seckill.entity.Item;
import com.wyu.seckill.entity.ItemStockLog;

import java.util.List;

public interface ItemService {
    List<Item> findItemsOnPromotion();

    Item findItemById(int id);

    Item findItemInCache(int id);

    ItemStockLog createItemStockLog(int itemId, int amount);

    void increaseSales(int itemId, int amount);

    boolean decreaseStock(int itemId, int amount);

    void updateItemStockLogStatus(String id, int status);

    boolean decreaseStockInCache(int itemId, int amount);

    boolean increaseStockInCache(int itemId, int amount);

    ItemStockLog findItemStorkLogById(String id);
}
