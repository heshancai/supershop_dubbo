package com.qf.serviceImpl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qf.IGoodsService;
import com.qf.ISearchService;
import com.qf.dao.GoodsImagesMapper;
import com.qf.dao.GoodsMapper;
import com.qf.entity.GoodsEntity;
import com.qf.entity.GoodsImageEntity;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;


import java.util.ArrayList;
import java.util.List;

@Service
public class GoodsService implements IGoodsService {

    //注入持久层的操作
    @Autowired
    private GoodsMapper goodsMapper;

    @Autowired
    private GoodsImagesMapper goodsImagesMapper;

    //注入RabbitTemplate操作消息队列
    @Autowired
    private RabbitTemplate rabbitTemplate;
    //注入searchService服务

    @Reference
    private ISearchService searchService;

    @Override
    //需要被事务管理
    @Transactional(readOnly = true)
    public List<GoodsEntity> list() {
        List<GoodsEntity> goodsEntityList=goodsMapper.queryList();
        return goodsEntityList;
    }

    @Override
    @Transactional
    public int insert(GoodsEntity goodsEntity) {

        //保存商品的基本的信息
        goodsMapper.insert(goodsEntity);

        //构建图片的封面的信息
        GoodsImageEntity goodsImageEntity=new GoodsImageEntity()
                .setGid(goodsEntity.getId())
                .setIsfengmian(1)
                .setUrl(goodsEntity.getFmurl());

        //保存数据
        goodsImagesMapper.insert(goodsImageEntity);

        //保存封面的信息
        for (String otherurls:goodsEntity.getOtherurls()){
            GoodsImageEntity gi=new GoodsImageEntity()
                    .setGid(goodsEntity.getId())
                    .setIsfengmian(0)
                    .setUrl(otherurls);
            goodsImagesMapper.insert(gi);
        }

//        //同步索引库的信息
//        searchService.insertSolr(goodsEntity);
        //将添加索引库的消息发送到mq中  交换机
        //提者发布信息 自动将对象转为二进制文件
        rabbitTemplate.convertAndSend("goods_exchange","",goodsEntity);

      return 1;
    }

    //查询商品的详情信息
    @Override
    public GoodsEntity queryById(Integer id) {
        return goodsMapper.queryGoodsById(id);
    }


    //查询参与分页
    @Override
    public Page<GoodsEntity> listPage(Page<GoodsEntity> page) {
        return goodsMapper.listPage(page);
    }

    @Override
    public void delete(Integer gid) {
        goodsMapper.deleteById(gid);
        QueryWrapper queryWrapper=new QueryWrapper();
        queryWrapper.eq("gid",gid);
        List<GoodsImageEntity> list = goodsImagesMapper.selectList(queryWrapper);
        List<Integer> ids=new ArrayList<>(list.size());
        for (int i=0;i<list.size();i++){
            ids.add(list.get(i).getId());
        }
        goodsImagesMapper.deleteBatchIds(ids);
    }
}
