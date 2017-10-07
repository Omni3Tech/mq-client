package io.cdsoft.anypoint.mq.client.impl;

import lombok.Builder;
import lombok.Getter;
import lombok.experimental.Tolerate;

@Getter
@Builder
public class MqStatusResponse {
    private String messageId;
    private String status;
    private String statusMessage;

    @Tolerate
    public MqStatusResponse() {
    }
}
