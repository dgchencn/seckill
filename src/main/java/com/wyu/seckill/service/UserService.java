package com.wyu.seckill.service;

import com.wyu.seckill.entity.User;
import org.springframework.stereotype.Service;

public interface UserService {
    void register(User user);

    User login(String phone, String md5pwd);

    User findUserById(int id);

    User findUserFromCache(Integer userId);

    void updateUser(User user);
}
