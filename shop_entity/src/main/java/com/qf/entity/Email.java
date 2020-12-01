package com.qf.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class Email implements Serializable {

    //收件邮箱的地址
    private String to;
    //邮箱的标题
    private String subject;
    //邮箱的内容
    private String context;
    //邮箱的发送的时间
    private Date sendTime;

}
