package com.qihang.secKill.dao;

import org.apache.ibatis.annotations.Mapper;
import com.qihang.secKill.domain.User;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * Created by wsbty on 2019/6/13.
 */
@Mapper
public interface Userdao {

    @Select("select * from user where id = #{id}")
    public User getById(@Param("id") long id);

    @Update("update user set password = #{password} where id = #{id}")
    public void update(User newUser);
}
