package com.basic.spring.rpc.service;


import com.basic.spring.rpc.pojo.User;

import java.util.List;

public interface IUserService {
    User queryById(Integer id);
    List<User> getAllUsers();
    Boolean insertUser(User user);
}
