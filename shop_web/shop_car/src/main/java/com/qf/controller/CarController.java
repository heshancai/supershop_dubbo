package com.qf.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.qf.ICartService;
import com.qf.aop.IsLogin;
import com.qf.aop.UserHolder;
import com.qf.entity.ShopCarEntity;
import com.qf.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
@RequestMapping("/cart")
public class CarController {

    //注入购物车微服务对象
    @Reference
    private  ICartService cartService;

    @Autowired
    private RedisTemplate redisTemplate;
    /**
     * 添加商品到购物车
     * 未登录时的状态
     * @param gid
     * @param gnumber
     * @param cartToken
     * @param response
     * @return
     */
    @RequestMapping("/insert")
    @IsLogin//自定义注解必须经过登录验证
    public String insert(Integer gid, Integer gnumber, @CookieValue(name = "cartToken",required = false)String cartToken, HttpServletResponse response){
        System.out.println("添加商品到购物车:"+gid+"===="+gnumber);
        User user = UserHolder.getUser();

        //商品服务添加购物车
        ShopCarEntity shopCarEntity=new ShopCarEntity().setGid(gid).setNumber(gnumber);
        cartToken=cartService.insertCar(shopCarEntity,user,cartToken);

        //购物车的token写入客户端中
        Cookie cookie=new Cookie("cartToken",cartToken);

        cookie.setMaxAge(60*60*24*365);
        cookie.setPath("/");
        response.addCookie(cookie);
        return "insertok";
    }

    /**
     * 展示购物车的信息头部
     * @param cartToken
     * @param callback
     * @return
     */
    @RequestMapping("/list")
    @ResponseBody
    @IsLogin
    public String list(@CookieValue(name = "cartToken",required = false)String cartToken,String callback){

        //用户是否登录
        User user = UserHolder.getUser();
        //查询所有的购物车的信息 登录和未登录的
        List<ShopCarEntity> carEntities=cartService.listCarts(cartToken,user);
        //购物车的信息返回到前台
        return callback!=null ? callback+"("+ JSON.toJSONString(carEntities) +")":JSON.toJSONString(carEntities);
    }


    /**
     * 跳转购物车的页面
     * @return
     */
    @RequestMapping("/showlist")
    @IsLogin
    public String showlist(@CookieValue(name = "cartToken",required = false)String cartToken, Model model){

        //查询购物车的信息
        User user = UserHolder.getUser();
        List<ShopCarEntity> carEntities=cartService.listCarts(cartToken,user);
        model.addAttribute("carts",carEntities);
        return "cartlist";

    }

    /**
     * 购物车的合并
     * 登录成功
     * 重定向携带回成功后跳转的页面url
     * @return
     */
    @RequestMapping("/merge")
    @IsLogin(mustLoign = true)
    public String cartMerge(String returnUrl,@CookieValue(name = "cartToken",required = false) String cartToken,HttpServletResponse response){

        //合并临时的购物车
        if(cartToken!=null){
            //查询redis上的购物车的信息
            //得到redis上购物车的长度
            Long size = redisTemplate.opsForList().size(cartToken);
            //获取所有的商品
            List<ShopCarEntity> shopCarEntities = redisTemplate.opsForList().range(cartToken, 0, size);

            //拿到当前登录的用户信息
            User user = UserHolder.getUser();
            //将临时的购物车的信息保存到数据库
            for(ShopCarEntity shopCar:shopCarEntities){
                //遍历插入
                cartService.insertCar(shopCar,user,cartToken);
            }

            //清空临时的购物车
            redisTemplate.delete(cartToken);
            //清空购物车的cookie
            Cookie cookie=new Cookie("cartToken",null);
            cookie.setMaxAge(0);
            cookie.setPath("/");
            response.addCookie(cookie);
        }

        //跳转回登录时的也页面
        return "redirect:"+returnUrl;
    }


}
