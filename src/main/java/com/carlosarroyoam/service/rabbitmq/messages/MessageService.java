package com.carlosarroyoam.service.rabbitmq.messages;

import com.carlosarroyoam.service.rabbitmq.core.config.RabbitMqProps;
import com.carlosarroyoam.service.rabbitmq.messages.event.MessageCreatedEvent;
import java.util.UUID;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class MessageService {
  private final RabbitTemplate rabbitTemplate;
  private final RabbitMqProps rabbitMqProps;

  public MessageService(RabbitTemplate rabbitTemplate, RabbitMqProps rabbitMqProps) {
    this.rabbitTemplate = rabbitTemplate;
    this.rabbitMqProps = rabbitMqProps;
  }

  public void send(MessageCreatedEvent message) {
    String correlationId =
        message.getId() != null ? String.valueOf(message.getId()) : UUID.randomUUID().toString();
    rabbitTemplate.convertAndSend(
        rabbitMqProps.getMessageExchange(),
        rabbitMqProps.getMessageCreatedRoutingKey(),
        message,
        new CorrelationData(correlationId));
  }
}
