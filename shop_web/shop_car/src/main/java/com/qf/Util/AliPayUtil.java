package com.qf.Util;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;

public class AliPayUtil {
    //单例对象
    private static AlipayClient alipayClient;
    public   static  final String PRIVATEKEY="MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDQOvn/qRQkShviTnCwAI5/OVMp+Ntfb5udxSYQSOfx6enc3qEwzZD8DQIXMrPIZCXWISa0OVw2drQ8e2fCjH9TftQa7qwfBPj0hAw9wxcN6878OomK9frZA4uoxaF38sHlcYim4PSZIw1htxA9YQ/6XhG/2fqbXwezeZTnAxQ/exQM1bXo1YjHvelv94e31JfS5AiuiGsNNC2GELL74o+nakBjhpA0HqQzttrd/r3FQSWTUHkgDWPJ9WDHYIl+fDdlRdiCrPWXVB6QFB1O3lUDusi89wWtCf5BblyaS9/oTdKY90rOjyOwMPCenpMufpZkFcGVk77tDZFO2rQJ4izHAgMBAAECggEAbxJmdBcuZUUappaMyXUijX54e/UAn2GbMocNDLa28nWBh41BkhS0953AtbsX5xZ0kEnSqWPHak3bSj/RUxc6e8E1RsB1E37FhThCh1dfHs/f8u8Omw8mpUec4YO544+cGdV7vPhVwLHP24WYpgT2TuiKszmE0gfYxcVexfMAg/MecgePYqqedPURg5T7TvakuonAyfW9O/psPCMnV9g+rXAE9eohSUmzMILlUozqFQdNnRDZxKxnZiy/1WdIn/mp9eZ4QaSiWbgLIcOgQvQPD95Nv7m1BBn29yXVU8cHQl5mSdWNkLf31ugRJdOk+V475yaOlWftyR+fY6AbsRE00QKBgQDwrmSJFvA2h5+OYGZa10pOMxYPk3tZgyuKuaPDrrwN3kgpZ4CSb8osY9h6ZQoHeRncbV5RQws4lvYH4A4CfwBUT1KWi9UGYecDlDbV+KaaAV7/ScibBEjLdmnbPJdXJ8xtNuXUKbFu2/5kSLvCECOHcX2ZMESk7fKHpbYLdmH4rQKBgQDde9Y83tQLlf4OuGd8YHXBhgnZ+FX34ZdF8BfnLhqFyVGGlq3BDFo8L5wiL7zzGBOF/FFzu9zsTe49kqFSyja6eaS1kMDaJRuFJ7lZ+iKRE+yF31UbOEtcwsBWtGSlgbwMjW/GPmtu25vtQ7RPOqho96TX4todMkEZbOwDH+vlwwKBgQDBB/1V/fG2qluLw0EURmswAMUGxRA/IHbBklYH0ZK9xLM3ke2KhOIva/zpXTQH/laBda4Zmp3bSygT9N0Hn3hSJjRkzc2Sit7O/gZk4kIsbkEUmsarnWoM1PHdheETY7YPOeJmoPHPqd2D7Nm5sVvte1ZykEXdyYGkzYEN/GpjGQKBgQDVOx8z9TdifCJfG6wN83QLP7eGUW66ukaAP7lTiXBhc0N49uILZJAPN6rywtD/nSNWsGfeq3PzUMfYIa4M3b+44dnwL49rtaP8GxV4ibfzwqHI9Q2jqhvsD1dLYlsveGqKDzUYQAoAbY996tvSF2ABxsG+Upm42CQSjWnfTEE5LwKBgHJlbMc60sX+yJ8jfjC//zT487oYXyykzvchy9ZSZ/H+U6kBhbpmJHOkQUA2BFoFgSmmwNAEOrh0FQMOIDB1Iix73EfFPXE2jMWpkR1TZOreQ0mjJKDej9byiIFo0KLDvx715YJ3zWajGhfyjR2/7y9gCQkFSozW9YSPU3YTg2o9";
    public static final String ALIPAY_PUBLICK_KEY="MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAvo+46I4Cpo8GStTXI8kWbdJ/HQE8ynIA+mba4vnMEvHTgQraX0RaSS7d3GDPoxU3YRxFTkqrBWDybZr3HqPKN80OlqipXvodpxSZlCz7YYnEr9gExXLdTSLCEI6O1f7IBngOXTU9SZ2la3xDlEjDCAR4jeDzpeXIsRCjH3f7c9BPKYagIVt5e32zKir6fvX8y031I/jE0ddWEMpRl3tFQtRlOfT0hhEa4BcEJ6sfooJER0z5E55eJtohGD40q/+yfbkaCnmofD8jWh+6BwVFVQbFtUBW4y/HEm1q5tunnvpAAepEQsC9kN1qPJZWTTAy17oEuR6oxgjr+zRIF8aL/QIDAQAB";
    static {
        //为了帮助开发者调用开放接口，提供了开放平台服务端 SDK
        //在 SDK 调用前需要进行初始化
        alipayClient=new DefaultAlipayClient(
                //支付宝网关（固定） 沙箱环境
                "https://openapi.alipaydev.com/gateway.do",
                //APPID 即创建应用后生成 沙箱环境
                "2016101500691938", //APPID（我在支付宝的唯一标识）
                //自己生成开发者私钥
                PRIVATEKEY,
                //固定
                "json",
                //固定
                "UTF-8",
                //支付宝公钥，由支付宝生成
                "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAvo+46I4Cpo8GStTXI8kWbdJ/HQE8ynIA+mba4vnMEvHTgQraX0RaSS7d3GDPoxU3YRxFTkqrBWDybZr3HqPKN80OlqipXvodpxSZlCz7YYnEr9gExXLdTSLCEI6O1f7IBngOXTU9SZ2la3xDlEjDCAR4jeDzpeXIsRCjH3f7c9BPKYagIVt5e32zKir6fvX8y031I/jE0ddWEMpRl3tFQtRlOfT0hhEa4BcEJ6sfooJER0z5E55eJtohGD40q/+yfbkaCnmofD8jWh+6BwVFVQbFtUBW4y/HEm1q5tunnvpAAepEQsC9kN1qPJZWTTAy17oEuR6oxgjr+zRIF8aL/QIDAQAB",
                //商户生成签名字符串所使用的签名算法类型
                "RSA2"
        );
    }

    public static AlipayClient getAlipayClient(){
        return alipayClient;
    }
}
