package com.wyu.seckill.service.impl;

import com.wyu.seckill.common.BusinessException;
import com.wyu.seckill.common.ErrorCode;
import com.wyu.seckill.common.ObjectValidator;
import com.wyu.seckill.dao.UserMapper;
import com.wyu.seckill.entity.User;
import com.wyu.seckill.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl implements UserService , ErrorCode {

    private Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ObjectValidator validator;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    @Transactional
    public void register(User user) {
        if (user == null){
            throw new BusinessException(PARAMETER_ERROR, "参数不能为空！");
        }
        Map<String, String> result = validator.validate(user);
        if (result != null && result.size() > 0) {
            throw new BusinessException(PARAMETER_ERROR,
                    StringUtils.join(result.values().toArray(), ", ") + "！");
        }
        try {
            userMapper.insert(user);
        } catch (DuplicateKeyException e) {
            throw new BusinessException(PARAMETER_ERROR, "该手机号已注册！");
        }
    }

    @Override
    public User login(String phone, String md5pwd) {
        if (StringUtils.isEmpty(phone) || StringUtils.isEmpty(md5pwd)) {
            throw new BusinessException(PARAMETER_ERROR, "参数不合法！");
        }
        User user = userMapper.selectByPhone(phone);
        if (user == null || !StringUtils.equals(user.getPassword(),md5pwd)){
            throw new BusinessException(USER_LOGIN_FAILURE,"账号或密码错误！");
        }
        return user;
    }

    @Override
    public User findUserById(int id) {
        if (id <= 0) {
            return null;
        }

        return userMapper.selectByPrimaryKey(id);
    }

    @Override
    public User findUserFromCache(Integer userId) {
        if (userId <= 0) {
            return null;
        }
        // redis
        String key = "user:" + userId;
        User user = (User) redisTemplate.opsForValue().get(key);
        if (user != null) {
            logger.debug("缓存命中 [" + user + "]");
            return user;
        }
        //mysql
        user = findUserById(userId);
        if (user != null){
            logger.debug("同步缓存 [" + user + "]");
            redisTemplate.opsForValue().set(key,user,30, TimeUnit.MINUTES);
            return user;
        }
        return null;
    }

    @Override
    public void updateUser(User user) {
        if (user == null){
            throw new BusinessException(PARAMETER_ERROR, "参数不能为空！");
        }
        Map<String, String> result = validator.validate(user);
        if (result != null && result.size() > 0) {
            throw new BusinessException(PARAMETER_ERROR,
                    StringUtils.join(result.values().toArray(), ", ") + "！");
        }
        try {
            userMapper.updateByPrimaryKey(user);
        } catch (DuplicateKeyException e) {
            throw new BusinessException(PARAMETER_ERROR, "修改失败！");
        }
    }
}
