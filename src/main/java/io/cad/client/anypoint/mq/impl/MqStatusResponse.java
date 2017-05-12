package io.cad.client.anypoint.mq.impl;

import lombok.Data;

@Data
public class MqStatusResponse {
    private String messageId;
    private String status;
    private String statusMessage;
}
