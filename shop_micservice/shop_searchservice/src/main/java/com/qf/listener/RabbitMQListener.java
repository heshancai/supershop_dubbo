package com.qf.listener;

import com.qf.ISearchService;
import com.qf.entity.GoodsEntity;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 配置交换机和队列
 */
@Component
public class RabbitMQListener {

    @Autowired
    //注入搜索业务处理类
    private ISearchService searchService;

    //消费者声明队列和交换机的绑定：
    @RabbitListener(bindings = @QueueBinding(
            //绑定交换机
            exchange = @Exchange(value = "goods_exchange",type = "fanout"),
            //交换机绑定队列
            value = @Queue(name = "search_queue")
    ))
    public void msgHandler(GoodsEntity goodsEntity){
        System.out.println("搜索服务接收到队列的消息:"+goodsEntity);
        //插入到索引库中
        searchService.insertSolr(goodsEntity);
    }


}
