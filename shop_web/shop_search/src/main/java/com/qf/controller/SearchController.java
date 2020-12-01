package com.qf.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.qf.ISearchService;
import com.qf.entity.GoodsEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * 搜索前端控制
 */
@RequestMapping("search")
@Controller
public class SearchController {

    //注入searchService
    @Reference
    private ISearchService searchService;


    //关键进行搜索
    @RequestMapping("/searchByKeyword")
    public String searchByKeyword(String keyword, Model model){
        System.out.println("搜索关键字为:"+keyword);
        List<GoodsEntity> goods =searchService.querySolr(keyword);
        model.addAttribute("goods",goods);
        return "searchlist";

    }


}
