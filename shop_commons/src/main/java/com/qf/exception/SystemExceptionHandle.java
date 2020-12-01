package com.qf.exception;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import javax.xml.ws.RequestWrapper;

/**
 * 处理代码本身的异常
 */
@Controller
public class SystemExceptionHandle implements ErrorController {


    //获得状态码进行返回页面
    @RequestMapping("/error")
    public String systemExecption(HttpServletResponse response){

        //获取响应的状态码
        int status = response.getStatus();
        System.out.println(status);
        switch (status){
            case 401:
                return "401";
            case 402:
                return "402";
            case 403:
                return "403";
            case 404:
                return "404";

        }
        return "myerror";
    }




    @Override
    public String getErrorPath() {
        return "/error";
    }
}
