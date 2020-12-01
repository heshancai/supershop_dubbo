package com.qf.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.qf.IAddressService;
import com.qf.ICartService;
import com.qf.IOrdersService;
import com.qf.aop.IsLogin;
import com.qf.aop.UserHolder;
import com.qf.entity.*;
import com.qf.util.ShopUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Arrays;
import java.util.List;

@RequestMapping("/orders")
@Controller
public class OrdersController {


    @Reference
    private ICartService cartService;
    @Reference
    private IAddressService addressService;
    @Reference
    private IOrdersService ordersService;
    /**
     * 添加商品到购物车
     * 跳转到订单编辑页
     * 必须登录
     * @param gid
     * @return
     */
    @RequestMapping("/toOrdersEdit")
    @IsLogin(mustLoign = true)
    public String toOrderEdit(Integer[] gid, Model model){
        System.out.println("需要下单的商品:"+ Arrays.toString(gid));
        //得到登录的用户的消息
        User user = UserHolder.getUser();

        //得到购选的商品的购物车的信息
        List<ShopCarEntity> shopCarEntities=cartService.queryCarByGid(gid,user);
        //得到用户的地址的信息
        List<AddressEntity> addressEntities = addressService.queryByUid(user.getId());
        //计算总价
        double allprice = ShopUtil.allPrice(shopCarEntities);
        //返回到订单的页面
        model.addAttribute("carts",shopCarEntities);
        model.addAttribute("addresses",addressEntities);
        model.addAttribute("allprice",allprice);
        return "orderedit";
    }


    /**
     * 进行下单得操作
     * @return
     */
    @RequestMapping("/insert")
    @ResponseBody
    @IsLogin(mustLoign = true)
    public ResultData<OrdersEntity> insertOrders(Integer aid,Integer[] cids){

        System.out.println("收货地址id:"+aid);
        System.out.println("购物清单得id:"+Arrays.toString(cids));

        //下单操作
        OrdersEntity ordersEntity = ordersService.insertOrders(aid, cids, UserHolder.getUser());

        //携带订单的信息到前台，给支付操作使用
        return new ResultData<OrdersEntity>().setCode(ResultData.ResultCodeList.OK).setData(ordersEntity);
    }

    @RequestMapping("/list")
    @IsLogin(mustLoign = true)
    public String orderList(Model model){
        User user = UserHolder.getUser();
        List<OrdersEntity> ordersList = ordersService.queryByUid(user.getId());
        model.addAttribute("ordersList",ordersList);
        return "orderslist";
    }



}
