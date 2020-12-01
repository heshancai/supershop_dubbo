package com.qf;

import com.qf.entity.GoodsEntity;
import com.qf.entity.ShopCarEntity;
import com.qf.entity.User;

import java.util.List;

public interface ICartService {
    //添加商品到购物车
    String insertCar(ShopCarEntity shopCarEntity, User user, String cartToken);

    //查询所有的购物车的信息
    List<ShopCarEntity> listCarts(String cartToken, User user);

    //根据商品的id，查询购物车的信息
    List<ShopCarEntity> queryCarByGid(Integer[] gid, User user);
    //根据购物车的id查询购物的信息

    List<ShopCarEntity> queryCarByCid(Integer[] cids);

    //根据购物车的删除购物车的信息
    int deletCarByCid(Integer[] cids);
}
