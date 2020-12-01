package com.qf.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 商品图片实体类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@TableName("goods_images")
public class GoodsImageEntity extends BaseEntity{
    //商品id
    private Integer gid;
    private String info;
    //图片地址
    private String url;
    //是否为封面
    private Integer isfengmian;


}
