package com.carlosarroyoam.service.rabbitmq.messages;

import lombok.Data;

@Data
public class Message {
  private Integer id;
  private String content;
}
