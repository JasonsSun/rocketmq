package com.rocketmq.testmq.version2.constants;

/**
 * @author 16591
 * @title: TopicEnum
 * @projectName testmq
 * @description: TODO
 * @date 2019/12/11   22:14
 */
public enum TopicEnum {
    DemoTopic("DemoTopic","示例主题"),
    CQTopic("CQTopic","重庆专用"),
    ;

    private String code;
    private String msg;

    private TopicEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return this.code;
    }

    public String getMsg() {
        return this.msg;
    }

}