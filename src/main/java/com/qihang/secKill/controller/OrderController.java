package com.qihang.secKill.controller;

import com.qihang.secKill.domain.OrderInfo;
import com.qihang.secKill.domain.User;
import com.qihang.secKill.redis.RedisService;
import com.qihang.secKill.result.CodeMsg;
import com.qihang.secKill.result.Result;
import com.qihang.secKill.service.GoodsService;
import com.qihang.secKill.service.OrderService;
import com.qihang.secKill.service.UserService;
import com.qihang.secKill.vo.GoodsVo;
import com.qihang.secKill.vo.OrderDetailVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by wsbty on 2019/6/21.
 */
@Controller
@RequestMapping("/order")
public class OrderController {
    @Autowired
    UserService userService;

    @Autowired
    RedisService redisService;

    @Autowired
    OrderService orderService;

    @Autowired
    GoodsService goodsService;

    @RequestMapping("/detail")
    @ResponseBody
    public Result<OrderDetailVo> info(Model model, User user,
                                      @RequestParam("orderId") long orderId) {
        if(user == null){
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        OrderInfo order = orderService.getOrderById(orderId);
        if(order ==null){
            return Result.error(CodeMsg.ORDER_NOT_EXIST);
        }
        long goodsId = order.getGoodsId();
        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        OrderDetailVo orderDetailVo = new OrderDetailVo();
        orderDetailVo.setOrder(order);
        orderDetailVo.setGoods(goods);
        return Result.success(orderDetailVo);
    }
}
