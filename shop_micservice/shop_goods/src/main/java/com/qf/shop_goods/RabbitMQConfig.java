package com.qf.shop_goods;

import org.springframework.amqp.core.FanoutExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//声明配置类
//提供者声明交换机
@Configuration
public class RabbitMQConfig {

    @Bean
    //将方法的返回值对象交给springioc容器管理
    public FanoutExchange getFanoutExchange(){
        return new FanoutExchange("goods_exchange");
    }

}
