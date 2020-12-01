package com.qf.Controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.qf.IUserService;
import com.qf.entity.Email;
import com.qf.entity.ResultData;
import com.qf.entity.User;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Controller
@RequestMapping("/forget")
public class ForgetController {

    @Reference
    private IUserService userService;

    //消息队列操作的模板
    @Autowired
    private RabbitTemplate rabbitTemplate;

    //缓存服务器操作对象
    @Autowired
    private StringRedisTemplate redisTemplate;
    //跳转到忘记密码页面
    @RequestMapping("/toForgetPassword")
    public String toForgetPassword(){
        return "forgetpassword";
    }


    //后台服务发送邮箱连接进行修改密码
    @RequestMapping("/sendmail")
    @ResponseBody
    public ResultData<Map<String,String>> sendmail(String username){

        //根据用户名查询用户信息
        User user = userService.queryByUserName(username);
        if(user==null){
            //用户不存在
            return new ResultData<Map<String, String>>().setCode(ResultData.ResultCodeList.ERROR)
                    .setMsg("用户名不存在");
        }

        //生成uuid作为key值
        String uuid = UUID.randomUUID().toString();
        //将键值对保存到redis缓存服务器中
        redisTemplate.opsForValue().set(uuid,username);
        //对保存的键设置有效期 五分钟
        redisTemplate.expire(uuid,3, TimeUnit.MINUTES);

        //设置找回密码的链接
        String url="http://localhost:8083/forget/toUpdatePassword?token="+uuid;

        //构建邮箱对象发送邮箱信息
        Email email=new Email()
                //收件地址
                .setTo(user.getEmail())
                .setSubject("找回密码，非本人操作请忽略")
                .setContext("点击<a href='"+url+"'>这里</a>,找回密码")
                .setSendTime(new Date());
        //将构建好的邮箱信息发送到mq队列中
        rabbitTemplate.convertAndSend("mail_exchange","",email);

        //构建反馈信息到前端
        String email1 = user.getEmail();
        //截取邮箱的信息进行替换
        String subemail1 = email1.substring(3, email1.lastIndexOf("@"));
        String showemail=email1.replace(subemail1,"******");

        //构建点击链接地址
        String tomail="mail."+email1.substring(email1.lastIndexOf("@")+1);

        //将构建好的信息放到集合
        Map<String,String> map=new HashMap<>();
        map.put("showmail",showemail);
        map.put("tomail",tomail);

        return new ResultData<Map<String, String>>()
                .setCode(ResultData.ResultCodeList.OK)
                .setMsg("邮件发送成功")
                .setData(map);

    }

    //跳转修改密码页面
    @RequestMapping("/toUpdatePassword")
    public String toUpdatePassword(){
        return "toUpdatePassword";
    }

    //修改密码
    @RequestMapping("/updatepassword")
    public String updatepassword(String password,String token){

        //检验token的有效性
        String username = redisTemplate.opsForValue().get(token);
        if(username==null){
            //token过期或者无效
            return "updateerror";
        }

        //修改密码操作
        userService.updatePassword(username,password);
        //修改成功，删除token
        redisTemplate.delete(token);
        return "login";

    }
}
