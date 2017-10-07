package io.cdsoft.anypoint.mq.client.impl;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * JSON payload
 * <p>
 * {
 * "access_token": [UUID],
 * "simple_client": {
 * "envId": [UUID],
 * "orgId": [UUID]
 * },
 * "token_type": "bearer"
 * }
 */
@Data
public class AuthResponse {
    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("simple_client")
    private Client simpleClient;

    @JsonProperty("token_type")
    private String tokenType;

    @Data
    @AllArgsConstructor
    public static class Client {
        private String envId;
        private String orgId;
    }
}
