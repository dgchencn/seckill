package com.wyu.seckill.service;

public interface PromotionService {
    String generateToken(Integer userId, int itemId, int promotionId);
}
