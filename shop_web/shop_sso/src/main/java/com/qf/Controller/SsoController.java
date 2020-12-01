package com.qf.Controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.qf.IUserService;
import com.qf.entity.ResultData;
import com.qf.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RequestMapping("/sso")
@Controller
public class SsoController {

    //登录页面的跳转
    @RequestMapping("/tologin")
    public String login(){
        return "login";
    }
    //注册页面的跳转
    @RequestMapping("/toregister")
    public String register(){
        return "register";
    }

    //注入用户的微服务对象
    @Reference
    private IUserService userService;

    //redis操作对象
    @Autowired
    private  RedisTemplate redisTemplate;
    //用户注册
    @RequestMapping("/register")
    public String register(User user, Model model){

        int register = userService.register(user);

        if(register>0){
            return "login";
        }else if(register==-1){
            model.addAttribute("msg","用户名已经存在");
        }else {
            model.addAttribute("msg","注册失败");
        }

        return "register";
    }

    //用户进行登录
    @RequestMapping("/login")
    public String login(String username, String password, String returnUrl, Model model, HttpServletResponse response){

        //根据用户名查询是否存在对象
        User user = userService.queryByUserName(username);
        //判断对象是否为空 并且密码进行比对
        if(user!=null && user.getPassword().equals(password)){
            //不为空，保存登录的状态到redis中
            System.out.println("登录成功:"+user);
            //uuid作为redis的唯一标识 作为token
            String token = UUID.randomUUID().toString();
            //将对象的存在redis中
            redisTemplate.opsForValue().set(token,user);
            //设置token有效时间 最作为判断的条件
            redisTemplate.expire(token,7, TimeUnit.DAYS);
            //将token 最为cookie的值
            Cookie cookie=new Cookie("loginToken",token);
            //设置cookie跨域请求时的设置
            //cookie存活时间
            cookie.setMaxAge(60*60*24*7);
            //设置可以访问该cookie的域名  sb.com  注意：这个属性不能设置成顶级域名，不允许
            cookie.setDomain("localhost");
            //设置可以访问该cookie的请求
            cookie.setPath("/");
            //设置cookie只有服务才能d读取该cookie,js页面的脚本代码,不允许该cookie
//            cookie.setHttpOnly(true);
            //设置该cookie只有https协议下,服务才能收到cookie
            /*cookie.setSecure(true);*/
            //添加cookie到客户端
            response.addCookie(cookie);

            //判断当前登录的页面携带的返回的url是否为空
            if(returnUrl==null || returnUrl.equals("")){
                //为空返回首页地址
                returnUrl = "http://localhost";
            }
            //对携带过来的returnUrl进行编码,进行双重重定向的操作
            //登录成功后的页面，跳转回去，进行双重定向的进行两次的解码
            try {
                URLEncoder.encode(returnUrl,"utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            //登录成功后,浏览器发送请求到购物车的合并，携带登录成功后的返回的url
            //双重重定向
            return "redirect:http://localhost:8084/cart/merge?returnUrl="+returnUrl;
        }
        //登录失败
        model.addAttribute("msg","用户名或密码错误");
        return "redirect:/sso/tologin";


    }


    /**
     * 判断是否登录
     * 解决ajax请求跨域问题
     * jsonp解决的方案
     * @return
     */
    @ResponseBody
    @RequestMapping("/isologin")
    public String isologin(@CookieValue(name = "loginToken",required = false)String loginToken,String callback){
        System.out.println("获取客户端的cookie"+loginToken);
        System.out.println(callback);
        //保存已经登录的信息
        ResultData<User> resultData=new ResultData<User>().setCode(ResultData.ResultCodeList.ERROR);
        //判断是否为空cookie
        if(loginToken!=null){
            //根据cookie值从redis中拿用户的信息
            User user=(User) redisTemplate.opsForValue().get(loginToken);
            if(user!=null){
                //登录的信息有效
                resultData.setCode(ResultData.ResultCodeList.OK);
                resultData.setData(user);
            }
        }
        System.out.println("返回的结果:"+resultData);
        //如果是jsonp的形式，需要返回js格式的方法结构
        //普通的ajax形式，直接返回json数据
        return callback !=null ? callback+"("+ JSON.toJSONString(resultData) +")":JSON.toJSONString(resultData);

    }

    /**
     * springmvc解决跨域的问题
     * @return
     */
    @CrossOrigin(allowCredentials = "true")
    @ResponseBody
    public ResultData<User>  isLogin2(@CookieValue(name = "loginToken",required = false)String loginToken){
        System.out.println("获取客户端的cookie"+loginToken);
        ResultData<User> resultData=new ResultData<User>().setCode(ResultData.ResultCodeList.ERROR);
        if(loginToken!=null){
            User user = (User) redisTemplate.opsForValue().get(loginToken);
           if(user!=null){
                resultData.setCode(ResultData.ResultCodeList.OK);
                resultData.setData(user);
           }
        }
        return resultData;
    }





    /**
     * 注销的请求
     * @return
     */
    @RequestMapping("/logout")
    public String logout(@CookieValue(name = "loginToken")String loginToken,HttpServletResponse response){
        //得到前台传过来的cookie
        System.out.println("注销请求:"+loginToken);
        //删除redis上的标识
        redisTemplate.delete(loginToken);
        //前台的cookie失效
        Cookie cookie=new Cookie("loginToken",null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        cookie.setDomain("localhost");
        response.addCookie(cookie);
        return "redirect:/sso/tologin";
    }



}
