package com.basic.demo.service.controller;

import com.basic.spring.rpc.pojo.User;
import com.basic.spring.rpc.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping(value = "user", produces = "application/json")
public class UserController implements IUserService{

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    private DiscoveryClient client;


    @Override
    @ResponseBody
    @GetMapping(value = "/{id}")
    public User queryById(@PathVariable(value = "id") Integer id) {
        ServiceInstance instance = client.getInstances("demo-service").get(0);
        String sql = "select * from tb_user where id = ?";
        User user = (User)jdbcTemplate.queryForObject(sql,new Object[]{id},new BeanPropertyRowMapper(User.class));
        return user;
    }

    @Override
    @ResponseBody
    @GetMapping(value = "/all")
    public List<User> getAllUsers() {
        String sql = "select * from tb_user";
        List<User> users = jdbcTemplate.query(sql,new BeanPropertyRowMapper(User.class));
        return users;
    }

    @Override
    @ResponseBody
    @PostMapping(value = "/insert")
    public Boolean insertUser(@RequestBody  User user) {
        String sql = "insert into tb_user values(null, ?, ?)";
        int result = jdbcTemplate.update(sql, user.getName(), user.getAge());
        return result > 0;
    }
}
