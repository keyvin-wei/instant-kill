package com.keyvin.instantkill.vo;

import com.keyvin.instantkill.domain.Goods;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author weiwh
 * @date 2019/8/14 11:14
 */
public class GoodsVo extends Goods {
    private BigDecimal buyoutPrice;
    private Date startDate;
    private Date endDate;

    public BigDecimal getBuyoutPrice() {
        return buyoutPrice;
    }

    public void setBuyoutPrice(BigDecimal buyoutPrice) {
        this.buyoutPrice = buyoutPrice;
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
}
