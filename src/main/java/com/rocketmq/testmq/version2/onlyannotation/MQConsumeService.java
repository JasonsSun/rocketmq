package com.rocketmq.testmq.version2.onlyannotation;

import com.rocketmq.testmq.version2.constants.TopicEnum;
import org.springframework.stereotype.Service;

import java.lang.annotation.*;

/**
 * @author 16591
 * @title: MQConsumeService
 * @projectName testmq
 * @description: TODO
 * @date 2019/12/11   21:50
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Service
public @interface MQConsumeService {
    /**
     * 消息主题
     */
    TopicEnum topic();

    /**
     * 消息标签,如果是该主题下所有的标签，使用“*”
     */
    String[] tags();


}
