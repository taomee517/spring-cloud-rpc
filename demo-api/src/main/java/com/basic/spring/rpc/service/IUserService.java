package com.basic.spring.rpc.service;



import com.basic.spring.rpc.pojo.User;
import feign.Headers;
import feign.Param;
import feign.RequestLine;

import java.util.List;

public interface IUserService {

    @RequestLine("GET /user/{id}")
    User queryById(@Param("id") Integer id);

    @RequestLine("GET /user/all")
    List<User> getAllUsers();

    @Headers({"Content-Type: application/json","Accept: application/json"})
    @RequestLine("POST /user/insert")
    Boolean insertUser(User user);
}
