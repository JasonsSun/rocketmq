package com.rocketmq.testmq;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;


//注意此处一定要添加，否则启动报错，因为我们没有配制数据源
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class )
public class TestmqApplication {

    public static void main(String[] args) {
        SpringApplication.run(TestmqApplication.class, args);
    }

}
