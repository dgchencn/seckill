package com.wyu.seckill.controller;

import com.wyu.seckill.common.BusinessException;
import com.wyu.seckill.common.ErrorCode;
import com.wyu.seckill.common.ResponseModel;
import com.wyu.seckill.common.Toolbox;
import com.wyu.seckill.entity.User;
import com.wyu.seckill.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/user")
@CrossOrigin(origins = "${seckill.web.path}", allowedHeaders = "*", allowCredentials = "true")
public class UserController {

    private Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private UserService userService;

    private String generateOTP() {
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 4; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

    @GetMapping("/otp/{phone}")
    public ResponseModel getOTP(@PathVariable("phone") String phone/*, HttpSession session*/) {
        // 生成OTP
        String otp = this.generateOTP();
        // 绑定OTP
//        session.setAttribute(phone, otp);
        redisTemplate.opsForValue().set(phone, otp, 5, TimeUnit.MINUTES);
        // 发送OTP
        logger.info("[秒杀网] 尊敬的{}您好, 您的注册验证码是{}, 请注意查收!", phone, otp);

        return new ResponseModel();
    }

    @PostMapping("/register")
    public ResponseModel register(String otp, User user /*, HttpSession session*/) {
        //验证短信验证码
        String realOTP = (String) redisTemplate.opsForValue().get(user.getPhone());
        if (StringUtils.isEmpty(otp)
                || StringUtils.isEmpty(realOTP)
                || !StringUtils.equals(otp, realOTP)){
            throw new BusinessException(ErrorCode.PARAMETER_ERROR,"验证码不正确！");
        }
        //加密用户密码
        user.setPassword(Toolbox.md5(user.getPassword()));
        //注册用户
        userService.register(user);
        return new ResponseModel();
    }

    @PostMapping("/login")
    public ResponseModel login(String phone, String password/*, HttpSession session*/) {
        if (StringUtils.isEmpty(phone)
                || StringUtils.isEmpty(password)) {
            throw new BusinessException(ErrorCode.PARAMETER_ERROR, "参数不合法！");
        }
        String md5pwd = Toolbox.md5(password);
        User user = userService.login(phone, md5pwd);
        //        session.setAttribute("loginUser", user);
        String token = UUID.randomUUID().toString().replace("-", "");
        redisTemplate.opsForValue().set(token, user, 1, TimeUnit.DAYS);
        return new ResponseModel(token);
    }

    @GetMapping("/logout")
    public ResponseModel logout(/*HttpSession session*/String token){
        //        session.invalidate();
        if (StringUtils.isNotEmpty(token)) {
            redisTemplate.delete(token);
        }
        return new ResponseModel();
    }


    @GetMapping("/status")
    public ResponseModel getUser(/*HttpSession session*/ String token) {
        //        User user = (User) session.getAttribute("loginUser");
        User user = null;
        if (StringUtils.isNoneEmpty(token)){
            user = (User) redisTemplate.opsForValue().get(token);
        }
        return new ResponseModel(user);
    }

    @PostMapping("/edit/address")
    public ResponseModel editAddress(/*HttpSession session*/ String token,String receiverName,String receiverPhone,String receiverAddress) {
        //        User user = (User) session.getAttribute("loginUser");
        User user = null;
        if (StringUtils.isNoneEmpty(token)){
            user = (User) redisTemplate.opsForValue().get(token);
        }
        user.setReceiverName(receiverName);
        user.setReceiverPhone(receiverPhone);
        user.setReceiverAddress(receiverAddress);
        userService.updateUser(user);
        redisTemplate.opsForValue().set(token, user, 1, TimeUnit.DAYS);
        return new ResponseModel(user);
    }

}
