package com.qf.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.domain.SignParams;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.qf.IOrdersService;
import com.qf.Util.AliPayUtil;
import com.qf.entity.OrdersEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/pay")
public class PayController {

    @Reference
    private  IOrdersService ordersService;

    //支付订单
    @RequestMapping("/alipay")
    public void alipay(String orderid, HttpServletResponse response) throws IOException {

        OrdersEntity ordersEntity = ordersService.queryByOid(orderid);

        //创建一个支付宝的客户端对象(支付、退款、查询、关闭交易....)
        AlipayClient alipayClient = AliPayUtil.getAlipayClient();
        //创建一个支付页面的申请对象
        AlipayTradePagePayRequest alipayRequest=new AlipayTradePagePayRequest();
        //设置支付完成后的同步请求
        alipayRequest.setReturnUrl("http://localhost:8084/orders/list");

        //设置支付完成后的异步请求 --决定支付是否成功
        //在公共参数中设置回跳和通知地址
        // 交易成功后，支付宝服务器通过 post 请求 notifyUrl（商户入参传入），返回异步通知参数。 使用natapp穿透
        alipayRequest.setNotifyUrl("http://9mt3mx.natappfree.cc/pay/callBack");
        //填充业务参数
        alipayRequest.setBizContent("{" +
                        //支付的订单号
                "    \"out_trade_no\":\"" + ordersEntity.getOrderid() + "\"," +
                "    \"product_code\":\"FAST_INSTANT_TRADE_PAY\"," +
                                    //订单支付的价格
                "    \"total_amount\":" + ordersEntity.getAllprice().doubleValue() + "," +
                                //订单详情信息
                "    \"subject\":\"" + ordersEntity.getOrderDetils().get(0).getSubject() + "..\"," +
                "    \"extend_params\":{" +
                "    \"sys_service_provider_id\":\"2088511833207846\"" +
                "    }"+
                "  }");

        //发送请求给支付宝，得到支付宝的返回一个页面
        String form="";
        try {
            form=alipayClient.pageExecute(alipayRequest).getBody();//调用sdk生成表单
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }

        //直接将支付页面发送给用户的浏览器
        response.setContentType("text/html;charset=utf-8");
        //将完成的表单输出到html页面
        response.getWriter().write(form);
        response.getWriter().flush();
        response.getWriter().close();
    }

    /**
     * 支付宝得回调接口
     * 支付完成后得异步请求
     * 决定支付是否成功
     * 进行验签得操作
     *
     * @return
     */
    @RequestMapping("/callBack")
    @ResponseBody
    public String payCallBack(String out_trade_no, String trade_status, HttpServletRequest request) throws AlipayApiException {
        //out_trade_no 原支付请求的商户订单号
        //trade_status交易目前所处的状态

        //创建一个map集合装转换过来得参数
        Map<String,String> signMap=new HashMap<>();
        //请求中拿到支付宝带过来得所有得参数 进行验证得操作
        // 将异步通知中收到的所有参数都存放到map中
        Map<String, String[]> parameterMap = request.getParameterMap();
        //取数组中第一个参数放到新的集合中
        for(Map.Entry<String,String[]> entry:parameterMap.entrySet()){
            signMap.put(entry.getKey(),entry.getValue()[0]);
        }

        //进行验签得操作                  自己生成开发者私
        boolean signVerified = AlipaySignature.rsaCheckV1(signMap, AliPayUtil.ALIPAY_PUBLICK_KEY, signMap.get("charset"), signMap.get("sign_type"));
        if(signVerified){

            //按照支付结果异步通知中的描述，对支付结果中的业务内容进行二次校验，
            // 校验成功后在response中返回success并继续商户自身业务处理，校验失败返回failure
            //验证out_trade_no是否存在
            //判断total_amount和对应的订单总金额是否一致
            //验证seller_id是否匹配
            //验证app_id是否为当前商户的appid

            //状态得验证 TRADE_SUCCESS交易支付成功  TRADE_FINISHED交易结束，不可退款
            if(trade_status.equals("TRADE_SUCCESS")||trade_status.equals("TRADE_FINISHED")){

                //验证成功。修改订单的状态 根据订单号 修改状态为1
                ordersService.updateOrderStatus(out_trade_no,1);
                //返回状态给支付宝
                return "success";
            }
        }else {
            //验签失败，返回failure  打印在日志中
            System.out.println("验签失败");
        }
        return "failure";
    }



}
