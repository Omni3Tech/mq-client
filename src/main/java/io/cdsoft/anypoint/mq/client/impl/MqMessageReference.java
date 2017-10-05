package io.cdsoft.anypoint.mq.client.impl;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MqMessageReference {
    private String messageId;
    private String lockId;
    private Integer ttl;

    public static MqMessageReference of(MqMessage message) {
        MqMessageReference ref = MqMessageReference.builder()
                .messageId(message.getHeaders().get("messageId"))
                .lockId(message.getHeaders().get("lockId"))
                .build();
        return ref;
    }
}
