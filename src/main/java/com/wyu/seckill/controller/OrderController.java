package com.wyu.seckill.controller;

import com.google.common.util.concurrent.RateLimiter;
import com.wyu.seckill.common.ErrorCode;
import com.wf.captcha.ArithmeticCaptcha;
import com.wyu.seckill.common.BusinessException;
import com.wyu.seckill.common.ResponseModel;
import com.wyu.seckill.entity.Order;
import com.wyu.seckill.entity.User;
import com.wyu.seckill.service.OrderService;
import com.wyu.seckill.service.PromotionService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/order")
@CrossOrigin(origins = "${seckill.web.path}", allowedHeaders = "*", allowCredentials = "true")
public class OrderController {

    private Logger logger = LoggerFactory.getLogger(OrderController.class);

    private RateLimiter rateLimiter = RateLimiter.create(1000);

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private PromotionService promotionService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private ThreadPoolTaskExecutor taskExecutor;

    //秒杀前需获取验证码
    @GetMapping("/captcha")
    public void getCaptcha(String token, HttpServletResponse response) {
        //算术验证码 数字加减乘除，建议2位运算即可
        ArithmeticCaptcha arithmeticCaptcha = new ArithmeticCaptcha(130, 48, 2);
        if (token != null){
            User user = (User) redisTemplate.opsForValue().get(token);
            if (user != null){
                String key = "captcha:" + user.getId();
                redisTemplate.opsForValue().set(key,arithmeticCaptcha.text(),1, TimeUnit.MINUTES);
            }
        }
        response.setContentType("image/png");
        try {
            ServletOutputStream os = response.getOutputStream();
            arithmeticCaptcha.out(os);
        }catch (IOException e){
            logger.error("发送验证码失败"+e.getMessage());
        }
    }

    //秒杀前需获取token令牌
    @PostMapping("/token")
    public ResponseModel generateToken(int itemId, int promotionId, String token, String captcha) {
        User user = (User) redisTemplate.opsForValue().get(token);
        if (user == null){
            throw new BusinessException(ErrorCode.USER_NOT_LOGIN, "请重新登录！");
        }
        if (StringUtils.isEmpty(captcha)) {
            throw new BusinessException(ErrorCode.PARAMETER_ERROR, "请输入正确的验证码！1");
        }
        String key = "captcha:" + user.getId();
        String realCaptcha = (String) redisTemplate.opsForValue().get(key);
        if (!captcha.equalsIgnoreCase(realCaptcha)){
            throw new BusinessException(ErrorCode.PARAMETER_ERROR, "请输入正确的验证码！2");
        }
        String promotionToken = promotionService.generateToken(user.getId(), itemId, promotionId);
        if (StringUtils.isEmpty(promotionToken)) {
            throw new BusinessException(ErrorCode.CREATE_ORDER_FAILURE, "下单失败！");
        }
        return new ResponseModel(promotionToken);
    }

    //使用限流器限制单位时间内流通的下单请求

    //下单
    @PostMapping("/create")
    public ResponseModel create(/*HttpSession session, */
            int itemId, int amount, Integer promotionId, String promotionToken, String token) {

        if(!rateLimiter.tryAcquire(1,TimeUnit.SECONDS)){
            throw new BusinessException(ErrorCode.OUT_OF_LIMIT,"服务器繁忙，请稍后再试！");
        }

        User user = (User) redisTemplate.opsForValue().get(token);
        logger.debug("登录用户 [" + token + ": " + user + "]");

        //验证活动凭证，校验promotionToken是否正确有效
        if (promotionId != null) {
            String key = "promotion:token:" + user.getId() + ":" + itemId + ":" + promotionId;
            String realPromotionToken = (String) redisTemplate.opsForValue().get(key);
            if (StringUtils.isEmpty(promotionToken) || !promotionToken.equals(realPromotionToken)){
                throw new BusinessException(ErrorCode.CREATE_ORDER_FAILURE, "下单失败！");
            }
        }

        //使用线程池提交订单
        Future future = taskExecutor.submit(new Callable() {
            @Override
            public Object call() throws Exception {
//              orderService.createOrder(user.getId(), itemId, amount, promotionId);
                orderService.createOrderAsync(user.getId(), itemId, amount, promotionId);
                return null;
            }
        });

        //验证处理结果
        try {
            future.get();
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.UNDEFINED_ERROR, "下单失败！");
        }

        return new ResponseModel();
    }

    //订单列表
    @GetMapping("/list")
    public ResponseModel getOrderList(String token , String status) {
        List<Order> orders = null;
        if (token != null){
            User user = (User) redisTemplate.opsForValue().get(token);
            if (user != null){
                orders = orderService.getOrderList(user.getId(),status);
            }
        }
        return new ResponseModel(orders);
    }

    //订单状态更改
    @PostMapping("/status")
    public ResponseModel updateOrderStatus(String token , String orderId, Integer orderStatus) {
        if (token != null){
            User user = (User) redisTemplate.opsForValue().get(token);
            if (user != null){
                orderService.updateOrderStatus(user.getId(),orderId,orderStatus);
            }
        }
        return new ResponseModel();
    }

}
