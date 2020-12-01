package com.qf.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.qf.IGoodsService;
import com.qf.entity.GoodsEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/item")
public class GoodsItemController {

    @Reference
    private IGoodsService goodsService;


    //根据商品id查询商品的详情信息
    @RequestMapping("/showById")
    public String showById(Integer id, Model model){
        GoodsEntity goodsEntity=goodsService.queryById(id);
        model.addAttribute("good",goodsEntity);
        return "goodsitem";

    }





}
