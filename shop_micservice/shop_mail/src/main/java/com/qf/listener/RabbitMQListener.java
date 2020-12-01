package com.qf.listener;

import com.qf.entity.Email;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

/**
 * 消息队列接收已经
 * 邮箱的发送
 */
@Component
public class RabbitMQListener {

    //注入邮箱操作对象
    @Autowired
    private  JavaMailSender javaMailSender;

    //从配置文件中读取发送方的邮箱地址
    @Value("${spring.mail.username}")
    private String from;

    //接收队列的消息 收件方的消息
    //获取该队列的消息
    @RabbitListener(queuesToDeclare = @Queue(name = "mail_queue"))
    public void msgHandle(Email email) throws MessagingException {

        System.out.println(email);
        //创建一封邮件
        MimeMessage mimeMessage=javaMailSender.createMimeMessage();

        //使用spirng提供的便捷邮件设置对象
        MimeMessageHelper helper=new MimeMessageHelper(mimeMessage,true);

        //设置邮件的内容
        //标题
        helper.setSubject(email.getSubject());

        //发送方邮箱地址
        helper.setFrom(from);
        //接收方邮箱地址
        helper.setTo(email.getTo());
        //抄送
        //密送

        //设置内容 html形式解析
        helper.setText(email.getContext(),true);

        //发送附件
        helper.setSentDate(email.getSendTime());

        //邮件发送
        javaMailSender.send(mimeMessage);
        System.out.println("邮件发送成功");
    }


}
