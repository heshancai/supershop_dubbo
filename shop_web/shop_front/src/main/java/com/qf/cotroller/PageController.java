package com.qf.cotroller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("toPage")
@Controller
public class PageController {

    @RequestMapping("/{page}")
    public String toPage(@PathVariable  String page){
        return page;
    }
}
