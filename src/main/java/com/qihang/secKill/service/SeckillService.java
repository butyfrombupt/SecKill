package com.qihang.secKill.service;

import com.qihang.secKill.dao.GoodsDao;
import com.qihang.secKill.domain.Goods;
import com.qihang.secKill.domain.OrderInfo;
import com.qihang.secKill.domain.SeckillOrder;
import com.qihang.secKill.domain.User;
import com.qihang.secKill.redis.RedisService;
import com.qihang.secKill.redis.SeckillKey;
import com.qihang.secKill.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by wsbty on 2019/6/19.
 */
@Service
public class SeckillService {

    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;

    @Autowired
    RedisService redisService;

    @Transactional
    public OrderInfo seckill(User user, GoodsVo goods) {

        boolean success = goodsService.reduceStock(goods);

        if(success){
            return orderService.createOrder(user,goods);
        }
        else {//卖完了
            setGoodsOver(goods.getId());
            return null;
        }

    }

    public long getSeckillResult(Long userId, long goodsId) {
        SeckillOrder order = orderService.getOrderByUserIdAndGoodsId(userId, goodsId);
        if (order != null){
            return order.getOrderId();
        }else{
            boolean isOver = getGoodsOver(goodsId);
            if(isOver) {
                return -1;
            }else {
                return 0;
            }
        }
    }
    private void setGoodsOver(Long goodsId) {
        redisService.set(SeckillKey.isGoodsOver, ""+goodsId, true);
    }

    private boolean getGoodsOver(long goodsId) {
        return redisService.exists(SeckillKey.isGoodsOver, ""+goodsId);
    }
}
