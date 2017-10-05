package io.cdsoft.anypoint.mq.client.impl;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * JSON payload
 *
 * {
 * "access_token": "0a61735b-c27f-465d-86f6-5e5a16aaa046",
 * "simple_client": {
 * "envId": "495f052a-ab5d-4d1d-b4e0-2abcb8894ac9",
 * "orgId": "61f57b73-76ea-4550-b28f-01395ce72da0"
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
    public class Client {
        private String envId;
        private String orgId;
    }
}
