package com.qf;

import com.qf.entity.AddressEntity;
import com.qf.entity.User;

import java.util.List;

public interface IAddressService {

    //根据用户的id查询所有的收货地址
    List<AddressEntity> queryByUid(Integer uid);
    //根据收获地址的id查询一个收货信息
    AddressEntity queryByAid(Integer id);
    //根据用户的信息添加收货地址
    int insertAddress(AddressEntity addressEntity, User user);
}
