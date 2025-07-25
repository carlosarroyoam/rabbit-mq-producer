package com.carlosarroyoam.service.rabbitmq.messages;

import com.carlosarroyoam.service.rabbitmq.config.RabbitMqConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class MessageService {
  private final RabbitTemplate rabbitTemplate;

  public MessageService(RabbitTemplate rabbitTemplate) {
    this.rabbitTemplate = rabbitTemplate;
  }

  public void send(Message message) {
    rabbitTemplate.convertAndSend(RabbitMqConfig.EXCHANGE_NAME, RabbitMqConfig.ROUTING_KEY,
        message);
  }
}
