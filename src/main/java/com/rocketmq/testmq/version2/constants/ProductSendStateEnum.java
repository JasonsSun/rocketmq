package com.rocketmq.testmq.version2.constants;

/**
 * @author 16591
 * @title: ProductSendStateEnum
 * @projectName testmq
 * @description: TODO
 * @date 2019/12/11   22:11
 */
public enum ProductSendStateEnum {
    /**消息发送状态：-201：定时任务发送失败（人工处理），-200：生产者发送失败（定时任务处理），100：待发送，200：生产者发送成功，201：定时任务发送成功*/

    JOB_SEND_FAIL(-201),
    PRODUCT_SEND_FAIL(-200),
    WAIT_SEND(100),
    PRODUCT_SEND_SUCCESS(200),
    JOB_SEND_SUCCESS(201),
    ;
    private int code;

    private ProductSendStateEnum(int code) {
        this.code = code;
    }

    public int getCode() {
        return this.code;
    }
}