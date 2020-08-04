package com.imooc.miaosha.rabbitmq;

import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class MQReceiver {
    public static Logger log = LoggerFactory.getLogger(MQReceiver.class);

    @RabbitListener(queues=MQConfig.QUEUE)
    public void receive(String msg){
        log.info("receive message:"+msg);
    }
}
