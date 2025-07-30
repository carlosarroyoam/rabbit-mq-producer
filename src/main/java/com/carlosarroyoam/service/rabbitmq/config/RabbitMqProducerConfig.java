package com.carlosarroyoam.service.rabbitmq.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqProducerConfig {
  public static final String MESSAGES_EXCHANGE_NAME = "messages.exchange";
  public static final String MESSAGES_CREATED_QUEUE_NAME = "messages-created-queue";
  public static final String MESSAGES_CREATED_ROUTING_KEY = "messages.created.key";

  @Bean
  Queue queue() {
    return new Queue(MESSAGES_CREATED_QUEUE_NAME, true);
  }

  @Bean
  RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
      Jackson2JsonMessageConverter messageConverter) {
    RabbitTemplate template = new RabbitTemplate(connectionFactory);
    template.setMessageConverter(messageConverter);
    return template;
  }

  @Bean
  Jackson2JsonMessageConverter messageConverter() {
    return new Jackson2JsonMessageConverter();
  }
}
