package com.qf.exception;

import com.qf.entity.ResultData;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

/**
 * 处理非代码的异常
 * 处理同步请求和ajax异步请求
 */
@ControllerAdvice
public class exceptionControllerAdvice {

    //全局异常处理  value属性需要要拦截的类型
    //Exception 拦截所有异常
    @ExceptionHandler(Exception.class)
    //如果是返回到的是json对象,将对象转为json字符串
    //放到响应体中,响应回给浏览器
    //如果是普通字符串,将字符串到响应体中，响应回json字符串
    @ResponseBody
    public Object exceptionHandler(HttpServletRequest request,Exception e){
        System.out.println("项目出现异常:"+e.getMessage());
        //获取请求的头部信息进行判断
        String header = request.getHeader("X-Requested-With");
        if(header!=null && header.equals("XMLHttpRequest")){
            //说明是ajax请求
            //返回错误的信息和和错误信息的响应码
            return new ResultData<String>().setCode(ResultData.ResultCodeList.ERROR).setMsg("服务器繁忙，别乱点。。。。");
        }else {
            //说明是一个同步到请求
            //同步请求返回错误的页面 ResponseBody将不起作用
            return  new ModelAndView("myerror");
        }
    }




}
