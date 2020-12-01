package com.qf.Utils;

import com.qf.entity.ShopCarEntity;

import java.math.BigDecimal;
import java.util.List;

/**
 * 工具类
 */
public class ShopUtil {
    /**
     * 根据购物车的清单
     * 计算购物车的总计
     * @return
     */
    public static double allPrice(List<ShopCarEntity> shopCarEntities){
        //将long值转换为BigDecimal
        BigDecimal allPrices=BigDecimal.valueOf(0);
        if(shopCarEntities!=null){
            //进行小计的累加
            for (ShopCarEntity shopCarEntity:shopCarEntities){
                allPrices=allPrices.add(shopCarEntity.getCartPrice());
            }
        }
        //转为doble类型
        return  allPrices.doubleValue();
    }
}
