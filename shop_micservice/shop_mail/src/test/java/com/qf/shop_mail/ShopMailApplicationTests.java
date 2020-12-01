package com.qf.shop_mail;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Date;

@SpringBootTest
class ShopMailApplicationTests {
    @Autowired
    private  JavaMailSender javaMailSender;

    @Test
    void contextLoads() throws MessagingException {
        //创建一件邮箱
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        //使用spring提供的便捷操作对象
        MimeMessageHelper mimeMessageHelper=new MimeMessageHelper(mimeMessage,true);
        mimeMessageHelper.setFrom("18648899602@sina.cn");
        mimeMessageHelper.setTo("1528919831@qq.com");
        mimeMessageHelper.setTo("2840137176@qq.com");
        mimeMessageHelper.setSubject("会员到期通知");
        mimeMessageHelper.setText("赶紧充钱!<img src='https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1575973255147&di=0706e7c23e0954a59cc64921fb5574f1&imgtype=0&src=http%3A%2F%2Fimgsrc.baidu.com%2Fforum%2Fpic%2Fitem%2F8cb1cb1349540923ebfc3dee9258d109b2de498c.jpg'/>",true);
        mimeMessageHelper.setSentDate(new Date());
        //发送邮件
        javaMailSender.send(mimeMessage);
        System.out.println("邮件发送成功");

    }

}
