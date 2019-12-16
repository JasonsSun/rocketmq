package com.rocketmq.testmq.version2.listener;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSON;
import com.alibaba.rocketmq.common.message.MessageExt;
import com.rocketmq.testmq.version2.MqResultvo.MQConsumeResult;
import com.rocketmq.testmq.version2.constants.RocketMQErrorEnum;
import com.rocketmq.testmq.version2.exception.AppException;
import com.rocketmq.testmq.version2.exception.RocketMQException;
import com.rocketmq.testmq.version2.intercontrol.MQMsgProcessor;
import com.rocketmq.testmq.version2.onlyannotation.MQConsumeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import com.alibaba.rocketmq.client.consumer.listener.MessageListenerConcurrently;

/**
 * @author 16591
 * @title: MQConsumeMsgListenerProcessor
 * @projectName testmq
 * @description: TODO
 * @date 2019/12/11   21:37
 */
@Component
public class MQCConsumeMsgListenerProcessor implements MessageListenerConcurrently{
    private static final Logger logger = LoggerFactory.getLogger(MQCConsumeMsgListenerProcessor.class);
    @Autowired
    private Map<String, MQMsgProcessor> mqMsgProcessorServiceMap;
    /**
     *  默认msgs里只有一条消息，可以通过设置consumeMessageBatchMaxSize参数来批量接收消息<br/>
     *  不要抛异常，如果没有return CONSUME_SUCCESS ，consumer会重新消费该消息，直到return CONSUME_SUCCESS
     */
    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {

                 // 重试次数

        if(CollectionUtils.isEmpty(msgs)){
            logger.info("接受到的消息为空，不处理，直接返回成功");
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        }
        ConsumeConcurrentlyStatus concurrentlyStatus = ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        try{
            //根据Topic分组
            Map<String, List<MessageExt>> topicGroups = msgs.stream().collect(Collectors.groupingBy(MessageExt::getTopic));
            for (Map.Entry<String, List<MessageExt>> topicEntry : topicGroups.entrySet()) {
                String topic = topicEntry.getKey();
                //根据tags分组
                Map<String, List<MessageExt>> tagGroups = topicEntry.getValue().stream().collect(Collectors.groupingBy(MessageExt::getTags));
                for (Map.Entry<String, List<MessageExt>> tagEntry : tagGroups.entrySet()) {
                    String tag = tagEntry.getKey();
                    //消费某个主题下，tag的消息
                    this.consumeMsgForTag(topic,tag,tagEntry.getValue());
                }
            }
        }catch(Exception e){
            logger.error("处理消息失败",e);
            if(e instanceof AppException){
                AppException mqe = (AppException)e;
                //TODO 记录消费失败日志
                if(mqe.getErrCode().getCode().equals("MQ_110"))
                {
                    System.out.println("错误信息:"+mqe.getErrMsg());
                    concurrentlyStatus = ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                }else
                {
                    concurrentlyStatus = ConsumeConcurrentlyStatus.RECONSUME_LATER;
                }


            }
            else
                concurrentlyStatus = ConsumeConcurrentlyStatus.RECONSUME_LATER;
        }

        // 如果没有return success ，consumer会重新消费该消息，直到return success
        return concurrentlyStatus;
    }
    /**
     * 根据topic 和 tags路由，查找消费消息服务
     * @param topic
     * @param tag
     * @param value
     * 2018年3月1日 zhaowg
     */
    private void consumeMsgForTag(String topic, String tag, List<MessageExt> value) {
        //根据topic 和  tag查询具体的消费服务
        MQMsgProcessor imqMsgProcessor = selectConsumeService(topic, tag);

        try{
            if(imqMsgProcessor==null){
                logger.error(String.format("根据Topic：%s和Tag:%s 没有找到对应的处理消息的服务",topic,tag));
                throw new RocketMQException(RocketMQErrorEnum.NOT_FOUND_CONSUMESERVICE);
            }
            logger.info(String.format("根据Topic：%s和Tag:%s 路由到的服务为:%s，开始调用处理消息",topic,tag,imqMsgProcessor.getClass().getName()));
            //调用该类的方法,处理消息
            MQConsumeResult mqConsumeResult = imqMsgProcessor.handle(topic,tag,value);
            if(mqConsumeResult==null){
                throw new RocketMQException(RocketMQErrorEnum.HANDLE_RESULT_NULL);
            }
            if(mqConsumeResult.getErrCode()!=null)
            {
                throw new RocketMQException(RocketMQErrorEnum.HANDLE_TIMES_OUT);
            }
            if(mqConsumeResult.isSuccess()){
                logger.info("消息处理成功："+ JSON.toJSONString(mqConsumeResult));
            }else{
                throw new RocketMQException(RocketMQErrorEnum.CONSUME_FAIL,JSON.toJSONString(mqConsumeResult),false);
            }
            if(mqConsumeResult.isSaveConsumeLog()){
                logger.debug("开始记录消费日志");
                //TODO 记录消费日志
            }
        }catch(Exception e){
            if(e instanceof AppException){
                AppException mqe = (AppException)e;
                //TODO 记录消费失败日志
                throw new AppException(mqe.getErrCode(),mqe.getErrMsg(),false);
            }else{
                //TODO 记录消费失败日志
                throw e;
            }
        }
    }
    /**
     * 根据topic和tag查询对应的具体的消费服务
     * @param topic
     * @param tag
     * @return
     * 2018年3月3日 zhaowg
     */
    private MQMsgProcessor selectConsumeService(String topic, String tag) {
        MQMsgProcessor imqMsgProcessor = null;
        for (Map.Entry<String, MQMsgProcessor> entry : mqMsgProcessorServiceMap.entrySet()) {
            //获取service实现类上注解的topic和tags
            MQConsumeService consumeService = entry.getValue().getClass().getAnnotation(MQConsumeService.class);
            if(consumeService == null){
                logger.error("消费者服务："+entry.getValue().getClass().getName()+"上没有添加MQConsumeService注解");
                continue;
            }
            String annotationTopic = consumeService.topic().getCode();
            if(!annotationTopic.equals(topic)){
                continue;
            }
            String[] tagsArr = consumeService.tags();
            //"*"号表示订阅该主题下所有的tag
            if(tagsArr[0].equals("*")){
                //获取该实例
                imqMsgProcessor = entry.getValue();
                break;
            }
            boolean isContains = Arrays.asList(tagsArr).contains(tag);
            if(isContains){
                //获取该实例
                imqMsgProcessor = entry.getValue();
                break;
            }
        }
        return imqMsgProcessor;
    }

}