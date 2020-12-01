package com.qf.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class BaseEntity implements Serializable {

    //主键自增
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Date createTime=new Date();
    //初始值状态
    private Integer status=0;
}
