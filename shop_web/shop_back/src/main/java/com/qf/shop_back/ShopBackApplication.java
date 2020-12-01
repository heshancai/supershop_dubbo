package com.qf.shop_back;

import com.github.tobato.fastdfs.FdfsClientConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

//可进行跨项目的扫描 其他的项目的包名必须一致
@SpringBootApplication(scanBasePackages = "com.qf")
//导入类进行如spring管理
@Import(FdfsClientConfig.class)
public class ShopBackApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShopBackApplication.class, args);
    }

}
