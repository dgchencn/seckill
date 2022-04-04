package com.wyu.seckill.rocket.producer;

import com.alibaba.fastjson.JSONObject;
import com.wyu.seckill.entity.ItemStockLog;
import com.wyu.seckill.entity.Order;
import com.wyu.seckill.service.ItemService;
import com.wyu.seckill.service.OrderService;
import org.apache.rocketmq.spring.annotation.RocketMQTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;

@Service
@RocketMQTransactionListener
public class LocalTransactionListenerImpl implements RocketMQLocalTransactionListener {

    private Logger logger = LoggerFactory.getLogger(LocalTransactionListenerImpl.class);

    @Autowired
    private OrderService orderService;

    @Autowired
    private ItemService itemService;

    //进行本地事务，RocketMQ会在发送消息后自动调用该方法
    @Override
    public RocketMQLocalTransactionState executeLocalTransaction(Message message, Object arg) {
        try {
            String tag = message.getHeaders().get("rocketmq_TAGS").toString();
            if ("decrease_stock".equals(tag)) {
                return this.createOrder(message, arg);
            } else {
                return RocketMQLocalTransactionState.UNKNOWN;
            }
        } catch (Exception e) {
            logger.error("执行MQ本地事务时发生错误", e);
            return RocketMQLocalTransactionState.ROLLBACK;
        }
    }

    @Override
    public RocketMQLocalTransactionState checkLocalTransaction(Message message) {
        try {
            String tag = (String) message.getHeaders().get("rocketmq_TAGS");
            if ("decrease_stock".equals(tag)) {
                return this.checkStockStatus(message);
            } else {
                return RocketMQLocalTransactionState.UNKNOWN;
            }
        } catch (Exception e) {
            logger.error("检查MQ本地事务时发生错误", e);
            return RocketMQLocalTransactionState.ROLLBACK;
        }
    }

    //进行本地事务具体要调用的创建订单操作
    private RocketMQLocalTransactionState createOrder(Message msg, Object arg) {
        JSONObject param = (JSONObject) arg;

        int userId = (int) param.get("userId");
        int itemId = (int) param.get("itemId");
        int amount = (int) param.get("amount");
        int promotionId = (int) param.get("promotionId");
        String itemStockLogId = (String) param.get("itemStockLogId");

        try {
            Order order = orderService.createOrder(userId, itemId, amount, promotionId, itemStockLogId);
            logger.debug("本地事务提交完成 [" + order.getId() + "]");
            // 更新库存流水状态
            itemService.updateItemStockLogStatus(itemStockLogId, 1);
            logger.debug("更新流水完成 [" + itemStockLogId + "]");
            return RocketMQLocalTransactionState.COMMIT;
        } catch (Exception e) {
            logger.error("创建订单失败", e);
            itemService.increaseSales(itemId,amount);
            logger.debug("已恢复预扣减库存 [" + amount + "]");
            itemService.updateItemStockLogStatus(itemStockLogId, 3);
            logger.debug("更新流水完成 [" + itemStockLogId + "]");
            return RocketMQLocalTransactionState.ROLLBACK;
        }
    }

    //检查本地事务的操作结果
    private RocketMQLocalTransactionState checkStockStatus(Message msg) {
        JSONObject body = JSONObject.parseObject(new String((byte[]) msg.getPayload()));
        String itemStockLogId = (String) body.get("itemStockLogId");
        ItemStockLog itemStockLog = itemService.findItemStorkLogById(itemStockLogId);
        logger.debug("检查事务状态完成 [" + itemStockLog + "]");
        if (itemStockLog == null) {
            return RocketMQLocalTransactionState.ROLLBACK;
        } else if (itemStockLog.getStatus() == 0) {
            return RocketMQLocalTransactionState.UNKNOWN;
        } else if (itemStockLog.getStatus() == 1) {
            return RocketMQLocalTransactionState.COMMIT;
        } else {
            return RocketMQLocalTransactionState.ROLLBACK;
        }
    }
}
