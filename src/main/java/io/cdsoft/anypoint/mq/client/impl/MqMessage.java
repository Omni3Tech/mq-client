package io.cdsoft.anypoint.mq.client.impl;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MqMessage {
    private Map<String, String> headers;
    private Map<String, String> properties;
    private String body;
}
