package com.qf.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@TableName("goods")
public class GoodsEntity extends BaseEntity{

    //商品的类别
    private String subject;
    private String info;
    //价格
    private BigDecimal price;
    private Integer save;

    //封面的url
    //非数据库字段
    @TableField(exist = false)
    private String fmurl;

    //其他的文件的路径
    @TableField(exist = false)
    private List<String> otherurls;

}
