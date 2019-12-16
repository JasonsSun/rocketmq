package com.rocketmq.testmq.version2.constants;

/**
 * @author 16591
 * @title: RocketMQErrorEnum
 * @projectName testmq
 * @description: TODO
 * @date 2019/12/11   22:10
 */
public enum RocketMQErrorEnum implements ErrorCode{
    /********公共********/
    PARAMM_NULL("MQ_001","参数为空"),

    /********生产者*******/



    /********消费者*******/
    NOT_FOUND_CONSUMESERVICE("MQ_100","根据topic和tag没有找到对应的消费服务"),
    HANDLE_RESULT_NULL("MQ_101","消费方法返回值为空"),
    HANDLE_TIMES_OUT("MQ_110","消息处理超时"),
    CONSUME_FAIL("MQ_102","消费失败")



    ;

    private String code;
    private String msg;

    private RocketMQErrorEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }
    @Override
    public String getCode() {
        return this.code;
    }

    @Override
    public String getMsg() {
        return this.msg;
    }


}