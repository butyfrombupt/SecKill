package com.qihang.secKill.vo;

import com.qihang.secKill.domain.Goods;

import java.util.Date;

/**
 * Created by wsbty on 2019/6/18.
 */
public class GoodsVo extends Goods {
    private Double seckillPrice;
    private int stockCount;
    private Date startDate;
    private Date endDate;
    private Integer version;

    public Double getSeckillPrice() {
        return seckillPrice;
    }

    public void setSeckillPrice(Double seckillPrice) {
        this.seckillPrice = seckillPrice;
    }

    public int getStockCount() {
        return stockCount;
    }

    public void setStockCount(int stockCount) {
        this.stockCount = stockCount;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }
}
