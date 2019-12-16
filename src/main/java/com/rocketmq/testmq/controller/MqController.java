package com.rocketmq.testmq.controller;



import com.rocketmq.testmq.version2.ProducerMegSend.CQProdu;
import com.rocketmq.testmq.version2.ProducerMegSend.Produ;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.UUID;

@Controller
@RequestMapping(value = "/mq")
public class MqController {


//    @Resource
//    private Student student;
//
//    @Autowired
//    private ProducerServiceImpl producer;


//    @RequestMapping(value = "/push",method = RequestMethod.GET)
//    @ResponseBody
//    @ApiOperation(value = "测试接口", notes = "自定义请求头sessionId，sessionId的值是登陆接口返回的")
//    public String test(){
//
//        producer.send("TopicTest1", "push", "测试消息");
//
//        return "Hello Springbootxxxx1111222333444!!!"+student.getName();
//    }


    @Resource
    private Produ produ;


    @Resource
    private CQProdu cqProdu;



    @RequestMapping(value = "/push2", method = RequestMethod.GET)
    @ResponseBody
    public String test2() {
        return produ.sendMsg();
    }
    @RequestMapping(value = "/push3", method = RequestMethod.GET)
    @ResponseBody
    public String test3() {
        return cqProdu.sendCQMsg();
    }

}