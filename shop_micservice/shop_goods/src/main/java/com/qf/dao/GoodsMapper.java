package com.qf.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qf.entity.GoodsEntity;

import java.util.List;

/**
 * 商品数据库操作的公共接口
 */
public interface GoodsMapper  extends BaseMapper<GoodsEntity> {
    List<GoodsEntity> queryList();
    GoodsEntity queryGoodsById(Integer gid);

    Page<GoodsEntity> listPage(Page<GoodsEntity> page);
}
