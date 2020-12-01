package com.qf;

import com.qf.entity.GoodsEntity;

import java.util.List;

public interface ISearchService {

    //添加数据到索引库
    int insertSolr(GoodsEntity goodsEntity);
    //根据关键字查询索引库
    List<GoodsEntity> querySolr(String keyword);
}
