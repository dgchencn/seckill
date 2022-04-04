package com.wyu.seckill.service;

import com.wyu.seckill.entity.Order;

import java.util.List;

public interface OrderService {
    void createOrderAsync(Integer userId, int itemId, int amount, Integer promotionId);

    Order createOrder(int userId, int itemId, int amount, Integer promotionId, String itemStockLogId);

    List<Order> getOrderList(Integer userId, String status);

    void updateOrderStatus(Integer userId, String orderId, Integer orderStatus);
}
