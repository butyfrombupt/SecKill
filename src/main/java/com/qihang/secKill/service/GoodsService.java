package com.qihang.secKill.service;

import com.qihang.secKill.dao.GoodsDao;
import com.qihang.secKill.domain.Goods;
import com.qihang.secKill.domain.SeckillGoods;
import com.qihang.secKill.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by wsbty on 2019/6/18.
 */
@Service
public class GoodsService {

    @Autowired
    GoodsDao goodsDao;

    public List<GoodsVo> listGoodsVo(){
        return goodsDao.getGoodsVoList();
    }

    public GoodsVo getGoodsVoByGoodsId(long goodsId) {
        return goodsDao.getGoodsVoByGoodsId(goodsId);
    }

    public boolean reduceStock(GoodsVo goods) {
        SeckillGoods good = new SeckillGoods();
        good.setGoodsId(goods.getId());
        int ret = goodsDao.reduceStock(good);
        return ret > 0;
    }
}
