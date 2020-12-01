package com.qf.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;


@Data
@Accessors(chain = true)
@TableName("address")
public class AddressEntity extends BaseEntity{
    private Integer uid;
    private String person;
    private String address;
    private String phone;
    private String code;
    //是否为默认的地址 1 是 0否
    private Integer isdefault=0;


}
