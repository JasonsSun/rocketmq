package com.rocketmq.testmq.version2.constants;

import java.io.Serializable;

/**
 * @author 16591
 * @title: ErrorCode
 * @projectName testmq
 * @description: TODO
 * @date 2019/12/11   22:09
 */
public interface ErrorCode extends Serializable {
    /**
     * 错误码
     * @return
     */
    String getCode();
    /**
     * 错误信息
     * @return
     */
    String getMsg();
}