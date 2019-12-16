# rocketmq
使用springBoot集成RocketMQ消息队列 （使用顺序消费模式）涉及到Redis数据去重

Windows下操作教程

1.RocketMQ配置与调用
#启动namesrv
start mqnamesrv.cmd
#启动broker
start mqbroker.cmd -n 127.0.0.1:9876 autoCreateTopicEnable=true

#检查broker是否注册到namesrv
mqadmin.cmd clusterList -n localhost:9876




可视化环境配置（java环境 mvn环境具备的前提）

1.下载
国外：
https://github.com/apache/rocketmq-externals.git
国内：
百度链接：https://pan.baidu.com/s/1sMO6W-562IFJF1uUBQFXYg  
提取码：fuzy 


		1.1 系统环境变量配置
		变量名：ROCKETMQ_HOME
		变量值：MQ解压路径\MQ文件夹名
		ROCKETMQ_HOME=D:\dev\rocketmq-all-4.3.0-bin-release  // 可重命名
		

2.配置

进入 rocketmq-externals\rocketmq-console\src\main\resources\application.properties

内容：
server.port=8080 //修改启动端口

rocketmq.config.namesrvAddr=127.0.0.1:9876  //127.0.0.1:9876 添加服务器启动地址

3.编译
进入 rocketmq-externals\rocketmq-console 文件夹，执行 mvn clean package -Dmaven.test.skip=true

4.启动
编译成功之后，进入 target 文件夹，执行 java -jar rocketmq-console-ng-1.0.1.jar


5.访问路径

启动成功之后，在浏览器输入地址 http://127.0.0.1:8080 进行访问  //对应修改的端口地址





需要手动关闭

mqshutdown namesrv


mqshutdown broker


2.Redis配置和启动关闭
登录 
edis-cli -h 127.0.0.1 -p 6379

redis-cli -h 127.0.0.1 -p 6379 -a myPassword

启动redis
redis-server redis.windows.conf

关闭redis

redis-cli shutdown

redis-cli -a 123456 shutdown


