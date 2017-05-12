package io.cad.client.anypoint.mq.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cad.client.anypoint.mq.api.AnypointConfig;
import io.cad.client.anypoint.mq.api.MqClient;
import io.cad.client.anypoint.mq.api.MqClientActivator;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class MqClientActivatorImpl<T> implements MqClientActivator<T> {

    private static final String APPLICATION_JSON_UTF8_VALUE = "application/json; charset=utf-8";

    private MqClient mqClient;
    private ObjectMapper objectMapper;
    private AnypointConfig anypointProperties;

    public MqClientActivatorImpl(AnypointConfig anypointProperties, ObjectMapper objectMapper) {
        // Set up logging interceptor
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.valueOf(anypointProperties.getLoggingLevel()));
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(anypointProperties.getServerUri())
                .addConverterFactory(JacksonConverterFactory.create())
                .client(client)
                .build();

        this.mqClient = retrofit.create(MqClient.class);
        this.anypointProperties = anypointProperties;
        this.objectMapper = objectMapper;
    }

    @Override
    public void publishMessage(T payload) throws AnypointException {
        MqMessage message;
        try {
            message = new MqMessage(new HashMap<>(), new HashMap<>(), objectMapper.writeValueAsString(payload));
            message.getHeaders().put("contentType", APPLICATION_JSON_UTF8_VALUE);
        } catch (JsonProcessingException e) {
            throw new AnypointException("Failed to serialize payload.", e);
        }

        AuthResponse response = getAuthResponse();

        Call<MqStatusResponse> putMessageCall = mqClient.putMessage(
                "bearer " + response.getAccessToken(),
                response.getSimpleClient().getOrgId(),
                response.getSimpleClient().getEnvId(),
                anypointProperties.getPublishDestination(),
                String.valueOf(UUID.randomUUID()),
                message
        );

        Response<MqStatusResponse> mqResponse;
        try {
            mqResponse = putMessageCall.execute();
        } catch (IOException e) {
            throw new AnypointException("Failed to put message.", e);
        }

        if (!mqResponse.isSuccessful()) {
            throw new AnypointException("Failed to put message, response was not successful");
        }
    }

    @Override
    public void publishMessages(List<T> payloads) throws AnypointException {
        List<MqMessage> messages = new ArrayList<>();

        try {
            for (T payload : payloads) {
                MqMessage message = new MqMessage(new HashMap<>(), new HashMap<>(), objectMapper.writeValueAsString(payload));
                message.getHeaders().put("messageId", String.valueOf(UUID.randomUUID()));
                message.getHeaders().put("contentType", APPLICATION_JSON_UTF8_VALUE);
                message.getProperties().put("className", payload.getClass().getName());
                messages.add(message);
            }
        } catch (JsonProcessingException e) {
            throw new AnypointException("Failed to serialize payload.", e);
        }

        AuthResponse response = getAuthResponse();

        Call<List<MqStatusResponse>> putAllMessagesCall = mqClient.putAllMessages(
                "bearer " + response.getAccessToken(),
                response.getSimpleClient().getOrgId(),
                response.getSimpleClient().getEnvId(),
                anypointProperties.getPublishDestination(),
                messages
        );

        Response<List<MqStatusResponse>> mqResponse;
        try {
            mqResponse = putAllMessagesCall.execute();
        } catch (IOException e) {
            throw new AnypointException("Failed to put messages.", e);
        }

        if (!mqResponse.isSuccessful()) {
            throw new AnypointException("Failed to put messages");
        }
    }

    @Override
    public T getMessage() throws AnypointException {
        List<T> messages = getMessages(1, MqClient.DEFAULT_POOLING_TIME, MqClient.DEFAULT_LOCK_TTL);
        if (messages.size() == 0) {
            return null;
        }

        return messages.iterator().next();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<T> getMessages(Integer batchSize, Integer poolingTime, Integer ttl) throws AnypointException {
        AuthResponse response = getAuthResponse();

        Call<List<MqMessage>> getMessagesCall = mqClient.getMessages(
                "bearer " + response.getAccessToken(),
                response.getSimpleClient().getOrgId(),
                response.getSimpleClient().getEnvId(),
                anypointProperties.getSubscribeDestination(),
                batchSize,
                poolingTime,
                ttl
        );

        Response<List<MqMessage>> getMessagesResponse;
        try {
            getMessagesResponse = getMessagesCall.execute();
        } catch (IOException e) {
            throw new AnypointException("Failed to get messages.", e);
        }

        List<T> messages = new ArrayList<>();
        try {
            if (getMessagesResponse.isSuccessful()) {
                for (MqMessage mqMessage : getMessagesResponse.body()) {
                    String jsonBody = mqMessage.getBody();
                    String jsonClass = mqMessage.getProperties().get("className");
                    T message = (T) objectMapper.readValue(jsonBody, Class.forName(jsonClass));
                    messages.add(message);
                }
            }
        } catch (Throwable t) {
            throw new AnypointException("Failed to get messages.", t);
        }

        if (messages.size() == 0) {
            return messages;
        }

        // Ack the messages
        List<MqMessageReference> refs = getMessagesResponse.body()
                .stream()
                .map(MqMessageReference::of)
                .collect(Collectors.toList());

        Call<List<MqStatusResponse>> deleteMessagesCall = mqClient.deleteMessages(
                "bearer " + response.getAccessToken(),
                response.getSimpleClient().getOrgId(),
                response.getSimpleClient().getEnvId(),
                anypointProperties.getSubscribeDestination(),
                refs);

        Response<List<MqStatusResponse>> deleteResponse;
        try {
            deleteResponse = deleteMessagesCall.execute();
        } catch (IOException e) {
            throw new AnypointException("Failed to ACK messages.", e);
        }

        if (!deleteResponse.isSuccessful()) {
            // TODO: delete locks?
            throw new AnypointException("Failed to ACK messages.");
        }

        return messages;
    }

    private AuthResponse getAuthResponse() throws AnypointException {
        Call<AuthResponse> authorizationCall = mqClient
                .getAuthorization(anypointProperties.getGrantType(), anypointProperties.getClientId(), anypointProperties.getClientSecret());

        Response<AuthResponse> response;
        try {
            response = authorizationCall.execute();
        } catch (IOException e) {
            throw new AnypointException("Failed to authorize.", e);
        }

        if (!response.isSuccessful()) {
            throw new AnypointException("Failed to get auth response");
        }

        return response.body();
    }

}
