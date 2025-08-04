package com.carlosarroyoam.service.rabbitmq.messages;

import com.carlosarroyoam.service.rabbitmq.core.config.RabbitMqProducerConfig;
import com.carlosarroyoam.service.rabbitmq.messages.event.MessageCreatedEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class MessageService {
  private final RabbitTemplate rabbitTemplate;

  public MessageService(RabbitTemplate rabbitTemplate) {
    this.rabbitTemplate = rabbitTemplate;
  }

  public void send(MessageCreatedEvent message) {
    rabbitTemplate.convertAndSend(RabbitMqProducerConfig.MESSAGES_EXCHANGE_NAME,
        RabbitMqProducerConfig.MESSAGES_CREATED_ROUTING_KEY, message);
  }
}
