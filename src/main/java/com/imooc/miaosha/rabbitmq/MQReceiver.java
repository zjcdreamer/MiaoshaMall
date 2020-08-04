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

    @RabbitListener(queues=MQConfig.TOPIC_QUEUE1)
    public void topicReceive1(String msg){
        log.info("TOPIC_QUEUE1 message:"+msg);
    }

    @RabbitListener(queues=MQConfig.TOPIC_QUEUE2)
    public void topicReceive2(String msg){
        log.info("TOPIC_QUEUE2 message:"+msg);
    }

    @RabbitListener(queues=MQConfig.TOPIC_QUEUE1)
    public void fanoutReceive1(String msg){
        log.info("Fanout_QUEUE1 message:"+msg);
    }

    @RabbitListener(queues=MQConfig.TOPIC_QUEUE2)
    public void fanoutReceive2(String msg){
        log.info("Fanout_QUEUE2 message:"+msg);
    }

    @RabbitListener(queues=MQConfig.HEADERS_QUEUE)
    public void headersReceive(String msg){
        log.info("HEADERS_QUEUE message:"+msg);
    }
}
