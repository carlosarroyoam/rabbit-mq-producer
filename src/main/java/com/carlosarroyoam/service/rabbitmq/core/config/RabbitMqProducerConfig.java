package com.carlosarroyoam.service.rabbitmq.core.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class RabbitMqProducerConfig {
  @Bean
  Queue messageCreatedQueue(RabbitMqProps rabbitMqProps) {
    return QueueBuilder.durable(rabbitMqProps.getMessageCreatedQueue())
        .deadLetterExchange(rabbitMqProps.getMessageDeadLetterExchange())
        .deadLetterRoutingKey(rabbitMqProps.getMessageCreatedDeadLetterRoutingKey())
        .build();
  }

  @Bean
  Queue messageCreatedDeadLetterQueue(RabbitMqProps rabbitMqProps) {
    return QueueBuilder.durable(rabbitMqProps.getMessageCreatedDeadLetterQueue()).build();
  }

  @Bean
  DirectExchange messageExchange(RabbitMqProps rabbitMqProps) {
    return new DirectExchange(rabbitMqProps.getMessageExchange(), true, false);
  }

  @Bean
  DirectExchange messageDeadLetterExchange(RabbitMqProps rabbitMqProps) {
    return new DirectExchange(rabbitMqProps.getMessageDeadLetterExchange(), true, false);
  }

  @Bean
  Binding messageCreatedBinding(
      @Qualifier("messageCreatedQueue") Queue queue,
      @Qualifier("messageExchange") DirectExchange exchange,
      RabbitMqProps rabbitMqProps) {
    return BindingBuilder.bind(queue)
        .to(exchange)
        .with(rabbitMqProps.getMessageCreatedRoutingKey());
  }

  @Bean
  Binding messageCreatedDeadLetterBinding(
      @Qualifier("messageCreatedDeadLetterQueue") Queue queue,
      @Qualifier("messageDeadLetterExchange") DirectExchange exchange,
      RabbitMqProps rabbitMqProps) {
    return BindingBuilder.bind(queue)
        .to(exchange)
        .with(rabbitMqProps.getMessageCreatedDeadLetterRoutingKey());
  }

  @Bean
  RabbitTemplate rabbitTemplate(
      ConnectionFactory connectionFactory, Jackson2JsonMessageConverter messageConverter) {
    RabbitTemplate template = new RabbitTemplate(connectionFactory);
    template.setMessageConverter(messageConverter);
    template.setMandatory(true);
    template.setConfirmCallback(
        (correlationData, ack, cause) -> {
          String id = correlationData != null ? correlationData.getId() : null;
          if (ack) {
            log.info("Message confirmed by broker, correlationId={}", id);
          } else {
            log.error("Message rejected by broker, correlationId={}, cause={}", id, cause);
          }
        });
    template.setReturnsCallback(
        returned ->
            log.error(
                "Message returned by broker, exchange={}, routingKey={}, replyCode={}, replyText={}, body={}",
                returned.getExchange(),
                returned.getRoutingKey(),
                returned.getReplyCode(),
                returned.getReplyText(),
                new String(returned.getMessage().getBody())));
    return template;
  }

  @Bean
  Jackson2JsonMessageConverter messageConverter() {
    return new Jackson2JsonMessageConverter();
  }
}
