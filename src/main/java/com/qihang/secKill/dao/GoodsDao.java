package com.qihang.secKill.dao;


import com.qihang.secKill.domain.Goods;
import com.qihang.secKill.domain.SeckillGoods;
import com.qihang.secKill.vo.GoodsVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * Created by wsbty on 2019/6/18.
 */
@Mapper
public interface GoodsDao {
    @Select("select t2.*,t1.stock_count,t1.start_date,t1.end_date,t1.seckill_price from goods_seckill t1 left join goods t2 on t1.goods_id = t2.id ")
    public List<GoodsVo> getGoodsVoList();

    @Select("select g.*, sg.stock_count, sg.start_date, sg.end_date, sg.seckill_price, sg.version from goods_seckill sg left join goods g  on sg.goods_id = g.id where g.id = #{goodsId}")
    public GoodsVo getGoodsVoByGoodsId(@Param("goodsId") long goodsId);

    @Update("update goods_seckill set stock_count = stock_count - 1, version= version + 1 where goods_id = #{goodsId} and stock_count > 0 ")
    public int reduceStock(SeckillGoods g);
}
