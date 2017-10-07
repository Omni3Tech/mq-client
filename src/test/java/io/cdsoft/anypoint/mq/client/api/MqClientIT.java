package io.cdsoft.anypoint.mq.client.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cdsoft.anypoint.mq.client.impl.AnypointException;
import io.cdsoft.anypoint.mq.client.impl.MqClientActivatorImpl;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j class MqClientIT {

    @Data class TestConfig implements AnypointConfig {
        private String clientId = System.getenv("CLIENT_ID");
        private String clientSecret = System.getenv("CLIENT_SECRET");
        private String serverUri = "https://mq-us-east-1.anypoint.mulesoft.com";
        private String publishDestination = "mq.client.test";
        private String subscribeDestination = "mq.client.test";
        private String loggingLevel = "BODY";
    }

    private MqClientActivator<String> client;

    @BeforeEach
    void setUp() throws AnypointException {
        client = new MqClientActivatorImpl<>(new TestConfig(), new ObjectMapper(), String.class);
        log.info("Verifying the test queue is empty");
        assertNull(client.getMessage());
    }

    @Test
    void givenMqClient_thenPublishMessage_verifyMessagePublished() throws AnypointException {
        String message = "givenMqClient_thenPublishMessage_verifyMessagePublished";
        client.publishMessage(message);
        assertEquals(message, client.getMessage());
        assertNull(client.getMessage());
    }

    @Test
    void givenMqClient_thenPublishFiveMessages_verifyFiveMessagesPublished() throws AnypointException {
        String message = "givenMqClient_thenPublishFiveMessages_verifyFiveMessagesPublished";
        for (Integer i = 0; i < 5; i++) {
            client.publishMessage(message + "_" + i);
        }

        List<String> messages = new ArrayList<>();
        String received = client.getMessage();
        while (received != null) {
            messages.add(received);
            received = client.getMessage();
        }

        assertEquals(5, messages.size());
        for (Integer i = 0; i < 5; i++) {
            assertTrue(messages.contains(message + "_" + i));
        }
    }

}