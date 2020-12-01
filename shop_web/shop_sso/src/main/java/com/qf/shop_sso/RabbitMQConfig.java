package com.qf.shop_sso;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 交换机和队列配置类
 */
@Configuration
public class RabbitMQConfig {

    //注入交换机 返回的对象注入ioc容器
    @Bean
    public FanoutExchange fanoutExchange(){
        return new FanoutExchange("mail_exchange");
    }

    //队列进行注入
    @Bean
    public Queue getQueue(){
        return new Queue("mail_queue");
    }

    //交换机和队列进行绑定
    //创建的交换机和队列作为参数进行绑定
    @Bean
    public Binding getBinding(Queue getQueue,FanoutExchange fanoutExchange){
        return BindingBuilder.bind(getQueue).to(fanoutExchange);
    }

}
