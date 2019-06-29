package com.qihang.secKill.controller;

import com.qihang.secKill.domain.OrderInfo;
import com.qihang.secKill.domain.SeckillOrder;
import com.qihang.secKill.domain.User;
import com.qihang.secKill.rabbitmq.MQSender;
import com.qihang.secKill.rabbitmq.SeckillMessage;
import com.qihang.secKill.redis.GoodsKey;
import com.qihang.secKill.redis.RedisService;
import com.qihang.secKill.redis.SeckillKey;
import com.qihang.secKill.result.CodeMsg;
import com.qihang.secKill.result.Result;
import com.qihang.secKill.service.GoodsService;
import com.qihang.secKill.service.OrderService;
import com.qihang.secKill.service.SeckillService;
import com.qihang.secKill.util.MD5Util;
import com.qihang.secKill.util.UUIDUtil;
import com.qihang.secKill.vo.GoodsVo;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by wsbty on 2019/6/19.
 */
@Controller
@RequestMapping("/seckill")
public class SeckillController implements InitializingBean{
    @Autowired
    GoodsService goodsService;

    @Autowired
    RedisService redisService;

    @Autowired
    OrderService orderService;

    @Autowired
    SeckillService seckillService;

    @Autowired
    MQSender sender;

    //做标记，判断该商品是否被处理过了
    private HashMap<Long, Boolean> localOverMap = new HashMap<Long, Boolean>();

    //系统初始化的时候调用
    @Override
    public void afterPropertiesSet() throws Exception {
        List<GoodsVo> goodsVoList = goodsService.listGoodsVo();
        if(goodsVoList == null){
            return;
        }
        for(GoodsVo goodsVo : goodsVoList){
            redisService.set(GoodsKey.getGoodsStock,""+goodsVo.getId(),goodsVo.getStockCount());
            localOverMap.put(goodsVo.getId(),false);
        }
    }

    @RequestMapping(value = "/do_seckill", method = RequestMethod.POST)
    @ResponseBody
    public Result<Integer> list(Model model, User user, @RequestParam("goodsId") long goodsId) {
        if (user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        model.addAttribute("user", user);
        //减少不必要的判断
        boolean over = localOverMap.get(goodsId);
        if(over){
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        long stock = redisService.decr(GoodsKey.getGoodsStock,""+goodsId);
        if(stock < 0){
            localOverMap.put(goodsId,true);
            return Result.error(CodeMsg.SECKILL_OVER);
        }
        //入队
        SeckillMessage mm = new SeckillMessage();
        mm.setUser(user);
        mm.setGoodsId(goodsId);
        sender.sendSeckillMessage(mm);
        return Result.success(0);
    }

    /**
     * orderId：成功
     * -1：秒杀失败
     * 0： 排队中
     */
    @RequestMapping(value = "/result", method = RequestMethod.GET)
    @ResponseBody
    public Result<Long> seckillResult(Model model, User user,
                                      @RequestParam("goodsId") long goodsId) {
        model.addAttribute("user", user);
        if (user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        long orderId = seckillService.getSeckillResult(user.getId(), goodsId);
        return Result.success(orderId);
    }

    @RequestMapping(value = "/getPath", method = RequestMethod.GET)
    @ResponseBody
    public Result<String> getPath(Model model, User user,
                                      @RequestParam("goodsId") long goodsId) {
        model.addAttribute("user", user);
        if (user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        String str = MD5Util.md5(UUIDUtil.uuid() + "123456");
        redisService.set(SeckillKey.getPath,""+user.getId()+"_"+goodsId,str);
        return Result.success(str);
    }

//    @RequestMapping("/do_seckill")
//    public String list(Model model, User user, @RequestParam("goodsId") long gooodsId){
//        model.addAttribute("user",user);
//        if(user == null){
//            return "login";
//        }
//        GoodsVo goods = goodsService.getGoodsVoByGoodsId(gooodsId);
//        int stock = goods.getStockCount();
//        if(stock <= 0){
//            model.addAttribute("errmsg", CodeMsg.SECKILL_OVER.getMsg());
//            return "seckill_fail";
//        }
//        SeckillOrder seckillorder = orderService.getOrderByUserIdAndGoodsId(user.getId(),gooodsId);
//        if(seckillorder != null){
//            model.addAttribute("errmsg",CodeMsg.REPEATE_SECKILL.getMsg());
//            return "seckill_fail";
//        }
//        //减去库存 下订单 写入秒杀订单
//        OrderInfo orderInfo = seckillService.seckill(user,goods);
//        model.addAttribute("orderInfo",orderInfo);
//        model.addAttribute("goods",goods);
//        return "order_detail";
//    }
    //静态页面化
//    @RequestMapping(value = "/do_seckill", method = RequestMethod.POST)
//    @ResponseBody
//    public Result<OrderInfo> list(Model model, User user, @RequestParam("goodsId") long goodsId) {
//        if (user == null) {
//            return Result.error(CodeMsg.SESSION_ERROR);
//        }
//        model.addAttribute("user", user);
//        //判断库存
//        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
//        int stock = goods.getStockCount();
//        if(stock <= 0){
//            return Result.error(CodeMsg.SECKILL_OVER);
//        }
//        //判断是否已经秒杀到了
//        SeckillOrder seckillorder = orderService.getOrderByUserIdAndGoodsId(user.getId(),goodsId);
//        if(seckillorder != null){
//            return Result.error(CodeMsg.REPEATE_SECKILL);
//        }
//        //减去库存 下订单 写入秒杀订单
//        OrderInfo orderInfo = seckillService.seckill(user,goods);
//        return Result.success(orderInfo);
//    }

}
