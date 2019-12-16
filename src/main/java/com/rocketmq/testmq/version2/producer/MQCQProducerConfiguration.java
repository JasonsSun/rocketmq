package com.rocketmq.testmq.version2.producer;

import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.client.producer.DefaultMQProducer;
import com.rocketmq.testmq.version2.constants.RocketMQErrorEnum;
import com.rocketmq.testmq.version2.exception.RocketMQException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.util.StringUtils;

/**
 * 生产者配置
 * .<br/>
 *
 * @author 16591
 * @title: MQProducerConfiguration
 * @projectName testmq
 * @description: TODO
 * @date 2019/12/11   21:31
 */
@SpringBootConfiguration
public class MQCQProducerConfiguration {
    public static final Logger LOGGER = LoggerFactory.getLogger(MQCQProducerConfiguration.class);
    /**
     * 发送同一类消息的设置为同一个group，保证唯一,默认不需要设置，rocketmq会使用ip@pid(pid代表jvm名字)作为唯一标示
     */
    @Value("${rocketmq.producer.groupName}CQ")
    private String groupNameCQ;
    @Value("${rocketmqCQ.producer.namesrvAddr}")
    private String namesrvAddrCQ;
    /**
     * 消息最大大小，默认4M
     */
    @Value("${rocketmqCQ.producer.maxMessageSize}")
    private Integer maxMessageSizeCQ ;
    /**
     * 消息发送超时时间，默认3秒
     */
    @Value("${rocketmqCQ.producer.sendMsgTimeout}")
    private Integer sendMsgTimeoutCQ;
    /**
     * 消息发送失败重试次数，默认2次
     */
    @Value("${rocketmqCQ.producer.retryTimesWhenSendFailed}")
    private Integer retryTimesWhenSendFailedCQ;

    @Autowired
    public DefaultMQProducer getRocketMQProducer() throws RocketMQException {
        if (StringUtils.isEmpty(this.groupNameCQ)) {
            throw new RocketMQException(RocketMQErrorEnum.PARAMM_NULL,"groupNameCQ is blank",false);
        }
        if (StringUtils.isEmpty(this.namesrvAddrCQ)) {
            throw new RocketMQException(RocketMQErrorEnum.PARAMM_NULL,"nameServerAddrCQ is blank",false);
        }
        DefaultMQProducer producer;
        producer = new DefaultMQProducer(this.groupNameCQ);
        producer.setNamesrvAddr(this.namesrvAddrCQ);
        //如果需要同一个jvm中不同的producer往不同的mq集群发送消息，需要设置不同的instanceName
        //producer.setInstanceName(instanceName);
        if(this.maxMessageSizeCQ!=null){
            producer.setMaxMessageSize(this.maxMessageSizeCQ);
        }
        if(this.sendMsgTimeoutCQ!=null){
            producer.setSendMsgTimeout(this.sendMsgTimeoutCQ);
        }
        //如果发送消息失败，设置重试次数，默认为2次
        if(this.retryTimesWhenSendFailedCQ!=null){
            producer.setRetryTimesWhenSendFailed(this.retryTimesWhenSendFailedCQ);
        }

        try {
            producer.start();

            LOGGER.info(String.format("producerCQ is start ! groupName:[%s],namesrvAddr:[%s]"
                    , this.groupNameCQ, this.namesrvAddrCQ));
        } catch (MQClientException e) {
            LOGGER.error(String.format("producerCQ is error {}"
                    , e.getMessage(),e));
            throw new RocketMQException(e);
        }
        return producer;
    }
}