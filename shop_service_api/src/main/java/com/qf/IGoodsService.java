package com.qf;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qf.entity.GoodsEntity;

import java.util.List;

public interface IGoodsService {

    //查询所有的商品
    List<GoodsEntity> list();
    //添加商品
    int insert(GoodsEntity goodsEntity);

    //查询商品的详情信息
    GoodsEntity queryById(Integer id);

    Page<GoodsEntity> listPage(Page<GoodsEntity> page);

    //删除一个商品
    void delete(Integer gid);

}
