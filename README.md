## 类库功能简介

将网络请求进行了封装，开发者不用自己请求网络和配置请求相关参数。只需要简单设置请求参数就可以发起HTTP/HTTPS GET/POST请求。

并自动实现了并发请求。开发者主要精力放到了请求成功后的数据解析。


## 项目运行时效果图
DEMO 爬取的是代理,既提供了例子,又让你有免费的代理用.一举两得哈哈,爬取线程数开小一点,会被屏蔽的
![image](https://raw.githubusercontent.com/MrJiao/SpiderJackson/master/images/idea%E6%88%AA%E5%9B%BE.png)

![image](https://raw.githubusercontent.com/MrJiao/SpiderJackson/master/images/mysql1.png)


![image](https://raw.githubusercontent.com/MrJiao/SpiderJackson/master/images/proxy.png)


## 引入框架

1. 数据库框架 mybatis 3.3.0（默认使用数据库 mysql）
2. 日志工具 log4j2 2.7
3. http协议请求 HttpClient 4.5.2
4. html解析 jsoup 1.9.2

## 功能描述

1. 支持http、https
2. 支持get、post请求
3. 支持自定义间隔时间请求，时间间隔不会造成任务线程阻塞
4. 支持爬取优先级设置
5. 支持断点续爬
6. 支持定时爬取同一url
7. 支持多线程并发请求，线程可配
8. 支持cookie自动管理
9. 支持请求失败自动更换proxy发起请求

## 其他：

1. 内置代理管理模块(代理自动验证有效性，代理按某规则优先取出)
2. 日志输入完整，方便查看问题
3. 内置user-agent管理模块(提供常用user-agent 500多个)
4. 简单api就可实现复杂的请求业务
5. 模块之间独立，可以自己组装自己的controller

## 接下来的开发任务

1. 代理添加高匿名、国内国外、等参数
2. 添加账号管理模块、封装模拟登陆
3. 添加爬取流程监听
4. 添加浏览器引擎动态渲染js
5. url表RequestConfig字段和IPaser字段添加关联表减少存储空间
6. 优化代理取出策略，保证最好用的代理有先被取出

## 使用说明
### 第一步
安装mysql数据库


### 第二部
这是一个IDEA JAVA项目，导入成功后，修改SpiderJackson/src/config/SqlMapConfig.xml里面的配置，配置好mybatis即可。

    <dataSource type="POOLED">
        <property name="driver" value="com.mysql.jdbc.Driver" />
        <property name="url" value="jdbc:mysql://localhost:3306/your_db_name" />
        <property name="username" value="your_username" />
        <property name="password" value="your_password" />
    </dataSource>

##### 修改 your_db_name 为你的数据库名字 
##### 修改 your_username 为你的数据库账号 
##### 修改 your_password 为你的数据库密码

### 第三步
直接运行代码,获取免费代理.
demo包里有使用例子和说明.
## 类简单介绍

1. Url：请求封装类，内部设置请求的url地址，get/post请求，HTTP/HTTPS请求，请求参数、解析对象
2. UrlService: 用来操作数据库url表的对象
3. ProxyService:用来操作数据库proxy表的对象
4. SimpleRequestTask：请求解析任务对象
5. ProxyController：使用proxy的管理类，内部实现验证proxy有效性，并将验证有效的存入内存
6. SimpleRequestControl：请求解析的管理类，内部提供多线程爬取 设置的url
7. TimerRequestControl: 定时请求任务管理类
8. UserAgentControl: 获取user-agent的管理类

## 使用例子
更多例子可以查看demo包下的代码


```
/**
     * Created by Jackson 
     * 请求例子，介绍使用SimpleRequestControl 请求并解析的例子
     */
    public class RequestDemo {
    
        public static void main(String[] args){
            //创建url表
            UrlService url_db_name = new UrlService("url_db_name");
            //创建请求对象
            Url url = Url.newHttpGetUrl("http://www.baidu.com");
            //将请求对象添加到表里
            url_db_name.insert(url);
            //创建请求管理对象
            SimpleRequestControl simpleRequestControl = SimpleRequestControl.newInstance(url_db_name);
            //设置请求线程数
            simpleRequestControl.setThreadSize(10);
            //设置任务间隔时间
            simpleRequestControl.setDelay(new SimpleRequestControl.DelayHandle() {
                @Override
                public long getDelay(Url url) {
                    // Random 不固定定时也可以
                    return 3000;
                }
            });
            //设置请求参数， 这里的设置相对于url的RequestConfig 会被回调，通常用作全局性的header设置等
            simpleRequestControl.setHttpGetConfigHandler(new HttpGetPool.HttpGetConfigHandler() {
                @Override
                public HttpGet setConfig(HttpGet httpGet, Url url) {
                    httpGet.setHeader("user-agent","BaiduSpider");
                    return httpGet;
                }
            });
            //开启任务，在开启任务前设置好参数
            simpleRequestControl.start();
        }
    }
```
