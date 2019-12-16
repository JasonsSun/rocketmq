package com.rocketmq.testmq.version2.ProducerMegSend;

import com.alibaba.rocketmq.client.exception.MQBrokerException;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.client.producer.DefaultMQProducer;
import com.alibaba.rocketmq.client.producer.MessageQueueSelector;
import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.common.message.Message;
import com.alibaba.rocketmq.common.message.MessageQueue;
import com.rocketmq.testmq.controller.MqController;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author 16591
 * @title: Produ
 * @projectName testmq
 * @description: TODO
 * @date 2019/12/11   21:39
 */
//@RunWith(SpringRunner.class)
//@SpringBootTest
@Controller
public class Produ {

    private static final Logger logger = LoggerFactory.getLogger(Produ.class);

    /**
     * 使用RocketMq的生产者
     */
    @Autowired
    private DefaultMQProducer defaultMQProducer;

    /**
     * 发送消息
     * <p>
     * 2018年3月3日 zhaowg
     *
     * @throws InterruptedException
     * @throws MQBrokerException
     * @throws RemotingException
     * @throws MQClientException
     */
    @Test
    public void send() throws MQClientException, RemotingException, MQBrokerException, InterruptedException {
        String msg = "demo msg test";
        logger.info("开始发送消息：" + msg);
        Message sendMsg = new Message("DemoTopic", "DemoTag", msg.getBytes());
        //默认3秒超时
        SendResult sendResult = null;
        try {
            sendResult = defaultMQProducer.send(sendMsg);
        } catch (com.alibaba.rocketmq.remoting.exception.RemotingException e) {
            e.printStackTrace();
        }
        logger.info("消息发送响应信息：" + sendResult.toString());

    }

    public String sendMsg() {

        long times=System.currentTimeMillis();
        SendResult result = null;
        try {
            for(int i=0;i<10;i++) {
                Message sendMsg = new Message("DemoTopic", "Demo",( "HelloDemo"+i).getBytes(RemotingHelper.DEFAULT_CHARSET));
                sendMsg.setKeys(UUID.randomUUID().toString());
                result = defaultMQProducer.send(sendMsg, new MessageQueueSelector() {
                    @Override
                    public MessageQueue select(List<MessageQueue> list, Message message, Object o) {

                        long demoId=Long.parseLong(o.toString());
                        long index=demoId % list.size();
                        return list.get((int)index);
                    }
                },1);
                TimeUnit.MILLISECONDS.sleep(1);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.info("消息发送响应信息：" + result.toString());
        return result.toString();
    }
}
