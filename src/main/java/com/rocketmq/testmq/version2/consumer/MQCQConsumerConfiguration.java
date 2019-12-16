package com.rocketmq.testmq.version2.consumer;

import com.alibaba.rocketmq.client.consumer.DefaultMQPushConsumer;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.common.consumer.ConsumeFromWhere;
import com.rocketmq.testmq.version2.constants.RocketMQErrorEnum;
import com.rocketmq.testmq.version2.exception.RocketMQException;
import com.rocketmq.testmq.version2.listener.MQConsumeMsgListenerProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.util.StringUtils;

/**
 * 消费者Bean配置
 * .<br/>
 *
 * @author 16591
 * @title: MQConsumerConfiguration
 * @projectName testmq
 * @description: TODO
 * @date 2019/12/11   21:34
 */
@SpringBootConfiguration
@Configuration
@PropertySource("classpath:config/rocketmqversion2.properties")
public class MQCQConsumerConfiguration {
    public static final Logger LOGGER = LoggerFactory.getLogger(MQCQConsumerConfiguration.class);
    @Value("${rocketmqCQ.consumer.namesrvAddr}")
    private String namesrvAddrCQ;
    @Value("${rocketmqCQ.consumer.groupName}")
    private String groupNameCQ;
    @Value("${rocketmqCQ.consumer.consumeThreadMin}")
    private int consumeThreadMinCQ;
    @Value("${rocketmqCQ.consumer.consumeThreadMax}")
    private int consumeThreadMaxCQ;
    @Value("${rocketmqCQ.consumer.topics}")
    private String topicsCQ;
    @Value("${rocketmqCQ.consumer.consumeMessageBatchMaxSize}")
    private int consumeMessageBatchMaxSize;
    @Autowired
    private MQConsumeMsgListenerProcessor mqMessageListenerProcessor;

    @Autowired
    public DefaultMQPushConsumer getRocketMQConsumer() throws RocketMQException {
        if (StringUtils.isEmpty(groupNameCQ)) {
            throw new RocketMQException(RocketMQErrorEnum.PARAMM_NULL, "groupNameCQ is null !!!", false);
        }
        if (StringUtils.isEmpty(namesrvAddrCQ)) {
            throw new RocketMQException(RocketMQErrorEnum.PARAMM_NULL, "namesrvAddrCQ is null !!!", false);
        }
        if (StringUtils.isEmpty(topicsCQ)) {
            throw new RocketMQException(RocketMQErrorEnum.PARAMM_NULL, "topicsCQ is null !!!", false);
        }
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(groupNameCQ);
        consumer.setNamesrvAddr(namesrvAddrCQ);
        consumer.setConsumeThreadMin(consumeThreadMinCQ);
        consumer.setConsumeThreadMax(consumeThreadMaxCQ);
        consumer.registerMessageListener(mqMessageListenerProcessor);
        /**
         * 设置Consumer第一次启动是从队列头部开始消费还是队列尾部开始消费
         * 如果非第一次启动，那么按照上次消费的位置继续消费
         */
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);
        /**
         * 设置消费模型，集群还是广播，默认为集群
         */
        //consumer.setMessageModel(MessageModel.CLUSTERING);
        /**
         * 设置一次消费消息的条数，默认为1条
         */
        consumer.setConsumeMessageBatchMaxSize(consumeMessageBatchMaxSize);
        try {
            /**
             * 设置该消费者订阅的主题和tag，如果是订阅该主题下的所有tag，则tag使用*；如果需要指定订阅该主题下的某些tag，则使用||分割，例如tag1||tag2||tag3
             */
            String[] topicTagsArr = topicsCQ.split(";");
            for (String topicTags : topicTagsArr) {
                String[] topicTag = topicTags.split("~");
                consumer.subscribe(topicTag[0], topicTag[1]);
            }
            consumer.start();
            LOGGER.info("consumer is start !!! groupName:{},topics:{},namesrvAddr:{}", groupNameCQ, topicsCQ, namesrvAddrCQ);
        } catch (MQClientException e) {
            LOGGER.error("consumer is start !!! groupName:{},topics:{},namesrvAddr:{}", groupNameCQ, topicsCQ, namesrvAddrCQ, e);
            throw new RocketMQException(e);
        }
        return consumer;
    }
}