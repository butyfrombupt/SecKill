package com.qihang.secKill.dao;

import com.qihang.secKill.domain.OrderInfo;
import com.qihang.secKill.domain.SeckillOrder;
import org.apache.ibatis.annotations.*;


/**
 * Created by wsbty on 2019/6/19.
 */
@Mapper
public interface OrderDao {

    @Select("select * from seckillorder where user_id = #{userId} and goods_id = #{goodsId}")
    public SeckillOrder getOrderByUserIdAndGoodsId(@Param("userId") long userId, @Param("goodsId") long goodsId);

    @Insert("insert into order_info(user_id, goods_id, goods_name, goods_count, goods_price, order_channel, status, create_date)values("
            + "#{userId}, #{goodsId}, #{goodsName}, #{goodsCount}, #{goodsPrice}, #{orderChannel},#{status},#{createDate} )")
    @SelectKey(keyColumn = "id", keyProperty = "id", resultType = long.class, before = false, statement = "select last_insert_id()")
    public long insert(OrderInfo orderInfo);


    @Insert("insert into seckillorder (user_id, goods_id, order_id)values(#{userId}, #{goodsId}, #{orderId})")
    public int insertSeckillOrder(SeckillOrder order);


    @Select("select * from order_info where id = #{orderId}")
    public OrderInfo getOrderById(@Param("orderId")long orderId);
}
