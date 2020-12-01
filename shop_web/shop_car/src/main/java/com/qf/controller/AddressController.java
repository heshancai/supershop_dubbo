package com.qf.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.qf.IAddressService;
import com.qf.aop.IsLogin;
import com.qf.aop.UserHolder;
import com.qf.entity.AddressEntity;
import com.qf.entity.ResultData;
import com.qf.entity.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@RequestMapping("/address")
@Controller
public class AddressController {

    @Reference
    IAddressService addressService;

    /**
     * 添加收货地址
     * @param addressEntity
     * @return
     */
    @RequestMapping("/insert")
    @IsLogin(mustLoign = true)
    @ResponseBody
    public ResultData<String> insert(AddressEntity addressEntity){
        User user = UserHolder.getUser();
        addressService.insertAddress(addressEntity,user);
        return new ResultData<String>().setCode(ResultData.ResultCodeList.OK).setMsg("添加成功！");
    }
}
