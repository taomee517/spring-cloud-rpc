package com.basic.spring.rpc.service;


import com.basic.rpc.annotations.MyMethod;
import com.basic.rpc.annotations.MyService;
import com.basic.spring.rpc.pojo.User;

import java.util.List;

@MyService("demo-service")
public interface IUserService {

    @MyMethod("queryById")
    User queryById(Integer id);

    @MyMethod("getAllUsers")
    List<User> getAllUsers();

    @MyMethod("insertUser")
    Boolean insertUser(User user);
}
