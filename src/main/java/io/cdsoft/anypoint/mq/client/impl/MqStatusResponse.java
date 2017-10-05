package io.cdsoft.anypoint.mq.client.impl;

import lombok.Data;

@Data
public class MqStatusResponse {
    private String messageId;
    private String status;
    private String statusMessage;
}
