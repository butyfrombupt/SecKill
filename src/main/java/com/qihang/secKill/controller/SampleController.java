package com.qihang.secKill.controller;

import com.qihang.secKill.rabbitmq.MQSender;
import com.qihang.secKill.redis.RedisService;
import com.qihang.secKill.redis.UserKey;
import com.qihang.secKill.result.CodeMsg;
import com.qihang.secKill.result.Result;
import com.qihang.secKill.service.UserService;
import com.qihang.secKill.domain.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by wsbty on 2019/6/13.
 */
@Controller
@RequestMapping("/demo")
public class SampleController {

    @Autowired
    UserService userService;

    @Autowired
    RedisService redisService;

    @Autowired
    MQSender mqSender;

    @RequestMapping("/mq")
    @ResponseBody
    public Result<String> mq(){
        mqSender.send("haha");
        return Result.success("heihei");
    }

    @RequestMapping("/thymeleaf")
    @ResponseBody
    public String thymeleaf(Model model){
        model.addAttribute("name","buty");
        return "hello";
    }
    @RequestMapping("/error")
    @ResponseBody
    public Result<String> error(Model model){
        return Result.error(CodeMsg.SESSION_ERROR);
    }

    @RequestMapping("/db")
    @ResponseBody
    public Result<User> dbGet(Model model){
        User user = userService.getById(1);
        return Result.success(user);
    }

    @RequestMapping("/redis/get")
    @ResponseBody
    public Result<User> redisGet(Model model){
        User user = redisService.get(UserKey.getById,""+1,User.class);
        return Result.success(user);
    }

    @RequestMapping("/redis/set")
    @ResponseBody
    public Result<Boolean> redisSet(Model model){
        User user = new User();
        user.setId((long)521);
        user.setNickname("zhou");
        boolean res = redisService.set(UserKey.getById,""+1,user);
        return Result.success(res);
    }
}
