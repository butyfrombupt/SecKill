package com.qihang.secKill.service;

import com.qihang.secKill.dao.Userdao;
import com.qihang.secKill.domain.User;
import com.qihang.secKill.exception.GlobleException;
import com.qihang.secKill.redis.RedisService;
import com.qihang.secKill.redis.UserKey;
import com.qihang.secKill.result.CodeMsg;
import com.qihang.secKill.util.MD5Util;
import com.qihang.secKill.util.UUIDUtil;
import com.qihang.secKill.vo.LoginVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by wsbty on 2019/6/13.
 */
@Service
public class UserService {

    @Autowired
    Userdao userdao;

    @Autowired
    RedisService redisService;

    public static final String COOKIE_NAME_TOKEN = "token";

    public User getById(long id){
        //取缓存
        User user = redisService.get(UserKey.getById,""+id,User.class);
        if(user != null){
            return user;
        }
        user = userdao.getById(id);
        if(user != null){
            redisService.set(UserKey.getById,""+id,user);
        }
        return user;
    }

    public boolean updatePassword(String token,long id,String passNew){
        User user = userdao.getById(id);
        if(user != null){
            throw new GlobleException(CodeMsg.MOBILE_NOT_EXIST);
        }
        User newUser = new User();
        newUser.setId(id);
        newUser.setPassword(MD5Util.formPassToDbPass(passNew,user.getSalt()));
        userdao.update(newUser);

        redisService.delete(UserKey.getById,""+id);
        user.setPassword(newUser.getPassword());
        redisService.set(UserKey.token,token,user);

        return true;
    }

    public String login(HttpServletResponse response, LoginVo loginVo) {
        if (loginVo == null) {
            throw new GlobleException(CodeMsg.SERVER_ERROR);
        }
        String mobile = loginVo.getMobile();
        String formPass = loginVo.getPassword();
        //判断手机号是否存在
        User user = getById(Long.parseLong(mobile));
        if (user == null) {
            throw new GlobleException(CodeMsg.MOBILE_NOT_EXIST);
        }
        //验证密码
        String dbPass = user.getPassword();
        String saltDB = user.getSalt();
        String calcPass = MD5Util.formPassToDbPass(formPass, saltDB);
        if (!calcPass.equals(dbPass)) {
            throw new GlobleException(CodeMsg.PASSWORD_ERROR);
        }
        //生成唯一id作为token
        String token = UUIDUtil.uuid();
        addCookie(response, token, user);
        return token;
    }

    /**
     * 将token做为key，用户信息做为value 存入redis模拟session
     * 同时将token存入cookie，保存登录状态
     */
    public void addCookie(HttpServletResponse response, String token, User user) {
        redisService.set(UserKey.token, token, user);
        Cookie cookie = new Cookie(COOKIE_NAME_TOKEN, token);
        cookie.setMaxAge(UserKey.token.expireSeconds());
        cookie.setPath("/");//设置为网站根目录
        response.addCookie(cookie);
    }

    public User getByToken(HttpServletResponse response, String token) {
        if (StringUtils.isEmpty(token)) {
            return null;
        }
        User user = redisService.get(UserKey.token, token, User.class);
        //延长有效期，有效期等于最后一次操作+有效期
        if (user != null) {
            addCookie(response, token, user);
        }
        return user;
    }
}
