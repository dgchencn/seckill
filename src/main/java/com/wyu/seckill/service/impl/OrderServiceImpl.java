package com.wyu.seckill.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.wyu.seckill.common.BusinessException;
import com.wyu.seckill.common.ErrorCode;
import com.wyu.seckill.common.Toolbox;
import com.wyu.seckill.dao.OrderMapper;
import com.wyu.seckill.dao.SerialNumberMapper;
import com.wyu.seckill.entity.*;
import com.wyu.seckill.service.ItemService;
import com.wyu.seckill.service.OrderService;
import com.wyu.seckill.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.TransactionSendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService, ErrorCode {

    private Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private SerialNumberMapper serialNumberMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserService userService;

    @Autowired
    private RocketMQTemplate rocketMQTemplate;


    @Override
    public void createOrderAsync(Integer userId, int itemId, int amount, Integer promotionId) {
        // 售罄标识
        if (redisTemplate.hasKey("item:stock:over:" + itemId)) {
            throw new BusinessException(STOCK_NOT_ENOUGH, "已经售罄！");
        }

        // 生成库存流水
        ItemStockLog itemStockLog = itemService.createItemStockLog(itemId, amount);
        logger.debug("生成库存流水完成 [" + itemStockLog.getId() + "]");

        // 消息体
        JSONObject body = new JSONObject();
        body.put("itemId", itemId);
        body.put("amount", amount);
        body.put("itemStockLogId", itemStockLog.getId());

        // 本地事务参数
        JSONObject arg = new JSONObject();
        arg.put("userId", userId);
        arg.put("itemId", itemId);
        arg.put("amount", amount);
        arg.put("promotionId", promotionId);
        arg.put("itemStockLogId", itemStockLog.getId());

        String dest = "seckill:decrease_stock";
        Message msg = MessageBuilder.withPayload(body.toString()).build();
        try {
            logger.debug("尝试投递扣减库存消息 [" + body.toString() + "]");
            TransactionSendResult sendResult = rocketMQTemplate.sendMessageInTransaction(dest, msg, arg);
            if (sendResult.getLocalTransactionState() == LocalTransactionState.UNKNOW) {
                throw new BusinessException(UNDEFINED_ERROR, "创建订单失败！");
            } else if (sendResult.getLocalTransactionState() == LocalTransactionState.ROLLBACK_MESSAGE) {
                throw new BusinessException(CREATE_ORDER_FAILURE, "创建订单失败！");
            }
        } catch (MessagingException e) {
            throw new BusinessException(CREATE_ORDER_FAILURE, "创建订单失败！");
        }
    }

    @Override
    @Transactional
    public Order createOrder(int userId, int itemId, int amount, Integer promotionId, String itemStockLogId) {
        // 校验参数
        if (amount < 1 || (promotionId != null && promotionId.intValue() <= 0)) {
            throw new BusinessException(PARAMETER_ERROR, "指定的参数不合法！");
        }

        // 校验用户
//        User user = userService.findUserById(userId);
//        User user = userService.findUserFromCache(userId);
//        if (user == null) {
//            throw new BusinessException(PARAMETER_ERROR, "指定的用户不存在！");
//        }

        // 校验商品
//        Item item = itemService.findItemById(itemId);
        Item item = itemService.findItemInCache(itemId);
        if (item == null) {
            throw new BusinessException(PARAMETER_ERROR, "指定的商品不存在！");
        }

        // 校验库存
//        int stock = item.getItemStock().getStock();
//        if (amount > stock) {
//            throw new BusinessException(STOCK_NOT_ENOUGH, "库存不足！");
//        }

        // 校验活动
//        if (promotionId != null) {
//            if (item.getPromotion() == null) {
//                throw new BusinessException(PARAMETER_ERROR, "指定的商品无活动！");
//            } else if (!item.getPromotion().getId().equals(promotionId)) {
//                throw new BusinessException(PARAMETER_ERROR, "指定的活动不存在！");
//            } else if (item.getPromotion().getStatus() != 0) {
//                throw new BusinessException(PARAMETER_ERROR, "指定的活动未开始！");
//            }
//        }

        // 扣减库存
        // 增加行锁：前提是商品主键字段上必须有索引。
        // 缓存库存：异步同步数据库并保证最终一致性。
//        boolean successful = itemService.decreaseStock(itemId, amount);
        boolean successful = itemService.decreaseStockInCache(itemId, amount);
        logger.debug("预扣减库存完成 [" + successful + "]");
        if (!successful) {
            throw new BusinessException(STOCK_NOT_ENOUGH, "库存不足！");
        }

        // 生成订单
        User user = userService.findUserFromCache(userId);
        Order order = new Order();
        order.setId(this.generateOrderID());
        order.setUserId(userId);
        order.setItemId(itemId);
        order.setPromotionId(promotionId);
        order.setOrderPrice(promotionId != null ? item.getPromotion().getPromotionPrice() : item.getPrice());
        order.setOrderAmount(amount);
        order.setOrderTotal(order.getOrderPrice().multiply(new BigDecimal(amount)));
        order.setOrderTime(new Timestamp(System.currentTimeMillis()));
        order.setOrderStatus(0);
        order.setReceiverName(user.getReceiverName());
        order.setReceiverPhone(user.getReceiverPhone());
        order.setReceiverAddress(user.getReceiverAddress());
        orderMapper.insert(order);
        logger.debug("生成订单完成 [" + order.getId() + "]");

        // 更新销量
//        itemService.increaseSales(itemId, amount);
//        logger.debug("更新销量完成 [" + itemId + "]");
        JSONObject body = new JSONObject();
        body.put("itemId", itemId);
        body.put("amount", amount);
        Message msg = MessageBuilder.withPayload(body.toString()).build();
        rocketMQTemplate.asyncSend("seckill:increase_sales", msg, new SendCallback() {

            @Override
            public void onSuccess(SendResult sendResult) {
                logger.debug("投递增加商品销量消息成功");
            }

            @Override
            public void onException(Throwable e) {
                logger.error("投递增加商品销量消息失败", e);
            }
        }, 60 * 1000);
        return order;
    }

    // 查询订单列表
    @Override
    public List<Order> getOrderList(Integer userId, String status) {
        if (StringUtils.isEmpty(status))
            return orderMapper.selectByUserId(userId);
        else
            return orderMapper.selectByUserId(userId).stream().filter(
                    order -> order.getOrderStatus().toString().equals(status)
            ).collect(Collectors.toList());
    }

    @Override
    public void updateOrderStatus(Integer userId, String orderId, Integer orderStatus) {
        Order order = orderMapper.selectByPrimaryKey(orderId);
        if (order == null || order.getUserId()!=userId || order.getOrderStatus()!=orderStatus-1)
            throw new BusinessException(PARAMETER_ERROR, "参数有误！");
        else {
            order.setOrderStatus(orderStatus);
            try {
                orderMapper.updateByPrimaryKey(order);
            } catch (Exception e) {
                throw new BusinessException(ErrorCode.UNDEFINED_ERROR, "订单状态更改失败！");
            }
        }

    }

    /**
     * 格式：日期 + 流水
     * 示例：20210123000000000001
     *
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    String generateOrderID() {
        StringBuilder sb = new StringBuilder();

        // 拼入日期
        sb.append(Toolbox.format(new Date(), "yyyyMMdd"));

        // 获取流水号
        SerialNumber serial = serialNumberMapper.selectByPrimaryKey("order_serial");
        Integer value = serial.getValue();

        // 更新流水号
        serial.setValue(value + serial.getStep());
        serialNumberMapper.updateByPrimaryKey(serial);

        // 拼入流水号
        String prefix = "000000000000".substring(value.toString().length());
        sb.append(prefix).append(value);

        return sb.toString();
    }
}
